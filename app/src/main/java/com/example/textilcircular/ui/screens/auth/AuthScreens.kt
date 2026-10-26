package com.example.textilcircular.ui.screens.auth

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.textilcircular.ui.components.BannerError
import com.example.textilcircular.ui.components.BarraSuperior
import com.example.textilcircular.ui.components.BotonPrincipal
import com.example.textilcircular.ui.components.CampoValidado
import com.example.textilcircular.ui.components.sacudir
import com.example.textilcircular.ui.theme.Amarillo
import com.example.textilcircular.ui.theme.Blanco
import com.example.textilcircular.ui.theme.Cyan
import com.example.textilcircular.ui.theme.HeroGradient
import com.example.textilcircular.ui.theme.Negro
import com.example.textilcircular.ui.theme.VerdeExito
import com.example.textilcircular.viewmodel.AppViewModelProvider
import com.example.textilcircular.viewmodel.LoginViewModel
import com.example.textilcircular.viewmodel.RegistroViewModel

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onIrRegistro: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) { viewModel.exito.collect { onLoginExitoso() } }
    LaunchedEffect(ui.intentosInvalidos) {
        if (ui.intentosInvalidos > 0) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(HeroGradient)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(32.dp))
        Box(Modifier.size(80.dp).clip(CircleShape).background(Amarillo), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Checkroom, contentDescription = null, tint = Negro, modifier = Modifier.size(44.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text("TEXTIL CIRCULAR", style = MaterialTheme.typography.displaySmall, color = Blanco)
        Text(
            "Conecta con emprendimientos de economía circular",
            style = MaterialTheme.typography.bodyMedium,
            color = Cyan
        )
        Spacer(Modifier.height(28.dp))

        Card(
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth().sacudir(ui.intentosInvalidos)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(12.dp))
                BannerError(ui.errorGeneral)
                CampoValidado(
                    campo = ui.email,
                    onValueChange = viewModel::onEmail,
                    etiqueta = "Correo electrónico",
                    icono = Icons.Filled.Email,
                    placeholder = "nombre@correo.cl",
                    tipoTeclado = KeyboardType.Email
                )
                CampoValidado(
                    campo = ui.password,
                    onValueChange = viewModel::onPassword,
                    etiqueta = "Contraseña",
                    icono = Icons.Filled.Lock,
                    tipoTeclado = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    esPassword = true
                )
                Spacer(Modifier.height(8.dp))
                BotonPrincipal(
                    texto = "Ingresar",
                    icono = Icons.AutoMirrored.Filled.Login,
                    cargando = ui.cargando,
                    onClick = viewModel::ingresar
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onIrRegistro) {
            Text("¿No tienes cuenta? ", color = Blanco)
            Text("Regístrate", color = Amarillo, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun RegistroScreen(
    onRegistrado: () -> Unit,
    onVolver: () -> Unit,
    viewModel: RegistroViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) { viewModel.exito.collect { onRegistrado() } }
    LaunchedEffect(ui.intentosInvalidos) {
        if (ui.intentosInvalidos > 0) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    Scaffold(topBar = { BarraSuperior("Crear cuenta", onVolver = onVolver) }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
                .sacudir(ui.intentosInvalidos)
        ) {
            Text(
                "Únete a la comunidad Textil Circular para registrar emprendimientos y donaciones.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            BannerError(ui.errorGeneral)
            CampoValidado(ui.nombre, viewModel::onNombre, "Nombre completo", Icons.Filled.Person, placeholder = "María González")
            CampoValidado(
                ui.rut, viewModel::onRut, "RUT", Icons.Filled.Badge,
                placeholder = "12.345.678-5", ayuda = "Se valida el dígito verificador"
            )
            CampoValidado(ui.email, viewModel::onEmail, "Correo electrónico", Icons.Filled.Email, tipoTeclado = KeyboardType.Email)
            CampoValidado(
                ui.password, viewModel::onPassword, "Contraseña", Icons.Filled.Lock,
                tipoTeclado = KeyboardType.Password, esPassword = true,
                ayuda = "Mínimo 8 caracteres, una mayúscula y un número"
            )
            IndicadorFuerza(ui.fuerzaPassword, visible = ui.password.valor.isNotEmpty())
            CampoValidado(
                ui.confirmacion, viewModel::onConfirmacion, "Confirmar contraseña", Icons.Filled.LockReset,
                tipoTeclado = KeyboardType.Password, imeAction = ImeAction.Done, esPassword = true
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = ui.aceptaTerminos, onCheckedChange = viewModel::onTerminos)
                Text("Acepto el uso de mis datos para fines de la fundación", style = MaterialTheme.typography.bodyMedium)
            }
            ui.errorTerminos?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 16.dp))
            }
            Spacer(Modifier.height(16.dp))
            BotonPrincipal("Crear cuenta", viewModel::registrar, cargando = ui.cargando, icono = Icons.Filled.PersonAdd)
        }
    }
}

/** Barra de seguridad de contraseña con color y avance animados. */
@Composable
private fun IndicadorFuerza(nivel: Int, visible: Boolean) {
    if (!visible) return
    val progreso by animateFloatAsState(nivel / 4f, tween(400), label = "fuerza")
    val (texto, colorObjetivo) = when (nivel) {
        0, 1 -> "Débil" to MaterialTheme.colorScheme.error
        2 -> "Media" to Amarillo
        3 -> "Buena" to Cyan
        else -> "Fuerte" to VerdeExito
    }
    val color by animateColorAsState(colorObjetivo, tween(400), label = "colorFuerza")
    Column(Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        LinearProgressIndicator(
            progress = { progreso },
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
        )
        Text("Seguridad: $texto", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
