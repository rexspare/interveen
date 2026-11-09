package com.interveen.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val InterveenColorScheme = darkColorScheme(
    primary = Emerald,
    onPrimary = AccentInk,
    primaryContainer = EmeraldSoft,
    onPrimaryContainer = CrispWhite,
    secondary = QuietSlate,
    onSecondary = AccentInk,
    background = SlateCharcoal,
    onBackground = CrispWhite,
    surface = CoolGray,
    onSurface = CrispWhite,
    surfaceVariant = CoolGrayRaised,
    onSurfaceVariant = SoftMist,
    outline = Rule,
    outlineVariant = Rule,
)

@Composable
fun InterveenTheme(content: @Composable () -> Unit) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }
    MaterialTheme(
        colorScheme = InterveenColorScheme,
        typography = InterveenTypography,
        shapes = InterveenShapes,
        content = content,
    )
}
