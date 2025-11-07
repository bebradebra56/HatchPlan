package com.hatchi.planing.soft.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val HatchPlanColorScheme = darkColorScheme(
    primary = YolkYellow,
    secondary = Turquoise,
    tertiary = LimeGreen,
    background = DarkBackground,
    surface = DarkSurface,
    error = Coral,
    onPrimary = DarkBackground,
    onSecondary = DarkBackground,
    onTertiary = DarkBackground,
    onBackground = LightText,
    onSurface = LightText,
    onError = LightText
)

@Composable
fun HatchPlanTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HatchPlanColorScheme,
        typography = Typography,
        content = content
    )
}
