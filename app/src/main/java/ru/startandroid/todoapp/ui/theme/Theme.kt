package ru.startandroid.todoapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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

    val view = LocalView.current
    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        WindowCompat.setDecorFitsSystemWindows(window, false) // To make insets work
        val insetsController = WindowCompat.getInsetsController(window, view)

        window.statusBarColor = Color.Transparent.toArgb()
        insetsController.isAppearanceLightStatusBars = !darkTheme
        window.navigationBarColor = Color.Transparent.toArgb()
        insetsController.isAppearanceLightNavigationBars = !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = mulishTypography(),
        content = content
    )
}