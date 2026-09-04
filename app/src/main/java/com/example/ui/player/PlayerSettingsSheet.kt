package com.example.ui.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.player.AspectRatioMode
import com.example.player.DecoderMode
import com.example.player.LiquidPlayerManager
import com.example.ui.theme.LiquidCyan
import com.example.ui.theme.LiquidDarkObsidian
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.LiquidNeonPurple
import com.example.ui.theme.LiquidTextMuted
import com.example.ui.theme.LiquidTextPrimary
import com.example.ui.theme.LiquidTextSecondary
import com.example.ui.theme.LiquidVividPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerSettingsSheet(
    playerManager: LiquidPlayerManager,
    onDismiss: () -> Unit
) {
    val uiState by playerManager.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xF5080E1C),
        scrimColor = Color.Black.copy(alpha = 0.65f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = LiquidCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Player & Audio Settings",
                        color = LiquidTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = LiquidTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Audio Super-Boost (0% - 200%)
            Text(
                text = "AUDIO SUPER-BOOST (0% - 200%)",
                color = LiquidTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x331E293B))
                    .border(1.dp, if (uiState.volumePercent > 100) LiquidVividPink.copy(alpha = 0.5f) else LiquidGlassBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = if (uiState.volumePercent > 100) LiquidVividPink else LiquidCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Current Gain: ${uiState.volumePercent}%",
                                color = LiquidTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (uiState.volumePercent > 100) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(LiquidVividPink)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "200% BOOST ACTIVE",
                                    color = LiquidDarkObsidian,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = uiState.volumePercent.toFloat(),
                        onValueChange = { playerManager.setVolumePercent(it.toInt()) },
                        valueRange = 0f..200f,
                        colors = SliderDefaults.colors(
                            thumbColor = if (uiState.volumePercent > 100) LiquidVividPink else LiquidCyan,
                            activeTrackColor = if (uiState.volumePercent > 100) LiquidVividPink else LiquidCyan,
                            inactiveTrackColor = Color(0x33FFFFFF)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Hardware Decoder Modes
            Text(
                text = "DECODER PIPELINE",
                color = LiquidTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DecoderMode.entries.forEach { mode ->
                    val isSelected = uiState.decoderMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) LiquidCyan.copy(alpha = 0.2f) else Color(0x221E293B))
                            .border(
                                1.dp,
                                if (isSelected) LiquidCyan else LiquidGlassBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                if (uiState.decoderMode != mode) playerManager.cycleDecoderMode()
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = mode.label,
                                color = if (isSelected) LiquidCyan else LiquidTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = when (mode) {
                                    DecoderMode.HW -> "Hardware"
                                    DecoderMode.HW_PLUS -> "Accelerated+"
                                    DecoderMode.SW -> "Software"
                                },
                                color = LiquidTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Playback Speed
            Text(
                text = "PLAYBACK SPEED",
                color = LiquidTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            val speedOptions = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f, 3.0f)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                speedOptions.forEach { speed ->
                    val isSelected = uiState.playbackSpeed == speed
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) LiquidNeonPurple.copy(alpha = 0.3f) else Color(0x221E293B))
                            .border(
                                1.dp,
                                if (isSelected) LiquidNeonPurple else LiquidGlassBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { playerManager.setPlaybackSpeed(speed) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${speed}x",
                            color = if (isSelected) LiquidNeonPurple else LiquidTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Aspect Ratio Modes
            Text(
                text = "SCREEN ASPECT RATIO",
                color = LiquidTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                AspectRatioMode.entries.forEach { mode ->
                    val isSelected = uiState.aspectRatioMode == mode
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) LiquidCyan.copy(alpha = 0.15f) else Color(0x221E293B))
                            .border(
                                1.dp,
                                if (isSelected) LiquidCyan else LiquidGlassBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { playerManager.setAspectRatio(mode) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = mode.label,
                                color = if (isSelected) LiquidCyan else LiquidTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (isSelected) {
                                Text(
                                    text = "ACTIVE",
                                    color = LiquidCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Background Playback Toggle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x221E293B))
                    .border(1.dp, LiquidGlassBorder, RoundedCornerShape(14.dp))
                    .clickable { playerManager.toggleBackgroundPlay() }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = LiquidCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Background Audio Play",
                                color = LiquidTextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Keep playing audio when screen turns off",
                                color = LiquidTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Switch(
                        checked = uiState.isBackgroundPlayEnabled,
                        onCheckedChange = { playerManager.toggleBackgroundPlay() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = LiquidDarkObsidian,
                            checkedTrackColor = LiquidCyan
                        )
                    )
                }
            }
        }
    }
}
