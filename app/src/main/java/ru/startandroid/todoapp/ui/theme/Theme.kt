package ru.startandroid.todoapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = lightPrimary,
    secondary = lightSecondary,
    tertiary = lightTertiary,
    error = lightError,

    primaryContainer = lightPrimaryContainer,
    secondaryContainer = lightSecondaryContainer,
    tertiaryContainer = lightTertiaryContainer,
    errorContainer = lightErrorContainer,

    background = lightBackgroundSurface,
    surface = lightBackgroundSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = darkPrimary,
    secondary = darkSecondary,
    tertiary = darkTertiary,
    error = darkError,

    primaryContainer = darkPrimaryContainer,
    secondaryContainer = darkSecondaryContainer,
    tertiaryContainer = darkTertiaryContainer,
    errorContainer = darkErrorContainer,

    background = darkBackgroundSurface,
    surface = darkBackgroundSurface
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