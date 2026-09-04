package com.example.ui.player

import android.app.Activity
import android.app.PictureInPictureParams
import android.os.Build
import android.util.Rational
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.data.model.VideoItem
import com.example.player.AspectRatioMode
import com.example.player.LiquidPlayerManager

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    playerManager: LiquidPlayerManager,
    video: VideoItem,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val uiState by playerManager.uiState.collectAsState()

    var gestureState by remember { mutableStateOf(GestureState()) }
    var showAiSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    DisposableEffect(video) {
        playerManager.playVideo(video, video.lastPositionMs)
        onDispose {
            // Player will be stopped or paused when navigating back
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Media3 ExoPlayer AndroidView
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    useController = false
                    player = playerManager.getPlayer()
                }
            },
            update = { playerView ->
                playerView.player = playerManager.getPlayer()
                playerView.resizeMode = when (uiState.aspectRatioMode) {
                    AspectRatioMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                    AspectRatioMode.FILL_CROP -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    AspectRatioMode.STRETCH -> AspectRatioFrameLayout.RESIZE_MODE_FILL
                    AspectRatioMode.RATIO_16_9 -> AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                    AspectRatioMode.RATIO_21_9 -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    AspectRatioMode.RATIO_4_3 -> AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
                    AspectRatioMode.ORIGINAL -> AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Gesture Touch Detector
        GestureTouchOverlay(
            playerManager = playerManager,
            modifier = Modifier.fillMaxSize(),
            onGestureChange = { newState ->
                gestureState = newState
            }
        )

        // Liquid Lustre Glass Controls Overlay
        LiquidPlayerControlsOverlay(
            playerManager = playerManager,
            gestureState = gestureState,
            onBackClick = onBack,
            onOpenAiSheet = { showAiSheet = true },
            onOpenSettingsSheet = { showSettingsSheet = true },
            onEnterPiP = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && activity != null) {
                    try {
                        val rational = Rational(
                            uiState.videoWidth.coerceAtLeast(16),
                            uiState.videoHeight.coerceAtLeast(9)
                        )
                        val pipParams = PictureInPictureParams.Builder()
                            .setAspectRatio(rational)
                            .build()
                        activity.enterPictureInPictureMode(pipParams)
                    } catch (e: Exception) {
                        // Fallback simple PiP
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            @Suppress("DEPRECATION")
                            activity.enterPictureInPictureMode()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // AI Detector Bottom Sheet
        if (showAiSheet) {
            AiDetectorSheet(
                aiEngine = playerManager.aiEngine,
                onDismiss = { showAiSheet = false }
            )
        }

        // Settings Bottom Sheet
        if (showSettingsSheet) {
            PlayerSettingsSheet(
                playerManager = playerManager,
                onDismiss = { showSettingsSheet = false }
            )
        }
    }
}
