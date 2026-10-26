package com.example.textilcircular.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.textilcircular.data.local.entity.Emprendedora
import com.example.textilcircular.data.repository.AuthRepository
import com.example.textilcircular.data.repository.DonacionRepository
import com.example.textilcircular.data.repository.EmprendedoraRepository
import com.example.textilcircular.domain.model.Categoria
import com.example.textilcircular.util.ImageStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.Normalizer

// ---------------------------------------------------------------- Home

data class HomeUiState(
    val nombreUsuario: String = "",
    val totalEmprendedoras: Int = 0,
    val totalPrendasDonadas: Int = 0,
    val recientes: List<Emprendedora> = emptyList()
)

class HomeViewModel(
    authRepo: AuthRepository,
    emprendedoraRepo: EmprendedoraRepository,
    donacionRepo: DonacionRepository
) : ViewModel() {
    val ui: StateFlow<HomeUiState> = combine(
        authRepo.sesion,
        emprendedoraRepo.total,
        donacionRepo.totalPrendas,
        emprendedoraRepo.todas
    ) { sesion, total, prendas, todas ->
        HomeUiState(
            nombreUsuario = sesion?.nombre?.substringBefore(" ").orEmpty(),
            totalEmprendedoras = total,
            totalPrendasDonadas = prendas,
            recientes = todas.take(5)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}

// ---------------------------------------------------------------- Directorio

data class DirectorioUiState(
    val busqueda: String = "",
    val categoria: Categoria? = null,
    val soloFavoritas: Boolean = false,
    val resultados: List<Emprendedora> = emptyList(),
    val cargando: Boolean = true
)

class DirectorioViewModel(private val repo: EmprendedoraRepository) : ViewModel() {

    private val filtros = MutableStateFlow(DirectorioUiState())

    val ui: StateFlow<DirectorioUiState> = combine(filtros, repo.todas) { f, todas ->
        val texto = f.busqueda.normalizar()
        f.copy(
            cargando = false,
            resultados = todas.filter { e ->
                (f.categoria == null || e.categoria == f.categoria.name) &&
                    (!f.soloFavoritas || e.favorita) &&
                    (texto.isEmpty() || listOf(e.nombre, e.nombreComercial, e.comuna, e.region)
                        .any { it.normalizar().contains(texto) })
            }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DirectorioUiState())

    fun onBusqueda(texto: String) = filtros.update { it.copy(busqueda = texto) }

    fun onCategoria(categoria: Categoria?) = filtros.update {
        it.copy(categoria = if (it.categoria == categoria) null else categoria)
    }

    fun onSoloFavoritas() = filtros.update { it.copy(soloFavoritas = !it.soloFavoritas) }

    fun alternarFavorita(e: Emprendedora) {
        viewModelScope.launch { repo.alternarFavorita(e) }
    }

    /** Búsqueda insensible a mayúsculas y tildes ("Ñuñoa" == "nunoa"). */
    private fun String.normalizar(): String =
        Normalizer.normalize(trim().lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")
}

// ---------------------------------------------------------------- Detalle

class DetalleViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: EmprendedoraRepository,
    private val imageStorage: ImageStorage
) : ViewModel() {

    private val id: Long = checkNotNull(savedStateHandle.get<Long>("id"))

    val emprendedora: StateFlow<Emprendedora?> = repo.observar(id)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _eliminada = Channel<Unit>(Channel.BUFFERED)
    val eliminada = _eliminada.receiveAsFlow()

    fun alternarFavorita() {
        val e = emprendedora.value ?: return
        viewModelScope.launch { repo.alternarFavorita(e) }
    }

    fun eliminar() {
        val e = emprendedora.value ?: return
        viewModelScope.launch {
            repo.eliminar(e)
            imageStorage.eliminar(e.fotoPath)
            _eliminada.send(Unit)
        }
    }
}

/** Mapea el nombre guardado en BD a su etiqueta legible. */
fun Emprendedora.categoriaEtiqueta(): String =
    Categoria.desdeNombre(categoria)?.etiqueta ?: categoria
