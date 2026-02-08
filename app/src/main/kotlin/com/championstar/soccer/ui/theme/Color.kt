package com.championstar.soccer.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Gold = Color(0xFFFFD700)
val DarkGreen = Color(0xFF006400)
val PitchGreen = Color(0xFF388E3C)
val SlateBlack = Color(0xFF1E1E1E)
val OffWhite = Color(0xFFF0F0F0)

val DarkColorScheme = darkColorScheme(
    primary = Gold,
    secondary = PitchGreen,
    tertiary = OffWhite,
    background = SlateBlack,
    surface = Color(0xFF2C2C2C),
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
