package com.example.textilcircular.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.textilcircular.viewmodel.EmprendedoraViewModel

@Composable
fun FormularioScreen(viewModel: EmprendedoraViewModel, onNavigateToCamera: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = "Registro de Emprendedora",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Retroalimentación visual: Mensaje de error general
        if (viewModel.errorMensaje.value.isNotEmpty()) {
            Text(
                text = viewModel.errorMensaje.value,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Campo 1: Nombre con validación visual e ícono
        OutlinedTextField(
            value = viewModel.nombre.value,
            onValueChange = { viewModel.nombre.value = it },
            label = { Text("Nombre de la emprendedora") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Ícono de persona") },
            isError = viewModel.nombre.value.isBlank() && viewModel.errorMensaje.value.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Campo 2: Nombre Comercial
        OutlinedTextField(
            value = viewModel.nombreComercial.value,
            onValueChange = { viewModel.nombreComercial.value = it },
            label = { Text("Nombre comercial / Marca") },
            leadingIcon = { Icon(Icons.Default.Star, contentDescription = "Ícono de marca") },
            isError = viewModel.nombreComercial.value.isBlank() && viewModel.errorMensaje.value.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Campo 3: Rubro
        OutlinedTextField(
            value = viewModel.rubro.value,
            onValueChange = { viewModel.rubro.value = it },
            label = { Text("Rubro (ej. Suprarreciclaje)") },
            leadingIcon = { Icon(Icons.Default.Info, contentDescription = "Ícono de rubro") },
            isError = viewModel.rubro.value.isBlank() && viewModel.errorMensaje.value.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        Button(
            onClick = {
                viewModel.guardarEmprendedora(
                    onSuccess = { onNavigateToCamera() }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar y Continuar")
        }
    }
}