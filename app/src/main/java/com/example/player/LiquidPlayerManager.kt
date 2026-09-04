package com.example.player

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.view.WindowManager
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import com.example.ai.AiVideoEngine
import com.example.data.model.VideoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AspectRatioMode(val label: String) {
    FIT("Fit to Screen"),
    FILL_CROP("Zoom / Fill"),
    STRETCH("Stretch 100%"),
    RATIO_16_9("16:9 Widescreen"),
    RATIO_21_9("21:9 Cinema"),
    RATIO_4_3("4:3 Vintage"),
    ORIGINAL("100% Pixel Match")
}

enum class DecoderMode(val label: String) {
    HW("HW"),
    HW_PLUS("HW+"),
    SW("SW")
}

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val bufferedPositionMs: Long = 0L,
    val isBuffering: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val aspectRatioMode: AspectRatioMode = AspectRatioMode.FIT,
    val decoderMode: DecoderMode = DecoderMode.HW_PLUS,
    val volumePercent: Int = 100, // up to 200 with Super-Boost
    val brightnessPercent: Int = 75,
    val isControlsVisible: Boolean = true,
    val isLocked: Boolean = false,
    val isBackgroundPlayEnabled: Boolean = false,
    val isMuted: Boolean = false,
    val videoWidth: Int = 3840,
    val videoHeight: Int = 2160
)

class LiquidPlayerManager(
    private val context: Context,
    val aiEngine: AiVideoEngine
) {
    private var exoPlayer: ExoPlayer? = null
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null
    private var controlsHideJob: Job? = null

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    var activeVideo: VideoItem? = null
        private set

    init {
        initPlayer()
        syncInitialSystemVolumeAndBrightness()
    }

    private fun initPlayer() {
        // High-performance 4K & 8K buffer tuning:
        // 500ms initial buffer for near-zero start latency, up to 60s cache
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs = */ 15_000,
                /* maxBufferMs = */ 60_000,
                /* bufferForPlaybackMs = */ 500,
                /* bufferForPlaybackAfterRebufferMs = */ 2_000
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val renderersFactory = DefaultRenderersFactory(context)
            .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
            .setEnableDecoderFallback(true)

        val player = ExoPlayer.Builder(context, renderersFactory)
            .setLoadControl(loadControl)
            .setSeekBackIncrementMs(10_000)
            .setSeekForwardIncrementMs(10_000)
            .build()

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
                if (isPlaying) startProgressPolling() else stopProgressPolling()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val isBuffering = playbackState == Player.STATE_BUFFERING
                val duration = if (player.duration > 0) player.duration else 0L
                _uiState.value = _uiState.value.copy(
                    isBuffering = isBuffering,
                    durationMs = duration
                )
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                if (videoSize.width > 0 && videoSize.height > 0) {
                    _uiState.value = _uiState.value.copy(
                        videoWidth = videoSize.width,
                        videoHeight = videoSize.height
                    )
                    aiEngine.updateFromFormat(player.videoFormat, activeVideo)
                }
            }
        })

        exoPlayer = player
    }

    fun getPlayer(): ExoPlayer? = exoPlayer

    fun playVideo(video: VideoItem, startPositionMs: Long = 0L) {
        activeVideo = video
        val player = exoPlayer ?: return

        val mediaItem = MediaItem.fromUri(Uri.parse(video.uriString))
        player.setMediaItem(mediaItem)
        player.prepare()
        if (startPositionMs > 0L) {
            player.seekTo(startPositionMs)
        }
        player.playWhenReady = true
        aiEngine.updateFromFormat(player.videoFormat, video)

        _uiState.value = _uiState.value.copy(
            currentPositionMs = startPositionMs,
            durationMs = video.durationMs,
            isPlaying = true
        )
        resetControlsTimeout()
    }

    fun togglePlayPause() {
        val player = exoPlayer ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
            resetControlsTimeout()
        }
    }

    fun seekTo(positionMs: Long) {
        val player = exoPlayer ?: return
        val validPos = positionMs.coerceIn(0L, player.duration.coerceAtLeast(0L))
        player.seekTo(validPos)
        _uiState.value = _uiState.value.copy(currentPositionMs = validPos)
        resetControlsTimeout()
    }

    fun seekRelative(deltaMs: Long) {
        val player = exoPlayer ?: return
        val target = (player.currentPosition + deltaMs).coerceIn(0L, player.duration.coerceAtLeast(0L))
        seekTo(target)
    }

    fun setPlaybackSpeed(speed: Float) {
        val player = exoPlayer ?: return
        player.playbackParameters = PlaybackParameters(speed, 1.0f)
        _uiState.value = _uiState.value.copy(playbackSpeed = speed)
    }

    fun cycleAspectRatio() {
        val current = _uiState.value.aspectRatioMode
        val next = when (current) {
            AspectRatioMode.FIT -> AspectRatioMode.FILL_CROP
            AspectRatioMode.FILL_CROP -> AspectRatioMode.STRETCH
            AspectRatioMode.STRETCH -> AspectRatioMode.RATIO_16_9
            AspectRatioMode.RATIO_16_9 -> AspectRatioMode.RATIO_21_9
            AspectRatioMode.RATIO_21_9 -> AspectRatioMode.RATIO_4_3
            AspectRatioMode.RATIO_4_3 -> AspectRatioMode.ORIGINAL
            AspectRatioMode.ORIGINAL -> AspectRatioMode.FIT
        }
        _uiState.value = _uiState.value.copy(aspectRatioMode = next)
        resetControlsTimeout()
    }

    fun setAspectRatio(mode: AspectRatioMode) {
        _uiState.value = _uiState.value.copy(aspectRatioMode = mode)
    }

    fun cycleDecoderMode() {
        val current = _uiState.value.decoderMode
        val next = when (current) {
            DecoderMode.HW -> DecoderMode.HW_PLUS
            DecoderMode.HW_PLUS -> DecoderMode.SW
            DecoderMode.SW -> DecoderMode.HW
        }
        _uiState.value = _uiState.value.copy(decoderMode = next)
        resetControlsTimeout()
    }

    // Audio Boost up to 200% (MX Player style!)
    fun adjustVolumeBy(deltaPercent: Int) {
        val current = _uiState.value.volumePercent
        val target = (current + deltaPercent).coerceIn(0, 200)
        setVolumePercent(target)
    }

    fun setVolumePercent(percent: Int) {
        val clamped = percent.coerceIn(0, 200)
        _uiState.value = _uiState.value.copy(volumePercent = clamped)

        val player = exoPlayer ?: return
        if (clamped <= 100) {
            // Hardware stream volume
            val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val sysVol = (clamped.toFloat() / 100f * maxVol).toInt().coerceIn(0, maxVol)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, sysVol, 0)
            player.volume = 1.0f
        } else {
            // Super-Boost Volume (+100% to +200%)
            val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, 0)
            // Software gain boost
            val boostMultiplier = 1.0f + ((clamped - 100).toFloat() / 100f) * 1.5f
            player.volume = boostMultiplier
        }
    }

    fun adjustBrightnessBy(activity: Activity?, deltaPercent: Int) {
        val current = _uiState.value.brightnessPercent
        val target = (current + deltaPercent).coerceIn(0, 100)
        setBrightnessPercent(activity, target)
    }

    fun setBrightnessPercent(activity: Activity?, percent: Int) {
        val clamped = percent.coerceIn(0, 100)
        _uiState.value = _uiState.value.copy(brightnessPercent = clamped)
        activity?.let {
            val layoutParams = it.window.attributes
            layoutParams.screenBrightness = (clamped.toFloat() / 100f).coerceIn(0.01f, 1f)
            it.window.attributes = layoutParams
        }
    }

    fun toggleControlsVisibility() {
        if (_uiState.value.isLocked) return
        val current = _uiState.value.isControlsVisible
        _uiState.value = _uiState.value.copy(isControlsVisible = !current)
        if (!current) {
            resetControlsTimeout()
        }
    }

    fun toggleLock() {
        val nextLock = !_uiState.value.isLocked
        _uiState.value = _uiState.value.copy(
            isLocked = nextLock,
            isControlsVisible = !nextLock
        )
    }

    fun toggleMute() {
        val current = _uiState.value.isMuted
        val player = exoPlayer ?: return
        if (current) {
            player.volume = 1.0f
            _uiState.value = _uiState.value.copy(isMuted = false)
        } else {
            player.volume = 0f
            _uiState.value = _uiState.value.copy(isMuted = true)
        }
    }

    fun toggleBackgroundPlay() {
        val cur = _uiState.value.isBackgroundPlayEnabled
        _uiState.value = _uiState.value.copy(isBackgroundPlayEnabled = !cur)
    }

    fun resetControlsTimeout() {
        controlsHideJob?.cancel()
        if (_uiState.value.isLocked) return
        controlsHideJob = coroutineScope.launch {
            delay(4500)
            if (_uiState.value.isPlaying) {
                _uiState.value = _uiState.value.copy(isControlsVisible = false)
            }
        }
    }

    private fun startProgressPolling() {
        progressJob?.cancel()
        progressJob = coroutineScope.launch {
            while (isActive) {
                exoPlayer?.let { player ->
                    _uiState.value = _uiState.value.copy(
                        currentPositionMs = player.currentPosition.coerceAtLeast(0L),
                        durationMs = player.duration.coerceAtLeast(0L),
                        bufferedPositionMs = player.bufferedPosition.coerceAtLeast(0L)
                    )
                }
                delay(300)
            }
        }
    }

    private fun stopProgressPolling() {
        progressJob?.cancel()
    }

    private fun syncInitialSystemVolumeAndBrightness() {
        try {
            val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            val percent = if (maxVol > 0) ((currentVol.toFloat() / maxVol.toFloat()) * 100).toInt() else 80
            _uiState.value = _uiState.value.copy(volumePercent = percent, brightnessPercent = 75)
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(volumePercent = 80, brightnessPercent = 75)
        }
    }

    fun release() {
        stopProgressPolling()
        controlsHideJob?.cancel()
        exoPlayer?.release()
        exoPlayer = null
    }
}
