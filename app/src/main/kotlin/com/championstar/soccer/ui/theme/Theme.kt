package com.championstar.soccer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ChampionstarTheme(
    darkTheme: Boolean = true, // Default to dark for gaming
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        // typography = Typography, // Default for now
        content = content
    )
}
