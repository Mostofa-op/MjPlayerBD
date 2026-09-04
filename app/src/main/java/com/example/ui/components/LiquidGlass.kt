package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LiquidCyan
import com.example.ui.theme.LiquidGlassBorder
import com.example.ui.theme.LiquidGlassCard
import com.example.ui.theme.LiquidNeonPurple

fun Modifier.liquidGlassSurface(
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = LiquidGlassCard,
    borderColor: Color = LiquidGlassBorder,
    borderWidth: Dp = 1.dp
): Modifier = this
    .clip(shape)
    .background(backgroundColor)
    .border(borderWidth, borderColor, shape)

@Composable
fun Modifier.liquidAnimatedGlowBorder(
    shape: Shape = RoundedCornerShape(16.dp),
    glowColor1: Color = LiquidCyan,
    glowColor2: Color = LiquidNeonPurple
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "LiquidGlow")
    val offsetProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LiquidGlowProgress"
    )

    return this
        .clip(shape)
        .drawBehind {
            val brush = Brush.linearGradient(
                colors = listOf(
                    glowColor1.copy(alpha = 0.8f * offsetProgress + 0.2f),
                    glowColor2.copy(alpha = 0.8f * (1f - offsetProgress) + 0.2f),
                    glowColor1.copy(alpha = 0.4f)
                ),
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )
            drawRect(brush = brush)
        }
}

@Composable
fun LiquidCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = Color(0x33141E33),
    borderColor: Color = Color(0x3338BDF8),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.7f),
                        borderColor.copy(alpha = 0.15f),
                        borderColor.copy(alpha = 0.4f)
                    )
                ),
                shape = shape
            )
            .padding(16.dp),
        content = content
    )
}

@Composable
fun LiquidBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0x3300F5D4),
    textColor: Color = LiquidCyan,
    borderColor: Color = Color(0x6600F5D4)
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .border(0.8.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
