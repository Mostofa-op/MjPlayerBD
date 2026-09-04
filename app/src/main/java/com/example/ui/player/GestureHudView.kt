package com.example.ui.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LiquidAmber
import com.example.ui.theme.LiquidCyan
import com.example.ui.theme.LiquidDarkObsidian
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.LiquidGlassSurface
import com.example.ui.theme.LiquidNeonPurple
import com.example.ui.theme.LiquidTextPrimary
import com.example.ui.theme.LiquidVividPink

@Composable
fun GestureHudView(
    gestureState: GestureState,
    isLocked: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Locked indicator
        if (isLocked) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(LiquidGlassSurface.copy(alpha = 0.85f))
                    .border(1.dp, LiquidGlassBorder, CircleShape)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Screen Locked",
                        tint = LiquidAmber,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "TOUCH LOCKED",
                        color = LiquidTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Volume HUD
        AnimatedVisibility(
            visible = gestureState.activeType == ActiveGestureType.VOLUME,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            val isSuperBoost = gestureState.volumeValue > 100
            val volumeRatio = (gestureState.volumeValue / 200f).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xCC090E1A))
                    .border(
                        1.5.dp,
                        if (isSuperBoost) LiquidVividPink else LiquidNeonPurple,
                        RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (gestureState.volumeValue == 0) Icons.Default.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Volume",
                        tint = if (isSuperBoost) LiquidVividPink else LiquidNeonPurple,
                        modifier = Modifier.size(38.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${gestureState.volumeValue}%",
                            color = LiquidTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        if (isSuperBoost) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(LiquidVividPink)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "SUPER-BOOST",
                                    color = LiquidDarkObsidian,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x33FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(volumeRatio)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = if (isSuperBoost) {
                                            listOf(LiquidNeonPurple, LiquidVividPink)
                                        } else {
                                            listOf(LiquidCyan, LiquidNeonPurple)
                                        }
                                    )
                                )
                        )
                    }
                }
            }
        }

        // Brightness HUD
        AnimatedVisibility(
            visible = gestureState.activeType == ActiveGestureType.BRIGHTNESS,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            val brightnessRatio = (gestureState.brightnessValue / 100f).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xCC090E1A))
                    .border(1.5.dp, LiquidCyan, RoundedCornerShape(24.dp))
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.BrightnessMedium,
                        contentDescription = "Brightness",
                        tint = LiquidCyan,
                        modifier = Modifier.size(38.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${gestureState.brightnessValue}%",
                        color = LiquidTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .width(140.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x33FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(brightnessRatio)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(LiquidCyan, Color(0xFF38BDF8))
                                    )
                                )
                        )
                    }
                }
            }
        }

        // Seek HUD
        AnimatedVisibility(
            visible = gestureState.activeType == ActiveGestureType.SEEK,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            val isForward = gestureState.seekDeltaMs >= 0
            val deltaSeconds = (gestureState.seekDeltaMs / 1000)
            val sign = if (isForward) "+" else ""

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xEE090E1A))
                    .border(1.5.dp, LiquidCyan, RoundedCornerShape(20.dp))
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isForward) Icons.Default.FastForward else Icons.Default.FastRewind,
                        contentDescription = null,
                        tint = LiquidCyan,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "$sign${deltaSeconds}s",
                            color = LiquidCyan,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatMs(gestureState.seekTargetMs),
                            color = LiquidTextPrimary,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Double tap indicators
        AnimatedVisibility(
            visible = gestureState.activeType == ActiveGestureType.DOUBLE_TAP_LEFT,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 48.dp)
                    .clip(CircleShape)
                    .background(Color(0x9900F5D4))
                    .padding(18.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Rewind",
                        tint = LiquidDarkObsidian,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "-10s",
                        color = LiquidDarkObsidian,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = gestureState.activeType == ActiveGestureType.DOUBLE_TAP_RIGHT,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 48.dp)
                    .clip(CircleShape)
                    .background(Color(0x9900F5D4))
                    .padding(18.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Forward",
                        tint = LiquidDarkObsidian,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "+10s",
                        color = LiquidDarkObsidian,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Turbo Speed HUD
        AnimatedVisibility(
            visible = gestureState.activeType == ActiveGestureType.TURBO_SPEED,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color(0xEE090E1A))
                    .border(1.5.dp, LiquidAmber, RoundedCornerShape(30.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Turbo",
                        tint = LiquidAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "2.0x TURBO SPEED",
                        color = LiquidAmber,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

fun formatMs(ms: Long): String {
    if (ms <= 0L) return "00:00"
    val totalSecs = ms / 1000
    val hours = totalSecs / 3600
    val mins = (totalSecs % 3600) / 60
    val secs = totalSecs % 60
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, mins, secs)
    } else {
        String.format("%02d:%02d", mins, secs)
    }
}
