package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryBlue,
  onPrimary = TextPrimary,
  secondary = AccentCyan,
  onSecondary = DarkSlateBackground,
  tertiary = AccentEmerald,
  background = DarkSlateBackground,
  onBackground = TextPrimary,
  surface = CardSurface,
  onSurface = TextPrimary,
  surfaceVariant = StrokeBorder,
  onSurfaceVariant = TextSecondary,
  outline = StrokeBorder
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
