package com.saiful.findbackbd.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object AppSettings {
    var dark by mutableStateOf(false)
}

private val LightColors = lightColorScheme(
    primary = Green,
    onPrimary = Color.White,
    secondary = Green,
    background = AppBg,
    surface = Color.White,
    onBackground = Color(0xFF121816),
    onSurface = Color(0xFF121816)
)

private val DarkColors = darkColorScheme(
    primary = Green,
    onPrimary = Color.White,
    secondary = Green,
    background = Color(0xFF0F1513),
    surface = Color(0xFF18221E),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun FindBackTheme(
    dark: Boolean = AppSettings.dark,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        content = content
    )
}
