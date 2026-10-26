package com.example.textilcircular.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.textilcircular.ui.theme.Amarillo
import com.example.textilcircular.ui.theme.Blanco
import com.example.textilcircular.ui.theme.Cyan
import com.example.textilcircular.ui.theme.HeroGradient
import com.example.textilcircular.ui.theme.Negro
import com.example.textilcircular.viewmodel.AppViewModelProvider
import com.example.textilcircular.viewmodel.SesionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onTerminar: (haySesion: Boolean) -> Unit,
    viewModel: SesionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val escala = remember { Animatable(0.3f) }
    val opacidadTexto = remember { Animatable(0f) }

    // Rotación continua del anillo: representa el ciclo de vida circular del textil
    val rotacion by rememberInfiniteTransition(label = "ciclo").animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2400, easing = LinearEasing), RepeatMode.Restart),
        label = "rotacion"
    )

    LaunchedEffect(Unit) {
        launch { escala.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)) }
        delay(350)
        opacidadTexto.animateTo(1f, tween(700))
    }

    LaunchedEffect(estado) {
        if (estado is SesionViewModel.Estado.Cargando) return@LaunchedEffect
        delay(1600)
        onTerminar(estado is SesionViewModel.Estado.Activa)
    }

    Box(
        Modifier.fillMaxSize().background(HeroGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.graphicsLayer { scaleX = escala.value; scaleY = escala.value }) {
                Icon(
                    Icons.Filled.Autorenew,
                    contentDescription = null,
                    tint = Cyan,
                    modifier = Modifier.size(150.dp).graphicsLayer { rotationZ = rotacion }
                )
                Box(
                    Modifier.size(72.dp).clip(CircleShape).background(Amarillo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Checkroom, contentDescription = "Logo Textil Circular", tint = Negro, modifier = Modifier.size(42.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer {
                    alpha = opacidadTexto.value
                    translationY = (1f - opacidadTexto.value) * 40f
                }
            ) {
                Text("TEXTIL CIRCULAR", style = MaterialTheme.typography.displaySmall, color = Blanco)
                Text("Fundación · Economía circular textil", style = MaterialTheme.typography.bodyLarge, color = Cyan)
            }
        }
    }
}
