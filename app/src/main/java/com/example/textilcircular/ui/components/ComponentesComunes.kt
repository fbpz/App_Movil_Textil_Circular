package com.example.textilcircular.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.textilcircular.ui.theme.Amarillo
import com.example.textilcircular.ui.theme.Blanco
import com.example.textilcircular.ui.theme.Carbon
import com.example.textilcircular.ui.theme.Cyan
import com.example.textilcircular.ui.theme.Negro
import java.io.File

/** Barra superior con el estilo oscuro del header de textilcircular.cl. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    onVolver: (() -> Unit)? = null,
    acciones: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = { Text(titulo.uppercase(), style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            if (onVolver != null) IconButton(onClick = onVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
        },
        actions = { acciones() },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Carbon,
            titleContentColor = Blanco,
            navigationIconContentColor = Cyan,
            actionIconContentColor = Cyan
        )
    )
}

/**
 * Botón principal de marca (cyan con texto negro, como los CTA de la web).
 * Se encoge levemente al presionarlo y muestra un indicador mientras carga.
 */
@Composable
fun BotonPrincipal(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cargando: Boolean = false,
    icono: ImageVector? = null,
    acento: Boolean = false
) {
    val interaccion = remember { MutableInteractionSource() }
    val presionado by interaccion.collectIsPressedAsState()
    val escala by animateFloatAsState(if (presionado) 0.96f else 1f, label = "escalaBoton")

    Button(
        onClick = onClick,
        enabled = !cargando,
        interactionSource = interaccion,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (acento) Amarillo else Cyan,
            contentColor = Negro,
            disabledContainerColor = (if (acento) Amarillo else Cyan).copy(alpha = 0.6f),
            disabledContentColor = Negro
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .graphicsLayer { scaleX = escala; scaleY = escala }
    ) {
        AnimatedContent(
            targetState = cargando,
            transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(200)) },
            label = "contenidoBoton"
        ) { estaCargando ->
            if (estaCargando) {
                CircularProgressIndicator(color = Negro, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
            } else Row(verticalAlignment = Alignment.CenterVertically) {
                icono?.let {
                    Icon(it, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                }
                Text(texto.uppercase(), style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

/**
 * Animación de "sacudida" horizontal: se dispara cada vez que cambia [disparador]
 * (por ejemplo, cuando el usuario envía un formulario con errores).
 */
fun Modifier.sacudir(disparador: Int): Modifier = composed {
    val desplazamiento = remember { Animatable(0f) }
    LaunchedEffect(disparador) {
        if (disparador == 0) return@LaunchedEffect
        for (x in listOf(-18f, 16f, -12f, 8f, -4f, 0f)) {
            desplazamiento.animateTo(x, tween(45))
        }
    }
    graphicsLayer { translationX = desplazamiento.value }
}

/** Mensaje de error general del formulario, aparece y desaparece animado. */
@Composable
fun BannerError(mensaje: String?, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = mensaje != null,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        ) {
            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.ErrorOutline, contentDescription = "Error")
                Spacer(Modifier.width(8.dp))
                Text(mensaje.orEmpty(), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

/** Número que cuenta desde 0 hasta [valor] al aparecer en pantalla. */
@Composable
fun ContadorAnimado(valor: Int, sufijo: String = ""): String {
    val animado by animateIntAsState(
        targetValue = valor,
        animationSpec = tween(1400, easing = FastOutSlowInEasing),
        label = "contador"
    )
    return "%,d".format(animado).replace(',', '.') + sufijo
}

@Composable
fun EstadoVacio(icono: ImageVector, titulo: String, mensaje: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier.size(96.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(titulo, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(4.dp))
        Text(
            mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/** Foto local cargada con Coil, con un ícono de respaldo si no hay imagen. */
@Composable
fun FotoLocal(
    path: String?,
    descripcion: String,
    iconoRespaldo: ImageVector,
    modifier: Modifier = Modifier
) {
    if (path == null) {
        Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            Icon(iconoRespaldo, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        SubcomposeAsyncImage(
            model = File(path),
            contentDescription = descripcion,
            contentScale = ContentScale.Crop,
            loading = {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                }
            },
            error = {
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                    Icon(iconoRespaldo, contentDescription = null)
                }
            },
            modifier = modifier
        )
    }
}

/** Escala de 0 a 1 con rebote al entrar en composición, usada para íconos de éxito. */
@Composable
fun rebote(visible: Boolean): Float {
    val escala = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        escala.animateTo(
            if (visible) 1f else 0f,
            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }
    return escala.value
}
