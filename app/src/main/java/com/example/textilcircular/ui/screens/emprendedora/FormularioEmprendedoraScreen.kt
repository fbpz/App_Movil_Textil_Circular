package com.example.textilcircular.ui.screens.emprendedora

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.textilcircular.domain.model.Categoria
import com.example.textilcircular.domain.model.REGIONES_CHILE
import com.example.textilcircular.ui.components.BarraSuperior
import com.example.textilcircular.ui.components.BotonPrincipal
import com.example.textilcircular.ui.components.CampoSelector
import com.example.textilcircular.ui.components.CampoValidado
import com.example.textilcircular.ui.components.SelectorFoto
import com.example.textilcircular.ui.components.TarjetaUbicacion
import com.example.textilcircular.ui.components.abrirAjustesApp
import com.example.textilcircular.ui.components.rememberAccionesFoto
import com.example.textilcircular.ui.components.rememberSolicitudUbicacion
import com.example.textilcircular.ui.components.sacudir
import com.example.textilcircular.viewmodel.AppViewModelProvider
import com.example.textilcircular.viewmodel.EmprendedoraFormViewModel
import kotlinx.coroutines.launch

@Composable
fun FormularioEmprendedoraScreen(
    onVolver: () -> Unit,
    onGuardado: (Long) -> Unit,
    viewModel: EmprendedoraFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun avisarPermisoDenegado(mensaje: String) {
        scope.launch {
            val r = snackbar.showSnackbar(mensaje, actionLabel = "Ajustes", duration = SnackbarDuration.Long)
            if (r == SnackbarResult.ActionPerformed) abrirAjustesApp(context)
        }
    }

    val accionesFoto = rememberAccionesFoto(
        prepararUriCamara = viewModel::prepararFotoCamara,
        onResultadoCamara = viewModel::resultadoCamara,
        onResultadoGaleria = viewModel::resultadoGaleria,
        onPermisoDenegado = { avisarPermisoDenegado("Se necesita la cámara para fotografiar tus productos") }
    )
    val solicitarUbicacion = rememberSolicitudUbicacion(
        onConcedido = viewModel::obtenerUbicacion,
        onDenegado = {
            viewModel.permisoUbicacionDenegado()
            avisarPermisoDenegado("Permiso de ubicación denegado")
        }
    )

    LaunchedEffect(Unit) {
        viewModel.guardado.collect { id ->
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onGuardado(id)
        }
    }
    LaunchedEffect(ui.intentosInvalidos) {
        if (ui.intentosInvalidos > 0) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            snackbar.showSnackbar("Revisa los campos marcados en rojo")
        }
    }

    Scaffold(
        topBar = { BarraSuperior(if (ui.modoEdicion) "Editar emprendimiento" else "Nuevo emprendimiento", onVolver = onVolver) },
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
            Text(
                "Registra un emprendimiento de economía circular para sumarlo al directorio. Los campos con * son obligatorios.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))

            SelectorFoto(ui.fotoPath, ui.errorFoto, accionesFoto, viewModel::quitarFoto, Modifier.sacudir(if (ui.errorFoto != null) ui.intentosInvalidos else 0))

            Seccion("Datos del emprendimiento")
            Column(Modifier.sacudir(ui.intentosInvalidos)) {
                CampoValidado(ui.nombre, viewModel::onNombre, "Nombre de la emprendedora", Icons.Filled.Person)
                CampoValidado(ui.nombreComercial, viewModel::onNombreComercial, "Nombre comercial / marca", Icons.Filled.Storefront)
                CampoSelector(
                    etiqueta = "Categoría",
                    icono = Icons.Filled.Category,
                    opciones = Categoria.entries,
                    seleccion = ui.categoria,
                    textoOpcion = { it.etiqueta },
                    onSeleccion = viewModel::onCategoria,
                    error = ui.errorCategoria
                )
                CampoValidado(
                    ui.descripcion, viewModel::onDescripcion, "Descripción", Icons.Filled.Description,
                    lineas = 3, contador = 300, ayuda = "¿Qué productos o servicios ofreces?"
                )

                Seccion("Ubicación")
                TarjetaUbicacion(ui.gps, onUsarUbicacion = solicitarUbicacion)
                CampoSelector(
                    etiqueta = "Región",
                    icono = Icons.Filled.Map,
                    opciones = REGIONES_CHILE,
                    seleccion = ui.region,
                    textoOpcion = { it },
                    onSeleccion = viewModel::onRegion,
                    error = ui.errorRegion
                )
                CampoValidado(ui.comuna, viewModel::onComuna, "Comuna", Icons.Filled.LocationCity)

                Seccion("Contacto")
                CampoValidado(
                    ui.telefono, viewModel::onTelefono, "Teléfono móvil", Icons.Filled.Phone,
                    placeholder = "9 1234 5678", tipoTeclado = KeyboardType.Phone
                )
                CampoValidado(ui.email, viewModel::onEmail, "Correo electrónico", Icons.Filled.Email, tipoTeclado = KeyboardType.Email)
                CampoValidado(
                    ui.instagram, viewModel::onInstagram, "Instagram", Icons.Filled.AlternateEmail,
                    placeholder = "@mimarca", imeAction = ImeAction.Done, obligatorio = false
                )
            }
            Spacer(Modifier.height(16.dp))
            BotonPrincipal(
                texto = if (ui.modoEdicion) "Guardar cambios" else "Registrar emprendimiento",
                onClick = viewModel::guardar,
                cargando = ui.guardando,
                icono = Icons.Filled.Save
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Seccion(titulo: String) {
    Text(
        titulo.uppercase(),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}
