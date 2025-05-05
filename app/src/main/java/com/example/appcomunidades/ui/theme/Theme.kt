package com.example.appcomunidades.ui.theme

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

val DarkNavy = Color(0xFF06141B)
val Navy = Color(0xFF11212D)
val SlateBlue = Color(0xFF253745)
val GrayBlue = Color(0xFF4A5C6A)
val LightGray = Color(0xFF9BA8AB)
val PaleGray = Color(0xFFCCD0CF)

private val DarkColorScheme = darkColorScheme(
    primary = SlateBlue,
    onPrimary = PaleGray,
    primaryContainer = GrayBlue,
    onPrimaryContainer = PaleGray,
    secondary = LightGray,
    onSecondary = DarkNavy,
    background = Navy,
    onBackground = PaleGray,
    surface = DarkNavy,
    onSurface = PaleGray
)

private val LightColorScheme = lightColorScheme(
    primary = SlateBlue,
    onPrimary = PaleGray,
    primaryContainer = GrayBlue,
    onPrimaryContainer = PaleGray,
    secondary = LightGray,
    onSecondary = DarkNavy,
    background = PaleGray,
    onBackground = DarkNavy,
    surface = LightGray,
    onSurface = DarkNavy
)

@Composable
fun TemaAppComunidades(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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