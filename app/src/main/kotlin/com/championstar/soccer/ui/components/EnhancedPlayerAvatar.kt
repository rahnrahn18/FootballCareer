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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI

/**
 * A highly detailed (for Canvas) human avatar generator.
 * Supports various skin tones, hair styles, and facial features.
 * Used for the "Human Graphics" requirement without external images.
 */

enum class HairStyle {
    SHORT, SPIKY, LONG, BALD, AFRO
}

@Composable
fun EnhancedPlayerAvatar(
    modifier: Modifier = Modifier,
    skinColor: Color = Color(0xFFFFDAB9), // Default Peach
    hairColor: Color = Color.Black,
    eyeColor: Color = Color(0xFF6B4226), // Brown
    shirtColor: Color = Color.Red,
    shortsColor: Color = Color.White,
    hairStyle: HairStyle = HairStyle.SHORT,
    showKit: Boolean = true // If false, shows portrait only (for menus)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val centerX = w / 2

        // --- Proportions (Human-like) ---
        // Head is roughly 1/7 of total height, but for game avatar we exaggerate slightly (1/6)
        // If portrait mode, scale head up
        val scaleFactor = if (showKit) 1.0f else 2.5f

        val headHeight = h * 0.16f * scaleFactor
        val headWidth = headHeight * 0.75f
        val neckWidth = headWidth * 0.4f
        val neckLength = headHeight * 0.2f

        // Y Positions
        val headTop = if (showKit) h * 0.05f else h * 0.1f
        val neckTop = headTop + headHeight
        val shoulderTop = neckTop + neckLength

        // Body (if showing kit)
        val shoulderWidth = headWidth * 2.2f
        val torsoHeight = headHeight * 1.8f
        val legLength = h * 0.4f

        // --- DRAW BODY (KIT) ---
        if (showKit) {
            // Legs (Skin)
            val legWidth = shoulderWidth * 0.25f
            val legGap = shoulderWidth * 0.1f
            val legY = shoulderTop + torsoHeight

            // Left Leg
            drawRect(
                color = skinColor,
                topLeft = Offset(centerX - legGap/2 - legWidth, legY),
                size = Size(legWidth, legLength)
            )
            // Right Leg
            drawRect(
                color = skinColor,
                topLeft = Offset(centerX + legGap/2, legY),
                size = Size(legWidth, legLength)
            )

            // Arms (Skin)
            val armWidth = shoulderWidth * 0.2f
            val armLength = torsoHeight * 0.9f
            val armY = shoulderTop + torsoHeight * 0.1f

            // Left Arm
            drawRect(
                color = skinColor,
                topLeft = Offset(centerX - shoulderWidth/2 - armWidth*0.8f, armY),
                size = Size(armWidth, armLength)
            )
            // Right Arm
            drawRect(
                color = skinColor,
                topLeft = Offset(centerX + shoulderWidth/2 - armWidth*0.2f, armY),
                size = Size(armWidth, armLength)
            )

            // Shirt (Trapezoid for better shape)
            val shirtPath = Path().apply {
                moveTo(centerX - shoulderWidth/2, shoulderTop)
                lineTo(centerX + shoulderWidth/2, shoulderTop)
                lineTo(centerX + shoulderWidth/2 * 0.8f, shoulderTop + torsoHeight)
                lineTo(centerX - shoulderWidth/2 * 0.8f, shoulderTop + torsoHeight)
                close()
            }
            drawPath(path = shirtPath, color = shirtColor)

            // Shorts
            drawRect(
                color = shortsColor,
                topLeft = Offset(centerX - shoulderWidth/2 * 0.8f, shoulderTop + torsoHeight),
                size = Size(shoulderWidth * 0.8f, torsoHeight * 0.4f)
            )

            // Boots (Generic Black)
            val bootHeight = legLength * 0.15f
            val bootY = shoulderTop + torsoHeight + legLength - bootHeight

            drawRect(color = Color.Black, topLeft = Offset(centerX - legGap/2 - legWidth, bootY), size = Size(legWidth, bootHeight))
            drawRect(color = Color.Black, topLeft = Offset(centerX + legGap/2, bootY), size = Size(legWidth, bootHeight))
        }

        // --- DRAW HEAD & FACE (Detailed) ---

        // Neck
        drawRect(
            color = skinColor,
            topLeft = Offset(centerX - neckWidth/2, neckTop - headHeight*0.1f), // Slight overlap
            size = Size(neckWidth, neckLength + headHeight*0.15f)
        )

        // Face Shape (Oval)
        drawOval(
            color = skinColor,
            topLeft = Offset(centerX - headWidth/2, headTop),
            size = Size(headWidth, headHeight)
        )

        // --- Facial Features ---
        // Scale feature sizes relative to head
        val eyeY = headTop + headHeight * 0.45f
        val eyeSize = headWidth * 0.15f
        val eyeSpacing = headWidth * 0.25f

        // Left Eye (Sclera + Iris)
        drawCircle(color = Color.White, radius = eyeSize/2, center = Offset(centerX - eyeSpacing, eyeY))
        drawCircle(color = eyeColor, radius = eyeSize/4, center = Offset(centerX - eyeSpacing, eyeY))

        // Right Eye
        drawCircle(color = Color.White, radius = eyeSize/2, center = Offset(centerX + eyeSpacing, eyeY))
        drawCircle(color = eyeColor, radius = eyeSize/4, center = Offset(centerX + eyeSpacing, eyeY))

        // Eyebrows
        val browY = eyeY - eyeSize * 0.8f
        val browWidth = eyeSize * 1.5f
        drawLine(
            color = hairColor,
            start = Offset(centerX - eyeSpacing - browWidth/2, browY),
            end = Offset(centerX - eyeSpacing + browWidth/2, browY),
            strokeWidth = headWidth * 0.05f
        )
        drawLine(
            color = hairColor,
            start = Offset(centerX + eyeSpacing - browWidth/2, browY),
            end = Offset(centerX + eyeSpacing + browWidth/2, browY),
            strokeWidth = headWidth * 0.05f
        )

        // Mouth (Simple Smile)
        val mouthY = headTop + headHeight * 0.75f
        val mouthWidth = headWidth * 0.4f
        val mouthHeight = headHeight * 0.1f

        drawArc(
            color = Color(0xFFBC8F8F), // Rosy brown lips
            startAngle = 0f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX - mouthWidth/2, mouthY),
            size = Size(mouthWidth, mouthHeight),
            style = Stroke(width = headWidth * 0.03f)
        )

        // --- HAIR ---
        val hairTop = headTop - headHeight * 0.1f
        val hairSize = Size(headWidth, headHeight * 0.6f)

        when (hairStyle) {
            HairStyle.SHORT -> {
                drawArc(
                    color = hairColor,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(centerX - headWidth/2, hairTop),
                    size = hairSize
                )
            }
            HairStyle.SPIKY -> {
                // Simplified spiky look (Triangle arc)
                val path = Path().apply {
                    moveTo(centerX - headWidth/2, headTop + headHeight * 0.3f)
                    lineTo(centerX, headTop - headHeight * 0.2f) // Spike up
                    lineTo(centerX + headWidth/2, headTop + headHeight * 0.3f)
                    close()
                }
                drawPath(path, color = hairColor)
            }
            HairStyle.LONG -> {
                // Back hair (behind neck)
                drawRect(
                    color = hairColor,
                    topLeft = Offset(centerX - headWidth*0.6f, headTop + headHeight*0.3f),
                    size = Size(headWidth*1.2f, headHeight * 0.8f)
                )
                // Front bangs
                drawArc(
                    color = hairColor,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(centerX - headWidth/2, hairTop),
                    size = Size(headWidth, headHeight * 0.5f)
                )
            }
            HairStyle.AFRO -> {
                 drawCircle(
                     color = hairColor,
                     radius = headWidth * 0.6f,
                     center = Offset(centerX, headTop + headHeight*0.3f)
                 )
            }
            HairStyle.BALD -> {
                // Just skin :)
            }
        }
    }
}
