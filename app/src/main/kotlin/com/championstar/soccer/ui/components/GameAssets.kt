package com.championstar.soccer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SentimentDissatisfied
import androidx.compose.material.icons.rounded.SentimentNeutral
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.championstar.soccer.ui.theme.GoldAccent
import com.championstar.soccer.ui.theme.GreenField

enum class Mood {
    HAPPY, NEUTRAL, SAD
}

@Composable
fun MoodIcon(
    mood: Mood,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    val icon: ImageVector = when (mood) {
        Mood.HAPPY -> Icons.Rounded.SentimentSatisfied
        Mood.NEUTRAL -> Icons.Rounded.SentimentNeutral
        Mood.SAD -> Icons.Rounded.SentimentDissatisfied
    }

    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun PlayerMarker(
    modifier: Modifier = Modifier,
    color: Color = GreenField,
    borderColor: Color = GoldAccent,
    size: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(color, CircleShape)
            .border(2.dp, borderColor, CircleShape)
    )
}

@Composable
fun WarningPanel(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.errorContainer,
    borderColor: Color = MaterialTheme.colorScheme.error,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        content()
    }
}
