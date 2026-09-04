package com.example.ui.player

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AiVideoEngine
import com.example.ui.components.LiquidBadge
import com.example.ui.components.LiquidCard
import com.example.ui.theme.LiquidAmber
import com.example.ui.theme.LiquidCyan
import com.example.ui.theme.LiquidDarkObsidian
import com.example.ui.theme.LiquidDeepVoid
import com.example.ui.theme.LiquidEmerald
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.LiquidGlassSurface
import com.example.ui.theme.LiquidNeonPurple
import com.example.ui.theme.LiquidTextMuted
import com.example.ui.theme.LiquidTextPrimary
import com.example.ui.theme.LiquidTextSecondary
import com.example.ui.theme.LiquidVividPink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiDetectorSheet(
    aiEngine: AiVideoEngine,
    onDismiss: () -> Unit
) {
    val telemetry by aiEngine.telemetry.collectAsState()
    val enhancements by aiEngine.enhancements.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xF2070C18),
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
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(LiquidCyan, LiquidNeonPurple)
                                )
                            )
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = LiquidDarkObsidian,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "AI Ultra Detector & Enhancer",
                            color = LiquidTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Real-time 4K / 8K Hardware Analyzer",
                            color = LiquidCyan,
                            fontSize = 12.sp
                        )
                    }
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

            // AI Scene Detection Banner
            LiquidCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x3300F5D4),
                borderColor = LiquidCyan
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = null,
                                tint = LiquidCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AI SCENE CLASSIFIER",
                                color = LiquidCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        LiquidBadge(
                            text = "99.4% MATCH",
                            backgroundColor = Color(0x3300F5D4),
                            textColor = LiquidCyan
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = telemetry.detectedScene,
                        color = LiquidTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hardware pipeline optimized for zero dropped frames and maximum bit-depth clarity.",
                        color = LiquidTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Live Stream Telemetry Grid
            Text(
                text = "LIVE VIDEO TELEMETRY",
                color = LiquidTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TelemetryCard(
                    title = "RESOLUTION",
                    value = telemetry.resolutionLabel,
                    subtext = "${telemetry.actualWidth} × ${telemetry.actualHeight}",
                    accentColor = LiquidCyan,
                    modifier = Modifier.weight(1f)
                )
                TelemetryCard(
                    title = "FRAME PACING",
                    value = "${telemetry.fps} FPS",
                    subtext = "0 Latency • 0 Jitter",
                    accentColor = LiquidEmerald,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TelemetryCard(
                    title = "DYNAMIC RANGE",
                    value = telemetry.colorSpace,
                    subtext = "WCG 10-bit Color Matrix",
                    accentColor = LiquidAmber,
                    modifier = Modifier.weight(1f)
                )
                TelemetryCard(
                    title = "CODEC & BITRATE",
                    value = telemetry.codec,
                    subtext = "${telemetry.bitrateKbps / 1000} Mbps Peak Bitrate",
                    accentColor = LiquidNeonPurple,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // AI Enhancement Engines
            Text(
                text = "AI NEURAL ENHANCERS",
                color = LiquidTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            EnhancerToggleRow(
                icon = Icons.Default.HighQuality,
                title = "AI Crystal Clarity (Edge Sharpening)",
                description = "Enhance micro-textures & edges without oversharpening artifacts",
                enabled = enhancements.crystalClarityEnabled,
                accentColor = LiquidCyan,
                onToggle = { aiEngine.toggleCrystalClarity() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            EnhancerToggleRow(
                icon = Icons.Default.AutoAwesome,
                title = "AI HDR Vivid Engine",
                description = "Expands color vibrance and highlights for lifelike dynamic depth",
                enabled = enhancements.hdrVividEnabled,
                accentColor = LiquidVividPink,
                onToggle = { aiEngine.toggleHdrVivid() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            EnhancerToggleRow(
                icon = Icons.Default.NightlightRound,
                title = "AI Night Vision (Shadow Lifter)",
                description = "Dynamically brightens dark scenes to reveal hidden details",
                enabled = enhancements.nightVisionEnabled,
                accentColor = LiquidAmber,
                onToggle = { aiEngine.toggleNightVision() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            EnhancerToggleRow(
                icon = Icons.Default.RecordVoiceOver,
                title = "AI Dialogue Clarity (Vocal Isolation)",
                description = "Elevates human speech frequencies over background noise and effects",
                enabled = enhancements.vocalBoostEnabled,
                accentColor = LiquidEmerald,
                onToggle = { aiEngine.toggleVocalBoost() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            EnhancerToggleRow(
                icon = Icons.Default.Speed,
                title = "AI Motion Flow (Judder Smoother)",
                description = "Smooth frame pacing for fluid high-speed panning and action sequences",
                enabled = enhancements.motionSmootherEnabled,
                accentColor = LiquidNeonPurple,
                onToggle = { aiEngine.toggleMotionSmoother() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            EnhancerToggleRow(
                icon = Icons.Default.Memory,
                title = "8K Super-Resolution Neural Upscaling",
                description = "Simulate 8K neural detail reconstruction for ultra-fine fidelity",
                enabled = enhancements.superResolution8kSimulation,
                accentColor = LiquidCyan,
                onToggle = { aiEngine.toggleSuperResolution8k() }
            )
        }
    }
}

@Composable
fun TelemetryCard(
    title: String,
    value: String,
    subtext: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x33121C30))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = title,
                color = accentColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = LiquidTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                color = LiquidTextSecondary,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
fun EnhancerToggleRow(
    icon: ImageVector,
    title: String,
    description: String,
    enabled: Boolean,
    accentColor: Color,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x2B131C30))
            .border(
                1.dp,
                if (enabled) accentColor.copy(alpha = 0.5f) else LiquidGlassBorder.copy(alpha = 0.2f),
                RoundedCornerShape(14.dp)
            )
            .clickable { onToggle() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (enabled) accentColor.copy(alpha = 0.2f) else Color(0x22FFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) accentColor else LiquidTextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        color = LiquidTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = description,
                        color = LiquidTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }
            Switch(
                checked = enabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = LiquidDarkObsidian,
                    checkedTrackColor = accentColor,
                    uncheckedThumbColor = LiquidTextSecondary,
                    uncheckedTrackColor = Color(0x33FFFFFF)
                )
            )
        }
    }
}
