package com.example.textilcircular.data.repository

import com.example.textilcircular.data.local.dao.DonacionDao
import com.example.textilcircular.data.local.dao.EmprendedoraDao
import com.example.textilcircular.data.local.dao.UsuarioDao
import com.example.textilcircular.data.local.entity.Donacion
import com.example.textilcircular.data.local.entity.Emprendedora
import com.example.textilcircular.data.local.entity.Usuario
import com.example.textilcircular.data.preferences.Sesion
import com.example.textilcircular.data.preferences.SessionManager
import com.example.textilcircular.domain.security.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

sealed interface ResultadoAuth {
    data class Exito(val usuario: Usuario) : ResultadoAuth
    data class Error(val mensaje: String) : ResultadoAuth
}

class AuthRepository(
    private val usuarioDao: UsuarioDao,
    private val sessionManager: SessionManager
) {
    val sesion: Flow<Sesion?> = sessionManager.sesion
    val ultimoEmail: Flow<String> = sessionManager.ultimoEmail

    fun observarUsuario(id: Long): Flow<Usuario?> = usuarioDao.observarPorId(id)

    suspend fun registrar(nombre: String, rut: String, email: String, password: String): ResultadoAuth {
        val emailNormalizado = email.trim().lowercase()
        if (usuarioDao.buscarPorEmail(emailNormalizado) != null) {
            return ResultadoAuth.Error("Ya existe una cuenta con ese correo")
        }
        val usuario = Usuario(
            nombre = nombre.trim(),
            rut = rut.trim(),
            email = emailNormalizado,
            passwordHash = PasswordHasher.hash(password)
        )
        val id = usuarioDao.insertar(usuario)
        val guardado = usuario.copy(id = id)
        sessionManager.iniciarSesion(id, guardado.nombre, guardado.email)
        return ResultadoAuth.Exito(guardado)
    }

    suspend fun login(email: String, password: String): ResultadoAuth {
        val usuario = usuarioDao.buscarPorEmail(email.trim().lowercase())
        if (usuario == null || !PasswordHasher.verificar(password, usuario.passwordHash)) {
            return ResultadoAuth.Error("Correo o contraseña incorrectos")
        }
        sessionManager.iniciarSesion(usuario.id, usuario.nombre, usuario.email)
        return ResultadoAuth.Exito(usuario)
    }

    suspend fun sesionActual(): Sesion? = sessionManager.sesion.first()

    suspend fun cerrarSesion() = sessionManager.cerrarSesion()
}

class EmprendedoraRepository(private val dao: EmprendedoraDao) {
    val todas: Flow<List<Emprendedora>> = dao.observarTodas()
    val total: Flow<Int> = dao.contar()

    fun observar(id: Long): Flow<Emprendedora?> = dao.observarPorId(id)
    suspend fun obtener(id: Long): Emprendedora? = dao.obtenerPorId(id)
    suspend fun guardar(e: Emprendedora): Long =
        if (e.id == 0L) dao.insertar(e) else { dao.actualizar(e); e.id }
    suspend fun eliminar(e: Emprendedora) = dao.eliminar(e)
    suspend fun alternarFavorita(e: Emprendedora) = dao.marcarFavorita(e.id, !e.favorita)
}

class DonacionRepository(private val dao: DonacionDao) {
    val totalPrendas: Flow<Int> = dao.totalPrendas()
    fun deUsuario(usuarioId: Long): Flow<List<Donacion>> = dao.observarPorUsuario(usuarioId)
    suspend fun registrar(d: Donacion): Long = dao.insertar(d)
}
