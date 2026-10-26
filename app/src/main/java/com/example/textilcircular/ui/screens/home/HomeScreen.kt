package com.example.textilcircular.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.textilcircular.data.local.entity.Emprendedora
import com.example.textilcircular.ui.components.ContadorAnimado
import com.example.textilcircular.ui.components.FotoLocal
import com.example.textilcircular.ui.theme.Amarillo
import com.example.textilcircular.ui.theme.Blanco
import com.example.textilcircular.ui.theme.Cyan
import com.example.textilcircular.ui.theme.HeroGradient
import com.example.textilcircular.ui.theme.Negro
import com.example.textilcircular.viewmodel.AppViewModelProvider
import com.example.textilcircular.viewmodel.HomeViewModel
import com.example.textilcircular.viewmodel.categoriaEtiqueta

@Composable
fun HomeScreen(
    onIrDirectorio: () -> Unit,
    onIrDonar: () -> Unit,
    onNuevaEmprendedora: () -> Unit,
    onVerDetalle: (Long) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    // Activa la entrada escalonada de las secciones solo la primera vez
    var mostrar by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) { mostrar = true }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero con el degradado de la web
        Column(
            Modifier
                .fillMaxWidth()
                .background(HeroGradient)
                .statusBarsPadding()
                .padding(20.dp)
        ) {
            Text(
                if (ui.nombreUsuario.isEmpty()) "¡Hola!" else "¡Hola, ${ui.nombreUsuario}!",
                style = MaterialTheme.typography.titleMedium,
                color = Cyan
            )
            Text(
                "MODA QUE CIRCULA,\nPLANETA QUE RESPIRA",
                style = MaterialTheme.typography.headlineMedium,
                color = Blanco
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Conectamos a las personas con más de 1.000 emprendimientos de economía circular textil en las 16 regiones de Chile.",
                style = MaterialTheme.typography.bodyMedium,
                color = Blanco.copy(alpha = 0.8f)
            )
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Metrica(ContadorAnimado(if (mostrar) 60 else 0, " t"), "textiles gestionados", Modifier.weight(1f))
                Metrica(ContadorAnimado(if (mostrar) 204 else 0), "familias apoyadas", Modifier.weight(1f))
                Metrica(ContadorAnimado(if (mostrar) 990 else 0, " t"), "CO₂e evitado", Modifier.weight(1f))
            }
        }

        Entrada(mostrar, retraso = 100) {
            Column(Modifier.padding(20.dp)) {
                Text("¿Qué quieres hacer?", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Accion("Explorar directorio", Icons.Filled.Storefront, Cyan, Modifier.weight(1f), onIrDirectorio)
                    Accion("Donación cuidada", Icons.Filled.VolunteerActivism, Amarillo, Modifier.weight(1f), onIrDonar)
                }
                Spacer(Modifier.height(12.dp))
                Accion("Registrar mi emprendimiento", Icons.Filled.AddBusiness, MaterialTheme.colorScheme.primaryContainer, Modifier.fillMaxWidth(), onNuevaEmprendedora)
            }
        }

        Entrada(mostrar, retraso = 250) {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Text("Tu aporte en esta app", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TarjetaDato(Icons.Filled.Storefront, ContadorAnimado(ui.totalEmprendedoras), "emprendimientos registrados", Modifier.weight(1f))
                    TarjetaDato(Icons.Filled.Inventory2, ContadorAnimado(ui.totalPrendasDonadas), "prendas comprometidas", Modifier.weight(1f))
                }
            }
        }

        Entrada(mostrar, retraso = 400) {
            Column(Modifier.padding(vertical = 20.dp)) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recién llegados", style = MaterialTheme.typography.titleLarge)
                    TextButton(onClick = onIrDirectorio) { Text("Ver todo") }
                }
                if (ui.recientes.isEmpty()) {
                    Text(
                        "Aún no hay emprendimientos. ¡Registra el primero!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(ui.recientes, key = { it.id }) { e -> TarjetaReciente(e, Modifier.animateItem()) { onVerDetalle(e.id) } }
                    }
                }
            }
        }
    }
}

@Composable
private fun Entrada(visible: Boolean, retraso: Int, contenido: @Composable () -> Unit) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500, delayMillis = retraso)) +
            slideInVertically(tween(500, delayMillis = retraso)) { it / 4 }
    ) { contenido() }
}

@Composable
private fun Metrica(valor: String, etiqueta: String, modifier: Modifier) {
    Column(modifier) {
        Text(valor, style = MaterialTheme.typography.headlineMedium, color = Amarillo)
        Text(etiqueta, style = MaterialTheme.typography.labelSmall, color = Blanco.copy(alpha = 0.8f))
    }
}

@Composable
private fun Accion(texto: String, icono: ImageVector, fondo: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = fondo, contentColor = Negro),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null)
            Spacer(Modifier.width(10.dp))
            Text(texto, style = MaterialTheme.typography.titleSmall)
        }
    }
}

@Composable
private fun TarjetaDato(icono: ImageVector, valor: String, etiqueta: String, modifier: Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(16.dp)) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(valor, style = MaterialTheme.typography.headlineLarge)
            Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TarjetaReciente(e: Emprendedora, modifier: Modifier, onClick: () -> Unit) {
    Card(modifier = modifier.width(160.dp).clickable(onClick = onClick)) {
        FotoLocal(e.fotoPath, "Foto de ${e.nombreComercial}", Icons.Filled.Checkroom, Modifier.fillMaxWidth().height(110.dp))
        Column(Modifier.padding(10.dp)) {
            Text(e.nombreComercial, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(e.categoriaEtiqueta(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Eco, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(" ${e.comuna}", style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

