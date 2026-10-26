package com.example.textilcircular.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.textilcircular.data.local.entity.Donacion
import com.example.textilcircular.data.local.entity.Emprendedora
import com.example.textilcircular.data.repository.AuthRepository
import com.example.textilcircular.data.repository.DonacionRepository
import com.example.textilcircular.data.repository.EmprendedoraRepository
import com.example.textilcircular.domain.model.Categoria
import com.example.textilcircular.domain.model.EstadoPrenda
import com.example.textilcircular.domain.model.REGIONES_CHILE
import com.example.textilcircular.domain.validation.CampoFormulario
import com.example.textilcircular.domain.validation.Validadores
import com.example.textilcircular.util.ImageStorage
import com.example.textilcircular.util.LocationProvider
import com.example.textilcircular.util.ResultadoUbicacion
import com.example.textilcircular.util.Ubicacion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

/** Estado de la lectura del GPS, compartido por los formularios que lo usan. */
sealed interface EstadoGps {
    data object Inactivo : EstadoGps
    data object Buscando : EstadoGps
    data class Obtenida(val ubicacion: Ubicacion) : EstadoGps
    data class Error(val mensaje: String) : EstadoGps
}

private fun ResultadoUbicacion.aEstadoGps(): EstadoGps = when (this) {
    is ResultadoUbicacion.Exito -> EstadoGps.Obtenida(ubicacion)
    ResultadoUbicacion.SinPermiso -> EstadoGps.Error("Permiso de ubicación denegado")
    ResultadoUbicacion.GpsDesactivado -> EstadoGps.Error("Activa el GPS del dispositivo")
    ResultadoUbicacion.NoDisponible -> EstadoGps.Error("No fue posible obtener la ubicación, intenta de nuevo")
}

/** Busca la región de la lista oficial que coincide con la que entrega el geocoder. */
private fun coincidirRegion(region: String?): String? {
    if (region.isNullOrBlank()) return null
    val r = region.lowercase().removePrefix("región").removePrefix("region").removePrefix(" de ").trim()
    return REGIONES_CHILE.firstOrNull { it.lowercase().contains(r) || r.contains(it.lowercase()) }
}

// ---------------------------------------------------------------- Emprendedora

data class EmprendedoraFormUiState(
    val modoEdicion: Boolean = false,
    val nombre: CampoFormulario = CampoFormulario(),
    val nombreComercial: CampoFormulario = CampoFormulario(),
    val categoria: Categoria? = null,
    val errorCategoria: String? = null,
    val region: String? = null,
    val errorRegion: String? = null,
    val comuna: CampoFormulario = CampoFormulario(),
    val telefono: CampoFormulario = CampoFormulario(),
    val email: CampoFormulario = CampoFormulario(),
    val instagram: CampoFormulario = CampoFormulario(),
    val descripcion: CampoFormulario = CampoFormulario(),
    val fotoPath: String? = null,
    val errorFoto: String? = null,
    val gps: EstadoGps = EstadoGps.Inactivo,
    val latitud: Double? = null,
    val longitud: Double? = null,
    val guardando: Boolean = false,
    val intentosInvalidos: Int = 0
)

class EmprendedoraFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: EmprendedoraRepository,
    private val authRepo: AuthRepository,
    private val imageStorage: ImageStorage,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val idEdicion: Long = savedStateHandle.get<Long>("id") ?: -1L
    private var original: Emprendedora? = null
    private var fotoPendienteCamara: File? = null
    private val fotosDescartables = mutableListOf<String>()

    private val _ui = MutableStateFlow(EmprendedoraFormUiState())
    val ui: StateFlow<EmprendedoraFormUiState> = _ui.asStateFlow()

    private val _guardado = Channel<Long>(Channel.BUFFERED)
    val guardado = _guardado.receiveAsFlow()

    init {
        if (idEdicion > 0) viewModelScope.launch {
            repo.obtener(idEdicion)?.let { e ->
                original = e
                _ui.value = EmprendedoraFormUiState(
                    modoEdicion = true,
                    nombre = CampoFormulario(e.nombre),
                    nombreComercial = CampoFormulario(e.nombreComercial),
                    categoria = Categoria.desdeNombre(e.categoria),
                    region = e.region,
                    comuna = CampoFormulario(e.comuna),
                    telefono = CampoFormulario(e.telefono),
                    email = CampoFormulario(e.email),
                    instagram = CampoFormulario(e.instagram.orEmpty()),
                    descripcion = CampoFormulario(e.descripcion),
                    fotoPath = e.fotoPath,
                    latitud = e.latitud,
                    longitud = e.longitud
                )
            }
        }
    }

    fun onNombre(v: String) = _ui.update { it.copy(nombre = it.nombre.actualizar(v, Validadores::nombrePersona)) }
    fun onNombreComercial(v: String) = _ui.update { it.copy(nombreComercial = it.nombreComercial.actualizar(v, Validadores::nombreComercial)) }
    fun onCategoria(c: Categoria) = _ui.update { it.copy(categoria = c, errorCategoria = null) }
    fun onRegion(r: String) = _ui.update { it.copy(region = r, errorRegion = null) }
    fun onComuna(v: String) = _ui.update { it.copy(comuna = it.comuna.actualizar(v, Validadores::comuna)) }
    fun onTelefono(v: String) = _ui.update { it.copy(telefono = it.telefono.actualizar(v.take(15), Validadores::telefonoChile)) }
    fun onEmail(v: String) = _ui.update { it.copy(email = it.email.actualizar(v, Validadores::email)) }
    fun onInstagram(v: String) = _ui.update { it.copy(instagram = it.instagram.actualizar(v, Validadores::instagramOpcional)) }
    fun onDescripcion(v: String) = _ui.update { it.copy(descripcion = it.descripcion.actualizar(v.take(300)) { d -> Validadores.descripcion(d) }) }

    // ------------------------------------------------ Cámara / galería

    /** Prepara el archivo donde la cámara guardará la foto y devuelve su Uri segura. */
    fun prepararFotoCamara(): Uri {
        val (archivo, uri) = imageStorage.crearArchivoParaCamara()
        fotoPendienteCamara = archivo
        return uri
    }

    fun resultadoCamara(exito: Boolean) {
        val archivo = fotoPendienteCamara ?: return
        fotoPendienteCamara = null
        if (exito && archivo.length() > 0) reemplazarFoto(archivo.absolutePath) else archivo.delete()
    }

    fun resultadoGaleria(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            val path = imageStorage.copiarDesdeGaleria(uri)
            if (path != null) reemplazarFoto(path)
            else _ui.update { it.copy(errorFoto = "No se pudo cargar la imagen seleccionada") }
        }
    }

    fun quitarFoto() {
        _ui.value.fotoPath?.let { if (it != original?.fotoPath) imageStorage.eliminar(it) }
        _ui.update { it.copy(fotoPath = null) }
    }

    private fun reemplazarFoto(nuevoPath: String) {
        _ui.value.fotoPath?.let { anterior ->
            if (anterior == original?.fotoPath) fotosDescartables += anterior else imageStorage.eliminar(anterior)
        }
        _ui.update { it.copy(fotoPath = nuevoPath, errorFoto = null) }
    }

    // ------------------------------------------------ GPS

    fun permisoUbicacionDenegado() = _ui.update {
        it.copy(gps = EstadoGps.Error("Sin permiso de ubicación. Puedes ingresar la comuna manualmente."))
    }

    fun obtenerUbicacion() {
        _ui.update { it.copy(gps = EstadoGps.Buscando) }
        viewModelScope.launch {
            val estado = locationProvider.obtenerUbicacion().aEstadoGps()
            _ui.update { s ->
                if (estado is EstadoGps.Obtenida) {
                    val u = estado.ubicacion
                    s.copy(
                        gps = estado,
                        latitud = u.latitud,
                        longitud = u.longitud,
                        comuna = u.comuna?.let { c -> s.comuna.actualizar(c, Validadores::comuna) } ?: s.comuna,
                        region = coincidirRegion(u.region) ?: s.region,
                        errorRegion = if (coincidirRegion(u.region) != null) null else s.errorRegion
                    )
                } else s.copy(gps = estado)
            }
        }
    }

    // ------------------------------------------------ Guardar

    fun guardar() {
        val s = _ui.value
        val v = s.copy(
            nombre = s.nombre.validar(Validadores::nombrePersona),
            nombreComercial = s.nombreComercial.validar(Validadores::nombreComercial),
            errorCategoria = Validadores.seleccion(s.categoria?.name, "una categoría"),
            errorRegion = Validadores.seleccion(s.region, "una región"),
            comuna = s.comuna.validar(Validadores::comuna),
            telefono = s.telefono.validar(Validadores::telefonoChile),
            email = s.email.validar(Validadores::email),
            instagram = s.instagram.validar(Validadores::instagramOpcional),
            descripcion = s.descripcion.validar { Validadores.descripcion(it) },
            errorFoto = if (s.fotoPath == null) "Agrega una foto de tus productos" else null
        )
        val camposOk = listOf(v.nombre, v.nombreComercial, v.comuna, v.telefono, v.email, v.instagram, v.descripcion)
            .all { it.esValido }
        if (!camposOk || v.errorCategoria != null || v.errorRegion != null || v.errorFoto != null) {
            _ui.value = v.copy(intentosInvalidos = s.intentosInvalidos + 1)
            return
        }
        _ui.value = v.copy(guardando = true)
        viewModelScope.launch {
            val usuarioId = authRepo.sesionActual()?.usuarioId ?: 0L
            val instagram = v.instagram.valor.trim().removePrefix("@").ifEmpty { null }
            val entidad = (original ?: Emprendedora(
                nombre = "", nombreComercial = "", categoria = "", region = "", comuna = "",
                telefono = "", email = "", descripcion = "", creadaPor = usuarioId
            )).copy(
                nombre = v.nombre.valor.trim(),
                nombreComercial = v.nombreComercial.valor.trim(),
                categoria = v.categoria!!.name,
                region = v.region!!,
                comuna = v.comuna.valor.trim(),
                telefono = v.telefono.valor.filter { it.isDigit() }.takeLast(9),
                email = v.email.valor.trim().lowercase(),
                instagram = instagram,
                descripcion = v.descripcion.valor.trim(),
                fotoPath = v.fotoPath,
                latitud = v.latitud,
                longitud = v.longitud
            )
            val id = repo.guardar(entidad)
            fotosDescartables.forEach(imageStorage::eliminar)
            fotosDescartables.clear()
            _ui.update { it.copy(guardando = false) }
            _guardado.send(id)
        }
    }
}

// ---------------------------------------------------------------- Donación Cuidada

data class DonacionUiState(
    val nombreContacto: CampoFormulario = CampoFormulario(),
    val telefono: CampoFormulario = CampoFormulario(),
    val region: String? = null,
    val errorRegion: String? = null,
    val comuna: CampoFormulario = CampoFormulario(),
    val tipoPrenda: String? = null,
    val errorTipoPrenda: String? = null,
    val cantidad: CampoFormulario = CampoFormulario(),
    val estado: EstadoPrenda? = null,
    val errorEstado: String? = null,
    val comentario: CampoFormulario = CampoFormulario(),
    val gps: EstadoGps = EstadoGps.Inactivo,
    val guardando: Boolean = false,
    val intentosInvalidos: Int = 0,
    val mostrarExito: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class DonacionViewModel(
    private val repo: DonacionRepository,
    private val authRepo: AuthRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _ui = MutableStateFlow(DonacionUiState())
    val ui: StateFlow<DonacionUiState> = _ui.asStateFlow()

    val historial: StateFlow<List<Donacion>> = authRepo.sesion
        .flatMapLatest { s -> if (s == null) flowOf(emptyList()) else repo.deUsuario(s.usuarioId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        // Precarga el nombre del usuario en sesión
        viewModelScope.launch {
            authRepo.sesionActual()?.let { s -> _ui.update { it.copy(nombreContacto = CampoFormulario(s.nombre)) } }
        }
    }

    fun onNombre(v: String) = _ui.update { it.copy(nombreContacto = it.nombreContacto.actualizar(v, Validadores::nombrePersona)) }
    fun onTelefono(v: String) = _ui.update { it.copy(telefono = it.telefono.actualizar(v.take(15), Validadores::telefonoChile)) }
    fun onRegion(r: String) = _ui.update { it.copy(region = r, errorRegion = null) }
    fun onComuna(v: String) = _ui.update { it.copy(comuna = it.comuna.actualizar(v, Validadores::comuna)) }
    fun onTipoPrenda(t: String) = _ui.update { it.copy(tipoPrenda = t, errorTipoPrenda = null) }
    fun onCantidad(v: String) = _ui.update { it.copy(cantidad = it.cantidad.actualizar(v.filter(Char::isDigit).take(4)) { c -> Validadores.cantidad(c) }) }
    fun onEstado(e: EstadoPrenda) = _ui.update { it.copy(estado = e, errorEstado = null) }
    fun onComentario(v: String) = _ui.update { it.copy(comentario = it.comentario.actualizar(v.take(200)) { c -> Validadores.comentarioOpcional(c) }) }

    fun permisoUbicacionDenegado() = _ui.update {
        it.copy(gps = EstadoGps.Error("Sin permiso de ubicación. Ingresa la comuna manualmente."))
    }

    fun obtenerUbicacion() {
        _ui.update { it.copy(gps = EstadoGps.Buscando) }
        viewModelScope.launch {
            val estado = locationProvider.obtenerUbicacion().aEstadoGps()
            _ui.update { s ->
                if (estado is EstadoGps.Obtenida) {
                    val region = coincidirRegion(estado.ubicacion.region)
                    s.copy(
                        gps = estado,
                        comuna = estado.ubicacion.comuna?.let { c -> s.comuna.actualizar(c, Validadores::comuna) } ?: s.comuna,
                        region = region ?: s.region,
                        errorRegion = if (region != null) null else s.errorRegion
                    )
                } else s.copy(gps = estado)
            }
        }
    }

    fun enviar() {
        val s = _ui.value
        val v = s.copy(
            nombreContacto = s.nombreContacto.validar(Validadores::nombrePersona),
            telefono = s.telefono.validar(Validadores::telefonoChile),
            errorRegion = Validadores.seleccion(s.region, "una región"),
            comuna = s.comuna.validar(Validadores::comuna),
            errorTipoPrenda = Validadores.seleccion(s.tipoPrenda, "el tipo de prenda"),
            cantidad = s.cantidad.validar { Validadores.cantidad(it) },
            errorEstado = if (s.estado == null) "Indica el estado de las prendas" else null,
            comentario = s.comentario.validar { Validadores.comentarioOpcional(it) }
        )
        val ok = listOf(v.nombreContacto, v.telefono, v.comuna, v.cantidad, v.comentario).all { it.esValido } &&
            v.errorRegion == null && v.errorTipoPrenda == null && v.errorEstado == null
        if (!ok) {
            _ui.value = v.copy(intentosInvalidos = s.intentosInvalidos + 1)
            return
        }
        _ui.value = v.copy(guardando = true)
        viewModelScope.launch {
            val usuarioId = authRepo.sesionActual()?.usuarioId ?: 0L
            repo.registrar(
                Donacion(
                    usuarioId = usuarioId,
                    nombreContacto = v.nombreContacto.valor.trim(),
                    telefono = v.telefono.valor.filter { it.isDigit() }.takeLast(9),
                    region = v.region!!,
                    comuna = v.comuna.valor.trim(),
                    tipoPrenda = v.tipoPrenda!!,
                    cantidad = v.cantidad.valor.toInt(),
                    estado = v.estado!!.name,
                    comentario = v.comentario.valor.trim().ifEmpty { null }
                )
            )
            _ui.update { it.copy(guardando = false, mostrarExito = true) }
        }
    }

    /** Cierra el diálogo de éxito y deja el formulario listo para otra donación. */
    fun cerrarExito() = _ui.update {
        DonacionUiState(nombreContacto = it.nombreContacto, telefono = it.telefono, region = it.region, comuna = it.comuna)
    }
}
