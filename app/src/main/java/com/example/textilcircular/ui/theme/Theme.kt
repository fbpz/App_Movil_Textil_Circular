package com.example.textilcircular.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = CyanOscuro,
    onPrimary = Blanco,
    primaryContainer = Cyan,
    onPrimaryContainer = Negro,
    secondary = AmarilloOscuro,
    onSecondary = Blanco,
    secondaryContainer = Amarillo,
    onSecondaryContainer = Negro,
    tertiary = VerdePetroleo,
    onTertiary = Blanco,
    tertiaryContainer = PetroleoProfundo,
    onTertiaryContainer = Cyan,
    background = GrisFondo,
    onBackground = Carbon,
    surface = Blanco,
    onSurface = Carbon,
    surfaceVariant = GrisClaro,
    onSurfaceVariant = GrisTexto,
    surfaceContainer = Blanco,
    surfaceContainerLow = Blanco,
    surfaceContainerHigh = GrisClaro,
    outline = GrisBorde,
    outlineVariant = GrisMedio,
    error = RojoError,
    onError = Blanco
)

private val DarkColorScheme = darkColorScheme(
    primary = Cyan,
    onPrimary = Negro,
    primaryContainer = VerdePetroleo,
    onPrimaryContainer = Cyan,
    secondary = Amarillo,
    onSecondary = Negro,
    secondaryContainer = Amarillo,
    onSecondaryContainer = Negro,
    tertiary = Cyan,
    onTertiary = Negro,
    tertiaryContainer = VerdePetroleo,
    onTertiaryContainer = Cyan,
    background = Carbon,
    onBackground = GrisFondo,
    surface = PetroleoProfundo,
    onSurface = GrisFondo,
    surfaceVariant = VerdePetroleo,
    onSurfaceVariant = GrisMedio,
    surfaceContainer = PetroleoProfundo,
    surfaceContainerLow = PetroleoProfundo,
    surfaceContainerHigh = VerdePetroleo,
    outline = GrisBorde,
    outlineVariant = VerdePetroleo,
    error = RojoErrorClaro,
    onError = Negro
)

// --r: 12px en la web
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

// Degradado del hero de textilcircular.cl (145deg)
val HeroGradient = Brush.linearGradient(
    colors = listOf(Carbon, VerdePetroleo, PetroleoProfundo),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)

@Composable
fun TextilCircularTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Sin color dinámico: la app debe respetar la identidad visual de la marca
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
