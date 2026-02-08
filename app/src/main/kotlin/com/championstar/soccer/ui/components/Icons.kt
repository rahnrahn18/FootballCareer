package com.championstar.soccer.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.EmojiEvents // For Glory
import androidx.compose.ui.unit.dp

/**
 * Reusable Icon + Text components for currencies.
 */

@Composable
fun StarCurrency(amount: Int) {
    Row(modifier = Modifier.padding(4.dp)) {
        Icon(Icons.Filled.Star, contentDescription = "Stars", tint = Color.Yellow)
        Text(text = "$amount", color = Color.White, modifier = Modifier.padding(start = 4.dp))
    }
}

@Composable
fun GloryCurrency(amount: Int) {
    Row(modifier = Modifier.padding(4.dp)) {
        Icon(Icons.Filled.EmojiEvents, contentDescription = "Glory", tint = Color(0xFFFFD700))
        Text(text = "$amount", color = Color.White, modifier = Modifier.padding(start = 4.dp))
    }
}
