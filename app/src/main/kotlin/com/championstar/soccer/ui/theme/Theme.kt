package com.championstar.soccer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    primary = NeonGold,
    secondary = ElectricGreen,
    tertiary = BrightBlue,
    background = CarbonBlack,
    surface = SurfaceDark,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = TextWhite,
    onSurface = TextWhite
)

// Light theme is not really used for "Dark Sporty", but kept for safety mapping
val LightColorScheme = lightColorScheme(
    primary = ElectricGreen,
    secondary = NeonGold,
    tertiary = BrightBlue,
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

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
