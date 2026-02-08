package com.championstar.soccer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * A reusable background simulating a football pitch or stadium ambiance.
 * Uses a gradient and simple lines to create texture.
 */
@Composable
fun PitchBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val pitchGradient = Brush.verticalGradient(
                colors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
            )
            drawRect(pitchGradient)

            // Draw faint pitch lines (stripes)
            val stripeWidth = size.height / 10
            for (i in 0 until 10) {
                if (i % 2 == 0) {
                    drawRect(
                        color = Color(0xFF388E3C).copy(alpha = 0.3f),
                        topLeft = Offset(0f, i * stripeWidth),
                        size = Size(size.width, stripeWidth)
                    )
                }
            }
        }
        content()
    }
}
