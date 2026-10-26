package com.example.textilcircular.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.textilcircular.domain.validation.CampoFormulario
import com.example.textilcircular.ui.theme.VerdeExito

/**
 * Campo de texto estándar de la app:
 * - ícono descriptivo a la izquierda
 * - ícono de estado a la derecha (✓ válido / ⚠ error) con animación
 * - mensaje de error animado bajo el campo
 */
@Composable
fun CampoValidado(
    campo: CampoFormulario,
    onValueChange: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    ayuda: String? = null,
    placeholder: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    esPassword: Boolean = false,
    lineas: Int = 1,
    contador: Int? = null,
    obligatorio: Boolean = true
) {
    var verPassword by rememberSaveable { mutableStateOf(false) }
    val error = campo.errorVisible
    val mostrarOk = campo.tocado && campo.error == null && campo.valor.isNotBlank()

    OutlinedTextField(
        value = campo.valor,
        onValueChange = onValueChange,
        label = { Text(if (obligatorio) "$etiqueta *" else etiqueta) },
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = { Icon(icono, contentDescription = null) },
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedContent(
                    targetState = when { error != null -> 1; mostrarOk -> 2; else -> 0 },
                    transitionSpec = { scaleIn() + fadeIn() togetherWith scaleOut() + fadeOut() },
                    label = "iconoEstado"
                ) { estado ->
                    when (estado) {
                        1 -> Icon(Icons.Filled.Error, "Campo con error", tint = MaterialTheme.colorScheme.error)
                        2 -> Icon(Icons.Filled.CheckCircle, "Campo válido", tint = VerdeExito)
                        else -> Spacer(Modifier.size(0.dp))
                    }
                }
                if (esPassword) {
                    IconButton(onClick = { verPassword = !verPassword }) {
                        Icon(
                            if (verPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (verPassword) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                } else Spacer(Modifier.width(12.dp))
            }
        },
        isError = error != null,
        supportingText = {
            Row {
                Column(Modifier.weight(1f)) {
                    AnimatedVisibility(
                        visible = error != null,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) { Text(error.orEmpty()) }
                    if (error == null && ayuda != null) Text(ayuda)
                }
                contador?.let { Text("${campo.valor.length}/$it") }
            }
        },
        singleLine = lineas == 1,
        minLines = lineas,
        maxLines = if (lineas == 1) 1 else lineas + 2,
        visualTransformation = if (esPassword && !verPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado, imeAction = imeAction),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.fillMaxWidth()
    )
}

/** Selector desplegable con la misma retroalimentación visual que los campos de texto. */
@Composable
fun <T> CampoSelector(
    etiqueta: String,
    icono: ImageVector,
    opciones: List<T>,
    seleccion: T?,
    textoOpcion: (T) -> String,
    onSeleccion: (T) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    var expandido by remember { mutableStateOf(false) }
    Box(modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = seleccion?.let(textoOpcion).orEmpty(),
            onValueChange = {},
            readOnly = true,
            label = { Text("$etiqueta *") },
            leadingIcon = { Icon(icono, contentDescription = null) },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    when {
                        error != null -> Icon(Icons.Filled.Error, "Campo con error", tint = MaterialTheme.colorScheme.error)
                        seleccion != null -> Icon(Icons.Filled.CheckCircle, "Campo válido", tint = VerdeExito)
                    }
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                }
            },
            isError = error != null,
            supportingText = {
                AnimatedVisibility(error != null, enter = expandVertically() + fadeIn(), exit = shrinkVertically() + fadeOut()) {
                    Text(error.orEmpty())
                }
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        )
        // Capa transparente para abrir el menú al tocar cualquier parte del campo
        Box(
            Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { expandido = true }
                .semantics { contentDescription = "Seleccionar $etiqueta" }
        )
        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(textoOpcion(opcion)) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}
