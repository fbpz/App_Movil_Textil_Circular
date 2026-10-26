package com.example.textilcircular.ui.screens.perfil

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.textilcircular.ui.components.BarraSuperior
import com.example.textilcircular.ui.theme.Amarillo
import com.example.textilcircular.ui.theme.Negro
import com.example.textilcircular.viewmodel.AppViewModelProvider
import com.example.textilcircular.viewmodel.SesionViewModel

@Composable
fun PerfilScreen(
    onSesionCerrada: () -> Unit,
    viewModel: SesionViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var confirmar by remember { mutableStateOf(false) }

    LaunchedEffect(estado) {
        if (estado is SesionViewModel.Estado.SinSesion) onSesionCerrada()
    }

    val nombre = (estado as? SesionViewModel.Estado.Activa)?.sesion?.nombre.orEmpty()

    Scaffold(topBar = { BarraSuperior("Mi perfil") }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.size(96.dp).clip(CircleShape).background(Amarillo), contentAlignment = Alignment.Center) {
                Text(
                    nombre.split(" ").filter { it.isNotBlank() }.take(2).joinToString("") { it.first().uppercase() },
                    style = MaterialTheme.typography.headlineLarge,
                    color = Negro
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(nombre, style = MaterialTheme.typography.headlineSmall)
            Text("Miembro de la comunidad Textil Circular", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(32.dp))

            OutlinedButton(
                onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://textilcircular.cl"))) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Language, contentDescription = null)
                Text("  Visitar textilcircular.cl")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { confirmar = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Text("  Cerrar sesión", color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Tus datos quedarán guardados en este dispositivo.") },
            confirmButton = {
                TextButton(onClick = { confirmar = false; viewModel.cerrarSesion() }) { Text("Cerrar sesión") }
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } }
        )
    }
}
