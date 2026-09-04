package com.example.ui.player

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPictureAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.player.DecoderMode
import com.example.player.LiquidPlayerManager
import com.example.ui.components.LiquidBadge
import com.example.ui.theme.LiquidAmber
import com.example.ui.theme.LiquidCyan
import com.example.ui.theme.LiquidDarkObsidian
import com.example.ui.theme.LiquidDeepVoid
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.LiquidNeonPurple
import com.example.ui.theme.LiquidTextMuted
import com.example.ui.theme.LiquidTextPrimary
import com.example.ui.theme.LiquidTextSecondary
import com.example.ui.theme.LiquidVividPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiquidPlayerControlsOverlay(
    playerManager: LiquidPlayerManager,
    gestureState: GestureState,
    onBackClick: () -> Unit,
    onOpenAiSheet: () -> Unit,
    onOpenSettingsSheet: () -> Unit,
    onEnterPiP: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by playerManager.uiState.collectAsState()
    val telemetry by playerManager.aiEngine.telemetry.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    var isDraggingSlider by remember { mutableStateOf(false) }
    var sliderTempPos by remember { mutableFloatStateOf(0f) }

    Box(modifier = modifier.fillMaxSize()) {
        // Always present HUD for gestures
        GestureHudView(
            gestureState = gestureState,
            isLocked = uiState.isLocked,
            modifier = Modifier.fillMaxSize()
        )

        // If locked, show only the unlock button in the top-left
        if (uiState.isLocked) {
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                IconButton(
                    onClick = { playerManager.toggleLock() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xCC000000))
                        .border(1.5.dp, LiquidAmber, CircleShape)
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Unlock Controls",
                        tint = LiquidAmber
                    )
                }
            }
            return@Box
        }

        // Full Controls Animated Visibility
        AnimatedVisibility(
            visible = uiState.isControlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xD9060913),
                                Color(0x22060913),
                                Color(0x22060913),
                                Color(0xF2060913)
                            )
                        )
                    )
            ) {
                // TOP BAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .align(Alignment.TopCenter),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0x401E293B))
                                .border(1.dp, LiquidGlassBorder, CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = LiquidTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = playerManager.activeVideo?.title ?: "Video Player",
                                color = LiquidTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = telemetry.resolutionLabel,
                                    color = LiquidCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = " • ${telemetry.fps} FPS • ${telemetry.colorSpace}",
                                    color = LiquidTextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Top Action Buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Decoder Mode Button (HW / HW+ / SW)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x401E293B))
                                .border(
                                    1.dp,
                                    if (uiState.decoderMode == DecoderMode.HW_PLUS) LiquidCyan else LiquidGlassBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { playerManager.cycleDecoderMode() }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = uiState.decoderMode.label,
                                color = if (uiState.decoderMode == DecoderMode.HW_PLUS) LiquidCyan else LiquidTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // AI Telemetry & Enhancer Badge Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0x3300F5D4), Color(0x337928CA))
                                    )
                                )
                                .border(1.dp, LiquidCyan, RoundedCornerShape(8.dp))
                                .clickable { onOpenAiSheet() }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = LiquidCyan,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AI DETECT",
                                    color = LiquidCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // PiP Button
                        IconButton(
                            onClick = onEnterPiP,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0x401E293B))
                                .border(1.dp, LiquidGlassBorder, CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureInPictureAlt,
                                contentDescription = "Picture-in-Picture",
                                tint = LiquidTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Settings Button
                        IconButton(
                            onClick = onOpenSettingsSheet,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0x401E293B))
                                .border(1.dp, LiquidGlassBorder, CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = LiquidTextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // CENTER PLAY / PAUSE / REWIND / FORWARD CONTROLS
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rewind 10s
                    IconButton(
                        onClick = { playerManager.seekRelative(-10_000) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0x331E293B))
                            .border(1.dp, LiquidGlassBorder, CircleShape)
                            .size(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastRewind,
                            contentDescription = "Rewind 10s",
                            tint = LiquidTextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Main Glass Play/Pause
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(LiquidCyan, LiquidNeonPurple)
                                )
                            )
                            .clickable { playerManager.togglePlayPause() }
                            .size(68.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                            tint = LiquidDarkObsidian,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    // Forward 10s
                    IconButton(
                        onClick = { playerManager.seekRelative(10_000) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0x331E293B))
                            .border(1.dp, LiquidGlassBorder, CircleShape)
                            .size(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = "Forward 10s",
                            tint = LiquidTextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // BOTTOM CONTROLS & SCRUBBER
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    // Time and aspect ratio row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${formatMs(if (isDraggingSlider) sliderTempPos.toLong() else uiState.currentPositionMs)} / ${formatMs(uiState.durationMs)}",
                            color = LiquidTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Aspect Ratio Chip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x401E293B))
                                    .border(1.dp, LiquidGlassBorder, RoundedCornerShape(6.dp))
                                    .clickable { playerManager.cycleAspectRatio() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AspectRatio,
                                        contentDescription = null,
                                        tint = LiquidTextSecondary,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = uiState.aspectRatioMode.label,
                                        color = LiquidTextPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Playback Speed Chip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x401E293B))
                                    .border(1.dp, LiquidGlassBorder, RoundedCornerShape(6.dp))
                                    .clickable {
                                        val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 3.0f)
                                        val curIdx = speeds.indexOf(uiState.playbackSpeed)
                                        val nextSpeed = speeds[(if (curIdx >= 0) curIdx + 1 else 0) % speeds.size]
                                        playerManager.setPlaybackSpeed(nextSpeed)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${uiState.playbackSpeed}x",
                                    color = if (uiState.playbackSpeed != 1.0f) LiquidCyan else LiquidTextPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Liquid Glow Progress Slider
                    val maxProgress = uiState.durationMs.coerceAtLeast(1L).toFloat()
                    val currentSliderVal = if (isDraggingSlider) sliderTempPos else uiState.currentPositionMs.toFloat().coerceIn(0f, maxProgress)

                    Slider(
                        value = currentSliderVal,
                        onValueChange = { newPos ->
                            isDraggingSlider = true
                            sliderTempPos = newPos
                        },
                        onValueChangeFinished = {
                            playerManager.seekTo(sliderTempPos.toLong())
                            isDraggingSlider = false
                        },
                        valueRange = 0f..maxProgress,
                        colors = SliderDefaults.colors(
                            thumbColor = LiquidCyan,
                            activeTrackColor = LiquidCyan,
                            inactiveTrackColor = Color(0x44FFFFFF)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Bottom Utility Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Lock button
                        IconButton(
                            onClick = { playerManager.toggleLock() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0x331E293B))
                                .border(1.dp, LiquidGlassBorder, CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = "Lock Screen",
                                tint = LiquidTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Super-Boost Volume indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (uiState.isMuted) Icons.Default.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Volume",
                                tint = if (uiState.volumePercent > 100) LiquidVividPink else LiquidCyan,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { playerManager.toggleMute() }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${uiState.volumePercent}%",
                                color = if (uiState.volumePercent > 100) LiquidVividPink else LiquidTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Orientation switch
                        IconButton(
                            onClick = {
                                activity?.let {
                                    val currentOrientation = it.requestedOrientation
                                    it.requestedOrientation = if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                    } else {
                                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                    }
                                }
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0x331E293B))
                                .border(1.dp, LiquidGlassBorder, CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ScreenRotation,
                                contentDescription = "Rotate Screen",
                                tint = LiquidTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
