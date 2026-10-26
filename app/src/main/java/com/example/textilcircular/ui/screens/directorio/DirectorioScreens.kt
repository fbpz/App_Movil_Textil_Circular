package com.example.textilcircular.ui.screens.directorio

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.textilcircular.data.local.entity.Emprendedora
import com.example.textilcircular.domain.model.Categoria
import com.example.textilcircular.ui.components.BarraSuperior
import com.example.textilcircular.ui.components.BotonPrincipal
import com.example.textilcircular.ui.components.EstadoVacio
import com.example.textilcircular.ui.components.FotoLocal
import com.example.textilcircular.ui.theme.Amarillo
import com.example.textilcircular.ui.theme.Cyan
import com.example.textilcircular.ui.theme.Negro
import com.example.textilcircular.viewmodel.AppViewModelProvider
import com.example.textilcircular.viewmodel.DetalleViewModel
import com.example.textilcircular.viewmodel.DirectorioViewModel
import com.example.textilcircular.viewmodel.categoriaEtiqueta

@Composable
fun DirectorioScreen(
    onVerDetalle: (Long) -> Unit,
    onNueva: () -> Unit,
    viewModel: DirectorioViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            BarraSuperior("Directorio") {
                IconButton(onClick = viewModel::onSoloFavoritas) {
                    Icon(
                        if (ui.soloFavoritas) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (ui.soloFavoritas) "Mostrar todas" else "Mostrar solo favoritas",
                        tint = if (ui.soloFavoritas) Amarillo else Cyan
                    )
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNueva,
                containerColor = Amarillo,
                contentColor = Negro,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Registrar") }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                value = ui.busqueda,
                onValueChange = viewModel::onBusqueda,
                placeholder = { Text("Buscar por nombre, marca o comuna") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    AnimatedVisibility(ui.busqueda.isNotEmpty(), enter = scaleIn() + fadeIn(), exit = fadeOut()) {
                        IconButton(onClick = { viewModel.onBusqueda("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Categoria.entries) { c ->
                    val seleccionada = ui.categoria == c
                    FilterChip(
                        selected = seleccionada,
                        onClick = { viewModel.onCategoria(c) },
                        label = { Text(c.etiqueta) },
                        leadingIcon = if (seleccionada) {
                            { Icon(Icons.Filled.Category, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        } else null,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Cyan,
                            selectedLabelColor = Negro,
                            selectedLeadingIconColor = Negro
                        ),
                        modifier = Modifier.animateContentSize()
                    )
                }
            }
            Text(
                "${ui.resultados.size} resultado(s)",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Column(Modifier.fillMaxSize()) {
                AnimatedVisibility(!ui.cargando && ui.resultados.isEmpty(), enter = fadeIn(), exit = fadeOut()) {
                    val hayFiltros = ui.busqueda.isNotEmpty() || ui.categoria != null || ui.soloFavoritas
                    EstadoVacio(
                        icono = if (hayFiltros) Icons.Filled.SearchOff else Icons.Filled.Storefront,
                        titulo = if (hayFiltros) "Sin coincidencias" else "Directorio vacío",
                        mensaje = if (hayFiltros) "Prueba con otra búsqueda o quita los filtros."
                        else "Registra el primer emprendimiento de economía circular con el botón Registrar."
                    )
                }
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ui.resultados, key = { it.id }) { e ->
                        TarjetaEmprendedora(
                            e = e,
                            onClick = { onVerDetalle(e.id) },
                            onFavorita = { viewModel.alternarFavorita(e) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaEmprendedora(e: Emprendedora, onClick: () -> Unit, onFavorita: () -> Unit, modifier: Modifier) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FotoLocal(e.fotoPath, "Foto de ${e.nombreComercial}", Icons.Filled.Checkroom, Modifier.size(96.dp))
            Column(Modifier.weight(1f).padding(12.dp)) {
                Text(e.nombreComercial, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(e.nombre, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(e.categoriaEtiqueta().uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${e.comuna}, ${e.region}", style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            BotonFavorito(e.favorita, onFavorita)
        }
    }
}

/** Corazón que "late" con rebote al marcar/desmarcar como favorito. */
@Composable
private fun BotonFavorito(favorita: Boolean, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    var pulsos by remember { mutableStateOf(0) }
    val escala by animateFloatAsState(
        targetValue = if (pulsos % 2 == 1) 1.35f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessMedium),
        finishedListener = { if (pulsos % 2 == 1) pulsos++ },
        label = "latido"
    )
    IconButton(onClick = {
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        pulsos++
        onClick()
    }) {
        Icon(
            if (favorita) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
            contentDescription = if (favorita) "Quitar de favoritos" else "Agregar a favoritos",
            tint = if (favorita) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.graphicsLayer { scaleX = escala; scaleY = escala }
        )
    }
}

// ---------------------------------------------------------------- Detalle

@Composable
fun DetalleScreen(
    onVolver: () -> Unit,
    onEditar: (Long) -> Unit,
    viewModel: DetalleViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val e by viewModel.emprendedora.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var confirmarEliminar by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.eliminada.collect {
            Toast.makeText(context, "Emprendimiento eliminado", Toast.LENGTH_SHORT).show()
            onVolver()
        }
    }

    Scaffold(
        topBar = {
            BarraSuperior(e?.nombreComercial ?: "Detalle", onVolver = onVolver) {
                e?.let { emp ->
                    BotonFavorito(emp.favorita, viewModel::alternarFavorita)
                    IconButton(onClick = { compartir(context, emp) }) { Icon(Icons.Filled.Share, "Compartir") }
                    IconButton(onClick = { onEditar(emp.id) }) { Icon(Icons.Filled.Edit, "Editar") }
                    IconButton(onClick = { confirmarEliminar = true }) { Icon(Icons.Filled.Delete, "Eliminar") }
                }
            }
        }
    ) { padding ->
        val emp = e ?: return@Scaffold
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            FotoLocal(emp.fotoPath, "Foto de ${emp.nombreComercial}", Icons.Filled.Checkroom, Modifier.fillMaxWidth().height(260.dp))
            Column(Modifier.padding(20.dp)) {
                Text(emp.categoriaEtiqueta().uppercase(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                Text(emp.nombreComercial, style = MaterialTheme.typography.headlineLarge)
                Text("por ${emp.nombre}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                Text(emp.descripcion, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                FilaDato(Icons.Filled.LocationOn, "${emp.comuna}, Región ${emp.region}")
                FilaDato(Icons.Filled.Call, "+56 ${emp.telefono}")
                FilaDato(Icons.Filled.Email, emp.email)
                emp.instagram?.let { FilaDato(Icons.Filled.Storefront, "@$it") }
                if (emp.latitud != null && emp.longitud != null) {
                    FilaDato(Icons.Filled.Map, "GPS: %.5f, %.5f".format(emp.latitud, emp.longitud))
                }
                Spacer(Modifier.height(20.dp))

                // Acciones que abren apps nativas del teléfono mediante Intents
                BotonPrincipal("Llamar", { abrir(context, Intent(Intent.ACTION_DIAL, Uri.parse("tel:+56${emp.telefono}"))) }, icono = Icons.Filled.Call)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { abrir(context, Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${emp.email}"))) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.Email, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Correo")
                    }
                    OutlinedButton(onClick = { abrirMapa(context, emp) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Map, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Mapa")
                    }
                }
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
            title = { Text("¿Eliminar emprendimiento?") },
            text = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { confirmarEliminar = false; viewModel.eliminar() }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun FilaDato(icono: ImageVector, texto: String) {
    Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Text(texto, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun abrir(context: Context, intent: Intent) {
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "No hay una aplicación disponible para esta acción", Toast.LENGTH_SHORT).show()
    }
}

private fun abrirMapa(context: Context, e: Emprendedora) {
    val uri = if (e.latitud != null && e.longitud != null)
        Uri.parse("geo:${e.latitud},${e.longitud}?q=${e.latitud},${e.longitud}(${Uri.encode(e.nombreComercial)})")
    else Uri.parse("geo:0,0?q=${Uri.encode("${e.comuna}, ${e.region}, Chile")}")
    abrir(context, Intent(Intent.ACTION_VIEW, uri))
}

private fun compartir(context: Context, e: Emprendedora) {
    val texto = "Conoce a ${e.nombreComercial} (${e.categoriaEtiqueta()}) en ${e.comuna}. " +
        "Contacto: +56 ${e.telefono}. Encontrado en la app Textil Circular — textilcircular.cl"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, texto)
    }
    abrir(context, Intent.createChooser(intent, "Compartir emprendimiento"))
}
