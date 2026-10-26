package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ZoyaCyan,
    onPrimary = ZoyaBgDark,
    secondary = ZoyaPink,
    onSecondary = ZoyaBgDark,
    tertiary = ZoyaPurple,
    onTertiary = ZoyaTextWhite,
    background = ZoyaBgDark,
    onBackground = ZoyaTextWhite,
    surface = ZoyaSurfaceDark,
    onSurface = ZoyaTextWhite,
    surfaceVariant = ZoyaSurfaceElevated,
    onSurfaceVariant = ZoyaTextMuted,
    outline = ZoyaBorder
)

@Composable
fun ZoyaTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = ZoyaBgDark.toArgb()
                window.navigationBarColor = ZoyaBgDark.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
