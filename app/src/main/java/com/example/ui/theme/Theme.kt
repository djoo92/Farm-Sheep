package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Emerald80,
    onPrimary = Color(0xFF003822),
    primaryContainer = EmeraldDeep,
    onPrimaryContainer = Sage80,
    secondary = Sage80,
    onSecondary = Color(0xFF003822),
    secondaryContainer = SlateBorderDark,
    onSecondaryContainer = Color(0xFFE2E8F0),
    tertiary = AmberAccent,
    onTertiary = Color(0xFF451A00),
    background = DarkBackground,
    onBackground = TextLight,
    surface = DarkSurface,
    onSurface = TextLight,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMutedDark,
    outline = DarkBorder,
    error = RoseAlert
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = EmeraldDeep,
    secondary = Sage40,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFECFDF5),
    onSecondaryContainer = Color(0xFF065F46),
    tertiary = AmberDark,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = TextDark,
    surface = LightSurface,
    onSurface = TextDark,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = LightBorder,
    error = RoseAlert
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek executive dark theme as requested
    dynamicColor: Boolean = false, // Keep consistent high-end agricultural branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
