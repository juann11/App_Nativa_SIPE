package com.sipe.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Green = Color(0xFF1B5E20)
private val GreenDark = Color(0xFF0D3B1E)
private val Amber = Color(0xFFFFB300)

private val Light = lightColorScheme(
    primary = Green,
    onPrimary = Color.White,
    secondary = Amber,
    background = Color(0xFFF6F7F9)
)
private val Dark = darkColorScheme(
    primary = Color(0xFF81C784),
    secondary = Amber
)

@Composable
fun SipeTheme(dark: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) Dark else Light, content = content)
}
