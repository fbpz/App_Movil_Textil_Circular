package com.example.textilcircular.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.textilcircular.data.preferences.Sesion
import com.example.textilcircular.data.repository.AuthRepository
import com.example.textilcircular.data.repository.ResultadoAuth
import com.example.textilcircular.domain.validation.CampoFormulario
import com.example.textilcircular.domain.validation.Validadores
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Estado de sesión: decide la pantalla inicial y permite cerrar sesión. */
class SesionViewModel(private val repo: AuthRepository) : ViewModel() {

    sealed interface Estado {
        data object Cargando : Estado
        data object SinSesion : Estado
        data class Activa(val sesion: Sesion) : Estado
    }

    val estado: StateFlow<Estado> = repo.sesion
        .map { if (it == null) Estado.SinSesion else Estado.Activa(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Estado.Cargando)

    fun cerrarSesion() {
        viewModelScope.launch { repo.cerrarSesion() }
    }
}

// ---------------------------------------------------------------- Login

data class LoginUiState(
    val email: CampoFormulario = CampoFormulario(),
    val password: CampoFormulario = CampoFormulario(),
    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    val intentosInvalidos: Int = 0   // cada incremento dispara la animación de "sacudida"
)

class LoginViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui.asStateFlow()

    private val _exito = Channel<Unit>(Channel.BUFFERED)
    val exito = _exito.receiveAsFlow()

    init {
        // Recordar el último correo usado (DataStore)
        viewModelScope.launch {
            val ultimo = repo.ultimoEmail.first()
            if (ultimo.isNotEmpty() && _ui.value.email.valor.isEmpty()) {
                _ui.update { it.copy(email = CampoFormulario(valor = ultimo)) }
            }
        }
    }

    fun onEmail(v: String) = _ui.update { it.copy(email = it.email.actualizar(v, Validadores::email), errorGeneral = null) }

    fun onPassword(v: String) = _ui.update {
        it.copy(password = it.password.actualizar(v) { p -> Validadores.requerido(p, "Ingresa tu contraseña") }, errorGeneral = null)
    }

    fun ingresar() {
        val s = _ui.value
        val validado = s.copy(
            email = s.email.validar(Validadores::email),
            password = s.password.validar { Validadores.requerido(it, "Ingresa tu contraseña") }
        )
        if (!validado.email.esValido || !validado.password.esValido) {
            _ui.value = validado.copy(intentosInvalidos = s.intentosInvalidos + 1)
            return
        }
        _ui.value = validado.copy(cargando = true, errorGeneral = null)
        viewModelScope.launch {
            when (val r = repo.login(validado.email.valor, validado.password.valor)) {
                is ResultadoAuth.Exito -> {
                    _ui.update { it.copy(cargando = false) }
                    _exito.send(Unit)
                }
                is ResultadoAuth.Error -> _ui.update {
                    it.copy(cargando = false, errorGeneral = r.mensaje, intentosInvalidos = it.intentosInvalidos + 1)
                }
            }
        }
    }
}

// ---------------------------------------------------------------- Registro

data class RegistroUiState(
    val nombre: CampoFormulario = CampoFormulario(),
    val rut: CampoFormulario = CampoFormulario(),
    val email: CampoFormulario = CampoFormulario(),
    val password: CampoFormulario = CampoFormulario(),
    val confirmacion: CampoFormulario = CampoFormulario(),
    val aceptaTerminos: Boolean = false,
    val errorTerminos: String? = null,
    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    val intentosInvalidos: Int = 0
) {
    /** Nivel de seguridad de 0 a 4, usado por la barra animada de la contraseña. */
    val fuerzaPassword: Int
        get() = listOf(
            password.valor.length >= 8,
            password.valor.any { it.isUpperCase() },
            password.valor.any { it.isDigit() },
            password.valor.any { !it.isLetterOrDigit() }
        ).count { it }
}

class RegistroViewModel(private val repo: AuthRepository) : ViewModel() {

    private val _ui = MutableStateFlow(RegistroUiState())
    val ui: StateFlow<RegistroUiState> = _ui.asStateFlow()

    private val _exito = Channel<Unit>(Channel.BUFFERED)
    val exito = _exito.receiveAsFlow()

    fun onNombre(v: String) = _ui.update { it.copy(nombre = it.nombre.actualizar(v, Validadores::nombrePersona)) }
    // Se filtran caracteres inválidos al escribir; el formato con puntos se aplica al guardar
    // (reformatear en cada tecla mueve el cursor y hace perder dígitos)
    fun onRut(v: String) = _ui.update {
        it.copy(rut = it.rut.actualizar(v.filter { c -> c.isDigit() || c in ".-kK" }.take(12), Validadores::rut))
    }
    fun onEmail(v: String) = _ui.update { it.copy(email = it.email.actualizar(v, Validadores::email), errorGeneral = null) }

    fun onPassword(v: String) = _ui.update {
        val confirmacion = if (it.confirmacion.tocado)
            it.confirmacion.validar { c -> Validadores.confirmarPassword(v, c) } else it.confirmacion
        it.copy(password = it.password.actualizar(v, Validadores::password), confirmacion = confirmacion)
    }

    fun onConfirmacion(v: String) = _ui.update {
        it.copy(confirmacion = it.confirmacion.actualizar(v) { c -> Validadores.confirmarPassword(it.password.valor, c) })
    }

    fun onTerminos(acepta: Boolean) = _ui.update {
        it.copy(aceptaTerminos = acepta, errorTerminos = Validadores.aceptaTerminos(acepta))
    }

    fun registrar() {
        val s = _ui.value
        val v = s.copy(
            nombre = s.nombre.validar(Validadores::nombrePersona),
            rut = s.rut.validar(Validadores::rut),
            email = s.email.validar(Validadores::email),
            password = s.password.validar(Validadores::password),
            confirmacion = s.confirmacion.validar { Validadores.confirmarPassword(s.password.valor, it) },
            errorTerminos = Validadores.aceptaTerminos(s.aceptaTerminos)
        )
        val valido = listOf(v.nombre, v.rut, v.email, v.password, v.confirmacion).all { it.esValido } &&
            v.errorTerminos == null
        if (!valido) {
            _ui.value = v.copy(intentosInvalidos = s.intentosInvalidos + 1)
            return
        }
        _ui.value = v.copy(cargando = true)
        viewModelScope.launch {
            when (val r = repo.registrar(v.nombre.valor, formatearRut(v.rut.valor), v.email.valor, v.password.valor)) {
                is ResultadoAuth.Exito -> {
                    _ui.update { it.copy(cargando = false) }
                    _exito.send(Unit)
                }
                is ResultadoAuth.Error -> _ui.update {
                    it.copy(
                        cargando = false,
                        email = it.email.copy(error = r.mensaje),
                        intentosInvalidos = it.intentosInvalidos + 1
                    )
                }
            }
        }
    }

    /** Normaliza el RUT al formato 12.345.678-5. */
    private fun formatearRut(entrada: String): String {
        val limpio = entrada.filter { it.isDigit() || it == 'k' || it == 'K' }.uppercase().take(9)
        if (limpio.length < 2) return limpio
        val cuerpo = limpio.dropLast(1).reversed().chunked(3).joinToString(".").reversed()
        return "$cuerpo-${limpio.last()}"
    }
}
