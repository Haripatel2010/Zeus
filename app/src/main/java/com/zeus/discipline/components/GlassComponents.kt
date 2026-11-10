
package com.zeus.discipline.components

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val GlassWhite = Color.White
private val GlassBlack = Color(0xFF080808)

fun Modifier.liquidGlass(
    cornerRadius: Dp = 26.dp,
    blurRadius: Dp = 24.dp
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .then(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Modifier.graphicsLayer {
                renderEffect = RenderEffect
                    .createBlurEffect(
                        blurRadius.value * density,
                        blurRadius.value * density,
                        Shader.TileMode.CLAMP
                    )
                    .asComposeRenderEffect()
            }
        } else {
            Modifier
        }
    )
    .background(
        Brush.linearGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.17f),
                Color.White.copy(alpha = 0.055f),
                GlassBlack.copy(alpha = 0.30f)
            )
        )
    )
    .border(
        width = 1.dp,
        brush = Brush.linearGradient(
            colors = listOf(
                GlassWhite.copy(alpha = 0.40f),
                GlassWhite.copy(alpha = 0.08f),
                GlassWhite.copy(alpha = 0.20f)
            )
        ),
        shape = RoundedCornerShape(cornerRadius)
    )
    .drawWithContent {
        drawContent()

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.055f),
                    Color.Transparent,
                    Color.Black.copy(alpha = 0.10f)
                )
            )
        )
    }

@androidx.compose.runtime.Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 26.dp,
    blurRadius: Dp = 24.dp,
    content: @androidx.compose.runtime.Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier.liquidGlass(
            cornerRadius = cornerRadius,
            blurRadius = blurRadius
        ),
        content = content
    )
}
