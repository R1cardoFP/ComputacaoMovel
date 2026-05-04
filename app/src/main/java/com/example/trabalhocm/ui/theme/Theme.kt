package com.example.trabalhocm.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BrandGreen,
    secondary = BrandBlue,
    tertiary = BrandWhite,
    background = BrandBlue,
    surface = BrandBlue,
    onPrimary = BrandWhite,
    onSecondary = BrandWhite,
    onTertiary = BrandBlue,
    onBackground = BrandWhite,
    onSurface = BrandWhite
)

private val LightColorScheme = lightColorScheme(
    primary = BrandGreen,
    secondary = BrandBlue,
    tertiary = BrandWhite,
    background = BrandWhite,
    surface = BrandWhite,
    onPrimary = BrandWhite,
    onSecondary = BrandWhite,
    onTertiary = BrandBlue,
    onBackground = BrandBlue,
    onSurface = BrandBlue
)

@Composable
fun TrabalhoCMTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
