package com.example.textilcircular.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GpsOff
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.textilcircular.ui.theme.Blanco
import com.example.textilcircular.ui.theme.Negro
import com.example.textilcircular.ui.theme.VerdeExito
import com.example.textilcircular.viewmodel.EstadoGps

/** Abre la ficha de la app en Ajustes para conceder un permiso denegado de forma permanente. */
fun abrirAjustesApp(context: Context) {
    context.startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

/**
 * Devuelve una función que, al invocarse, verifica/solicita el permiso de ubicación
 * y luego llama a [onConcedido] o [onDenegado].
 */
@Composable
fun rememberSolicitudUbicacion(onConcedido: () -> Unit, onDenegado: () -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { resultado ->
        if (resultado.values.any { it }) onConcedido() else onDenegado()
    }
    return {
        val concedido = listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            .any { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
        if (concedido) onConcedido()
        else launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }
}

/**
 * Devuelve las acciones para tomar una foto (con permiso de cámara) o elegirla de la galería
 * (Photo Picker del sistema, no requiere permisos).
 */
class AccionesFoto(val tomarFoto: () -> Unit, val elegirDeGaleria: () -> Unit)

@Composable
fun rememberAccionesFoto(
    prepararUriCamara: () -> Uri,
    onResultadoCamara: (Boolean) -> Unit,
    onResultadoGaleria: (Uri?) -> Unit,
    onPermisoDenegado: () -> Unit
): AccionesFoto {
    val context = LocalContext.current
    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture(), onResultadoCamara)
    val galeria = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia(), onResultadoGaleria)
    val permisoCamara = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
        if (concedido) camara.launch(prepararUriCamara()) else onPermisoDenegado()
    }
    return AccionesFoto(
        tomarFoto = {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                camara.launch(prepararUriCamara())
            } else permisoCamara.launch(Manifest.permission.CAMERA)
        },
        elegirDeGaleria = {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    )
}

/** Vista previa de la foto con botones de cámara y galería. */
@Composable
fun SelectorFoto(
    fotoPath: String?,
    error: String?,
    acciones: AccionesFoto,
    onQuitar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorBorde = if (error != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outlineVariant
    Column(modifier.fillMaxWidth()) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 10f)
                .clip(MaterialTheme.shapes.medium)
                .border(BorderStroke(if (error != null) 2.dp else 1.dp, colorBorde), MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = fotoPath,
                transitionSpec = { (fadeIn(tween(400)) + scaleIn(initialScale = 0.9f)) togetherWith fadeOut(tween(200)) },
                label = "foto"
            ) { path ->
                if (path == null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.AddAPhoto, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Foto de tus productos *", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    Box(Modifier.fillMaxSize()) {
                        FotoLocal(path, "Foto seleccionada", Icons.Filled.AddAPhoto, Modifier.fillMaxSize())
                        FilledIconButton(
                            onClick = onQuitar,
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Negro.copy(alpha = 0.6f), contentColor = Blanco),
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                        ) { Icon(Icons.Filled.Close, contentDescription = "Quitar foto") }
                    }
                }
            }
        }
        AnimatedVisibility(error != null, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
            Text(
                error.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = acciones.tomarFoto, modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.PhotoCamera, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Cámara")
            }
            OutlinedButton(onClick = acciones.elegirDeGaleria, modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.PhotoLibrary, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Galería")
            }
        }
    }
}

/** Tarjeta que muestra el estado del GPS con animaciones según la etapa. */
@Composable
fun TarjetaUbicacion(estado: EstadoGps, onUsarUbicacion: () -> Unit, modifier: Modifier = Modifier) {
    val pulso = rememberInfiniteTransition(label = "pulsoGps").animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(700, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulso"
    )
    OutlinedCard(modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                when (estado) {
                    EstadoGps.Buscando -> Icon(
                        Icons.Filled.GpsFixed, contentDescription = null, tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.graphicsLayer { scaleX = pulso.value; scaleY = pulso.value }
                    )
                    is EstadoGps.Obtenida -> Icon(Icons.Filled.GpsFixed, contentDescription = null, tint = VerdeExito)
                    is EstadoGps.Error -> Icon(Icons.Filled.GpsOff, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    EstadoGps.Inactivo -> Icon(Icons.Filled.MyLocation, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Ubicación GPS", style = MaterialTheme.typography.titleSmall)
                Text(
                    when (estado) {
                        EstadoGps.Inactivo -> "Completa comuna y región automáticamente"
                        EstadoGps.Buscando -> "Buscando señal…"
                        is EstadoGps.Obtenida -> estado.ubicacion.comuna?.let { "Detectado: $it" }
                            ?: "Coordenadas: %.4f, %.4f".format(estado.ubicacion.latitud, estado.ubicacion.longitud)
                        is EstadoGps.Error -> estado.mensaje
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (estado is EstadoGps.Error) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (estado == EstadoGps.Buscando) {
                CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
            } else {
                TextButton(onClick = onUsarUbicacion) { Text(if (estado is EstadoGps.Obtenida) "Actualizar" else "Usar") }
            }
        }
    }
}
