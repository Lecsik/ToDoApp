package ru.startandroid.todoapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = lightPrimary40,
    secondary = lightSecondary40,
    tertiary = lightTertiary40,
    error = lightError40,

    primaryContainer = lightPrimaryContainer90,
    secondaryContainer = lightSecondaryContainer90,
    tertiaryContainer = lightTertiaryContainer90,
    errorContainer = lightErrorContainer90,

    background = lightBackgroundSurface98,
    surface = lightBackgroundSurface98
)

private val DarkColorScheme = darkColorScheme(
    primary = darkPrimary80,
    secondary = darkSecondary80,
    tertiary = darkTertiary80,
    error = darkError80,

    primaryContainer = darkPrimaryContainer30,
    secondaryContainer = darkSecondaryContainer30,
    tertiaryContainer = darkTertiaryContainer30,
    errorContainer = darkErrorContainer30,

    background = darkBackgroundSurface6,
    surface = darkBackgroundSurface6
)

@Composable
fun MyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}