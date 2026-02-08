package com.championstar.soccer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A "Paper Doll" style character renderer using Canvas.
 * Can be used for custom player avatars.
 */
@Composable
fun PlayerAvatar(
    modifier: Modifier = Modifier,
    skinColor: Color = Color(0xFFFFDAB9), // Peach default
    hairColor: Color = Color.Black,
    shirtColor: Color = Color.Red,
    shortsColor: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val centerX = w / 2
        val centerY = h / 2

        // --- Proportions ---
        val headRadius = w * 0.15f
        val bodyWidth = w * 0.4f
        val bodyHeight = h * 0.35f
        val legWidth = w * 0.12f
        val legLength = h * 0.3f

        // Calculate Y positions
        val headY = h * 0.15f
        val bodyY = headY + headRadius
        val legsY = bodyY + bodyHeight

        // --- Draw Body Parts (Order Matters: Back to Front) ---

        // 1. Legs (Skin)
        drawRect(
            color = skinColor,
            topLeft = Offset(centerX - legWidth * 1.5f, legsY),
            size = Size(legWidth, legLength)
        )
        drawRect(
            color = skinColor,
            topLeft = Offset(centerX + legWidth * 0.5f, legsY),
            size = Size(legWidth, legLength)
        )

        // 2. Arms (Skin) - Simplified stick arms
        val armY = bodyY + bodyHeight * 0.1f
        val armLength = bodyHeight * 0.8f

        // Left Arm
        drawLine(
            color = skinColor,
            start = Offset(centerX - bodyWidth * 0.6f, armY),
            end = Offset(centerX - bodyWidth * 0.8f, armY + armLength),
            strokeWidth = legWidth
        )
        // Right Arm
        drawLine(
            color = skinColor,
            start = Offset(centerX + bodyWidth * 0.6f, armY),
            end = Offset(centerX + bodyWidth * 0.8f, armY + armLength),
            strokeWidth = legWidth
        )

        // 3. Torso (Shirt)
        drawRect(
            color = shirtColor,
            topLeft = Offset(centerX - bodyWidth / 2, bodyY),
            size = Size(bodyWidth, bodyHeight)
        )

        // 4. Shorts
        drawRect(
            color = shortsColor,
            topLeft = Offset(centerX - bodyWidth / 2, legsY),
            size = Size(bodyWidth, bodyHeight * 0.3f)
        )

        // 5. Head (Skin)
        drawCircle(
            color = skinColor,
            radius = headRadius,
            center = Offset(centerX, headY)
        )

        // 6. Hair (Top Half Circle)
        drawArc(
            color = hairColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(centerX - headRadius, headY - headRadius),
            size = Size(headRadius * 2, headRadius * 2)
        )
    }
}
