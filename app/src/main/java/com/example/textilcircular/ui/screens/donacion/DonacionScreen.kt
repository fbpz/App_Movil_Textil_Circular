package com.example.textilcircular.ui.screens.donacion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.textilcircular.domain.model.EstadoPrenda
import com.example.textilcircular.domain.model.REGIONES_CHILE
import com.example.textilcircular.domain.model.TIPOS_PRENDA
import com.example.textilcircular.ui.components.BarraSuperior
import com.example.textilcircular.ui.components.BotonPrincipal
import com.example.textilcircular.ui.components.CampoSelector
import com.example.textilcircular.ui.components.CampoValidado
import com.example.textilcircular.ui.components.TarjetaUbicacion
import com.example.textilcircular.ui.components.rebote
import com.example.textilcircular.ui.components.rememberSolicitudUbicacion
import com.example.textilcircular.ui.components.sacudir
import com.example.textilcircular.ui.theme.Amarillo
import com.example.textilcircular.ui.theme.Negro
import com.example.textilcircular.ui.theme.VerdeExito
import com.example.textilcircular.viewmodel.AppViewModelProvider
import com.example.textilcircular.viewmodel.DonacionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DonacionScreen(viewModel: DonacionViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val historial by viewModel.historial.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val snackbar = remember { SnackbarHostState() }

    val solicitarUbicacion = rememberSolicitudUbicacion(
        onConcedido = viewModel::obtenerUbicacion,
        onDenegado = viewModel::permisoUbicacionDenegado
    )

    LaunchedEffect(ui.intentosInvalidos) {
        if (ui.intentosInvalidos > 0) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            snackbar.showSnackbar("Revisa los campos marcados en rojo")
        }
    }

    Scaffold(
        topBar = { BarraSuperior("Donación cuidada") },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = Amarillo, contentColor = Negro)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Recycling, contentDescription = null, modifier = Modifier.size(36.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Clasificamos, donamos y trazamos tus prendas. Lo que no es donable entra a canales de economía circular.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            Column(Modifier.sacudir(ui.intentosInvalidos)) {
                CampoValidado(ui.nombreContacto, viewModel::onNombre, "Nombre de contacto", Icons.Filled.Person)
                CampoValidado(ui.telefono, viewModel::onTelefono, "Teléfono móvil", Icons.Filled.Phone, placeholder = "9 1234 5678", tipoTeclado = KeyboardType.Phone)
                TarjetaUbicacion(ui.gps, onUsarUbicacion = solicitarUbicacion)
                CampoSelector("Región", Icons.Filled.Map, REGIONES_CHILE, ui.region, { it }, viewModel::onRegion, ui.errorRegion)
                CampoValidado(ui.comuna, viewModel::onComuna, "Comuna de retiro", Icons.Filled.LocationCity)
                CampoSelector("Tipo de prenda", Icons.Filled.Checkroom, TIPOS_PRENDA, ui.tipoPrenda, { it }, viewModel::onTipoPrenda, ui.errorTipoPrenda)
                CampoValidado(
                    ui.cantidad, viewModel::onCantidad, "Cantidad de prendas", Icons.Filled.Inventory2,
                    tipoTeclado = KeyboardType.Number, ayuda = "Entre 1 y 500 prendas"
                )

                Text("Estado de las prendas *", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(vertical = 8.dp))
                EstadoPrenda.entries.forEach { estado ->
                    OpcionEstado(estado, seleccionado = ui.estado == estado, hayError = ui.errorEstado != null) { viewModel.onEstado(estado) }
                }
                AnimatedVisibility(ui.errorEstado != null) {
                    Text(ui.errorEstado.orEmpty(), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp))
                }
                // Mensaje contextual: lo no donable se deriva a reciclaje
                AnimatedVisibility(
                    ui.estado?.esDonable == false,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Text(
                        "♻ Estas prendas se derivarán a emprendimientos de suprarreciclaje y reciclaje textil.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(Modifier.height(8.dp))
                CampoValidado(
                    ui.comentario, viewModel::onComentario, "Comentario", Icons.AutoMirrored.Filled.Comment,
                    lineas = 2, contador = 200, obligatorio = false
                )
            }
            Spacer(Modifier.height(8.dp))
            BotonPrincipal("Enviar donación", viewModel::enviar, cargando = ui.guardando, icono = Icons.AutoMirrored.Filled.Send, acento = true)

            AnimatedVisibility(historial.isNotEmpty()) {
                Column(Modifier.padding(top = 24.dp)) {
                    Text("MIS DONACIONES", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    val formato = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
                    historial.forEach { d ->
                        OutlinedCard(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            Column(Modifier.padding(12.dp)) {
                                Text("${d.cantidad} × ${d.tipoPrenda}", style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "${EstadoPrenda.entries.firstOrNull { it.name == d.estado }?.etiqueta ?: d.estado} · ${d.comuna}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(formato.format(Date(d.fecha)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }

    if (ui.mostrarExito) {
        val escala = rebote(visible = true)
        AlertDialog(
            onDismissRequest = viewModel::cerrarExito,
            icon = {
                Icon(
                    Icons.Filled.CheckCircle, contentDescription = null, tint = VerdeExito,
                    modifier = Modifier.size(64.dp).graphicsLayer { scaleX = escala; scaleY = escala }
                )
            },
            title = { Text("¡Gracias por donar!") },
            text = { Text("Registramos tu donación. El equipo de la fundación te contactará para coordinar el retiro.") },
            confirmButton = { TextButton(onClick = viewModel::cerrarExito) { Text("Aceptar") } }
        )
    }
}

@Composable
private fun OpcionEstado(estado: EstadoPrenda, seleccionado: Boolean, hayError: Boolean, onClick: () -> Unit) {
    val borde by animateColorAsState(
        when {
            seleccionado -> MaterialTheme.colorScheme.primary
            hayError -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.outlineVariant
        },
        label = "bordeEstado"
    )
    OutlinedCard(
        border = BorderStroke(if (seleccionado) 2.dp else 1.dp, borde),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .selectable(selected = seleccionado, onClick = onClick, role = Role.RadioButton)
    ) {
        Row(Modifier.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = seleccionado, onClick = null)
            Text(estado.etiqueta, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
