package com.pemmob.responsikotlinshiftg.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/** Custom Dark Color Scheme (Material Design 3) */
private val DarkColorScheme = darkColorScheme(
    primary = SakuraPink,
    onPrimary = Color.White,
    primaryContainer = SakuraPinkDark,
    onPrimaryContainer = SakuraPinkLight,
    secondary = MidnightVioletLight,
    onSecondary = Night950,
    secondaryContainer = Night700,
    onSecondaryContainer = MidnightVioletLight,
    tertiary = SunsetGold,
    onTertiary = Night950,
    background = Night950,
    onBackground = NightText,
    surface = Night900,
    onSurface = NightText,
    surfaceVariant = Night800,
    onSurfaceVariant = NightTextMuted,
    surfaceContainer = Night800,
    surfaceContainerHigh = Night700,
    outline = Night700,
    error = ErrorRed,
    onError = Night950
)

/** Custom Light Color Scheme (Material Design 3) */
private val LightColorScheme = lightColorScheme(
    primary = SakuraPinkDark,
    onPrimary = Color.White,
    primaryContainer = SakuraPinkLight,
    onPrimaryContainer = SakuraPinkDark,
    secondary = MidnightViolet,
    onSecondary = Color.White,
    secondaryContainer = MidnightVioletLight,
    onSecondaryContainer = InkText,
    tertiary = SunsetGoldDark,
    onTertiary = Color.White,
    background = Cream50,
    onBackground = InkText,
    surface = Color.White,
    onSurface = InkText,
    surfaceVariant = Cream100,
    onSurfaceVariant = InkTextMuted,
    surfaceContainer = Cream100,
    surfaceContainerHigh = Cream200,
    outline = Cream200,
    error = ErrorRedDark,
    onError = Color.White
)

/** Custom Shapes */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/**
 * Custom Theme aplikasi PipitNime.
 * Dynamic color sengaja tidak digunakan agar identitas warna aplikasi tetap konsisten.
 */
@Composable
fun ResponsiKotlinShiftGTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}