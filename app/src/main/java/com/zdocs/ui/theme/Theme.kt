package com.zdocs.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Teal50,
    onPrimary = Gray10,
    primaryContainer = Teal90,
    onPrimaryContainer = Teal10,
    inversePrimary = Teal80,

    secondary = Green40,
    onSecondary = Gray10,
    secondaryContainer = Green90,
    onSecondaryContainer = Green10,

    tertiary = Teal60,
    onTertiary = Gray10,
    tertiaryContainer = Teal95,
    onTertiaryContainer = Teal30,

    error = ErrorRed,
    onError = Gray99,
    errorContainer = ErrorLight,
    onErrorContainer = ErrorRed,

    background = Gray99,
    onBackground = Gray10,
    surface = Gray99,
    onSurface = Gray10,
    surfaceVariant = Gray95,
    onSurfaceVariant = Gray30,
    inverseSurface = Gray20,
    inverseOnSurface = Gray95,
    outline = Gray50,
    outlineVariant = Gray80,
    scrim = Gray10,
)

private val DarkColorScheme = darkColorScheme(
    primary = Teal80,
    onPrimary = Teal20,
    primaryContainer = Teal40,
    onPrimaryContainer = Teal90,
    inversePrimary = Teal50,

    secondary = Green80,
    onSecondary = Green20,
    secondaryContainer = Green30,
    onSecondaryContainer = Green90,

    tertiary = Teal70,
    onTertiary = Teal20,
    tertiaryContainer = Teal40,
    onTertiaryContainer = Teal95,

    error = ErrorLight,
    onError = ErrorRed,
    errorContainer = ErrorRed,
    onErrorContainer = ErrorLight,

    background = Gray10,
    onBackground = Gray90,
    surface = Gray10,
    onSurface = Gray90,
    surfaceVariant = Gray30,
    onSurfaceVariant = Gray80,
    inverseSurface = Gray90,
    inverseOnSurface = Gray20,
    outline = Gray60,
    outlineVariant = Gray40,
    scrim = Gray10,
)

@Composable
fun ZDocsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ZDocsTypography,
        content = content
    )
}
