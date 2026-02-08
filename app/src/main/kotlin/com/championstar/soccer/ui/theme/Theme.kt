package com.championstar.soccer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    primary = Gold,
    secondary = PitchGreen,
    tertiary = OffWhite,
    background = SlateBlack,
    surface = SurfaceDark,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = OffWhite,
    onSurface = OffWhite
)

val LightColorScheme = lightColorScheme(
    primary = DarkGreen,
    secondary = Gold,
    tertiary = SlateBlack,
    background = OffWhite,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = SlateBlack,
    onSurface = SlateBlack
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
