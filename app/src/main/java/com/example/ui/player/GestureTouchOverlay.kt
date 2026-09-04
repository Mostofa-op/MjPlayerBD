package com.example.ui.player

import android.app.Activity
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import com.example.player.LiquidPlayerManager
import kotlin.math.abs

enum class ActiveGestureType {
    NONE,
    VOLUME,
    BRIGHTNESS,
    SEEK,
    DOUBLE_TAP_LEFT,
    DOUBLE_TAP_RIGHT,
    TURBO_SPEED
}

data class GestureState(
    val activeType: ActiveGestureType = ActiveGestureType.NONE,
    val volumeValue: Int = 100,
    val brightnessValue: Int = 75,
    val seekDeltaMs: Long = 0L,
    val seekTargetMs: Long = 0L
)

@Composable
fun GestureTouchOverlay(
    playerManager: LiquidPlayerManager,
    modifier: Modifier = Modifier,
    onGestureChange: (GestureState) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val uiState = playerManager.uiState.value

    var startX by remember { mutableFloatStateOf(0f) }
    var startY by remember { mutableFloatStateOf(0f) }
    var initialSeekPos by remember { mutableLongStateOf(0L) }
    var gestureType by remember { mutableStateOf(ActiveGestureType.NONE) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(uiState.isLocked) {
                if (uiState.isLocked) {
                    detectTapGestures(
                        onTap = {
                            playerManager.toggleControlsVisibility()
                        }
                    )
                } else {
                    detectTapGestures(
                        onTap = {
                            playerManager.toggleControlsVisibility()
                        },
                        onDoubleTap = { offset ->
                            val screenWidth = size.width
                            when {
                                offset.x < screenWidth * 0.35f -> {
                                    playerManager.seekRelative(-10_000)
                                    onGestureChange(GestureState(activeType = ActiveGestureType.DOUBLE_TAP_LEFT))
                                }
                                offset.x > screenWidth * 0.65f -> {
                                    playerManager.seekRelative(10_000)
                                    onGestureChange(GestureState(activeType = ActiveGestureType.DOUBLE_TAP_RIGHT))
                                }
                                else -> {
                                    playerManager.togglePlayPause()
                                }
                            }
                        },
                        onLongPress = {
                            playerManager.setPlaybackSpeed(2.0f)
                            onGestureChange(GestureState(activeType = ActiveGestureType.TURBO_SPEED))
                        },
                        onPress = {
                            val released = tryAwaitRelease()
                            if (released && gestureType == ActiveGestureType.TURBO_SPEED) {
                                playerManager.setPlaybackSpeed(1.0f)
                                onGestureChange(GestureState(activeType = ActiveGestureType.NONE))
                            }
                        }
                    )
                }
            }
            .pointerInput(uiState.isLocked) {
                if (!uiState.isLocked) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            startX = offset.x
                            startY = offset.y
                            initialSeekPos = playerManager.uiState.value.currentPositionMs
                            gestureType = ActiveGestureType.NONE
                        },
                        onDragEnd = {
                            if (gestureType == ActiveGestureType.SEEK) {
                                onGestureChange(GestureState(activeType = ActiveGestureType.NONE))
                            } else {
                                onGestureChange(GestureState(activeType = ActiveGestureType.NONE))
                            }
                            gestureType = ActiveGestureType.NONE
                        },
                        onDragCancel = {
                            onGestureChange(GestureState(activeType = ActiveGestureType.NONE))
                            gestureType = ActiveGestureType.NONE
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val totalDeltaX = change.position.x - startX
                            val totalDeltaY = change.position.y - startY
                            val screenWidth = size.width

                            if (gestureType == ActiveGestureType.NONE) {
                                if (abs(totalDeltaX) > abs(totalDeltaY) && abs(totalDeltaX) > 30) {
                                    gestureType = ActiveGestureType.SEEK
                                } else if (abs(totalDeltaY) > abs(totalDeltaX) && abs(totalDeltaY) > 30) {
                                    gestureType = if (startX < screenWidth * 0.5f) {
                                        ActiveGestureType.BRIGHTNESS
                                    } else {
                                        ActiveGestureType.VOLUME
                                    }
                                }
                            }

                            when (gestureType) {
                                ActiveGestureType.VOLUME -> {
                                    val deltaPercent = (-dragAmount.y / 8f).toInt()
                                    playerManager.adjustVolumeBy(deltaPercent)
                                    onGestureChange(
                                        GestureState(
                                            activeType = ActiveGestureType.VOLUME,
                                            volumeValue = playerManager.uiState.value.volumePercent
                                        )
                                    )
                                }
                                ActiveGestureType.BRIGHTNESS -> {
                                    val deltaPercent = (-dragAmount.y / 10f).toInt()
                                    playerManager.adjustBrightnessBy(activity, deltaPercent)
                                    onGestureChange(
                                        GestureState(
                                            activeType = ActiveGestureType.BRIGHTNESS,
                                            brightnessValue = playerManager.uiState.value.brightnessPercent
                                        )
                                    )
                                }
                                ActiveGestureType.SEEK -> {
                                    val duration = playerManager.uiState.value.durationMs.coerceAtLeast(60_000L)
                                    val deltaSeconds = (totalDeltaX / screenWidth * 120f).toLong()
                                    val deltaMs = deltaSeconds * 1000L
                                    val targetMs = (initialSeekPos + deltaMs).coerceIn(0L, duration)
                                    playerManager.seekTo(targetMs)
                                    onGestureChange(
                                        GestureState(
                                            activeType = ActiveGestureType.SEEK,
                                            seekDeltaMs = deltaMs,
                                            seekTargetMs = targetMs
                                        )
                                    )
                                }
                                else -> {}
                            }
                        }
                    )
                }
            }
    )
}
