package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AkColorScheme = darkColorScheme(
  primary = AkPurplePrimary,
  onPrimary = AkTextPrimary,
  primaryContainer = AkPurpleDark,
  onPrimaryContainer = AkPurpleLight,
  secondary = AkCyanAccent,
  onSecondary = AkBgDark,
  secondaryContainer = AkBgSurfaceElevated,
  onSecondaryContainer = AkCyanLight,
  tertiary = AkBlueAccent,
  onTertiary = AkTextPrimary,
  background = AkBgDark,
  onBackground = AkTextPrimary,
  surface = AkBgSurface,
  onSurface = AkTextPrimary,
  surfaceVariant = AkBgSurfaceVariant,
  onSurfaceVariant = AkTextSecondary,
  error = AkError,
  onError = AkTextPrimary,
)

@Composable
fun AKEditorTheme(
  content: @Composable () -> Unit
) {
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = AkBgDark.toArgb()
        window.navigationBarColor = AkBgDark.toArgb()
        val controller = WindowCompat.getInsetsController(window, view)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = AkColorScheme,
    typography = Typography,
    content = content
  )
}
