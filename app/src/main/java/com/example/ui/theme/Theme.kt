package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = NeonBlue,
    secondary = NeonEmerald,
    tertiary = NeonBlue,
    background = CosmicBackground,
    surface = CosmicBackground,
    onPrimary = Slate100,
    onSecondary = Slate100,
    onTertiary = Slate100,
    onBackground = Slate100,
    onSurface = Slate100,
    surfaceVariant = CardBackground,
    onSurfaceVariant = Slate300,
    outline = CardBorder
  )

private val LightColorScheme = DarkColorScheme // Force dark theme for Immersive UI

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme for Immersive UI
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false, // Disable dynamic colors to enforce theme
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
