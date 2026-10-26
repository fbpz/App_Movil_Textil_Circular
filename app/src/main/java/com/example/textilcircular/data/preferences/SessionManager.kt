package com.example.textilcircular.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "sesion")

data class Sesion(val usuarioId: Long, val nombre: String)

/** Guarda la sesión activa con DataStore para no pedir login en cada apertura. */
class SessionManager(private val context: Context) {

    private object Keys {
        val USUARIO_ID = longPreferencesKey("usuario_id")
        val NOMBRE = stringPreferencesKey("nombre")
        val ULTIMO_EMAIL = stringPreferencesKey("ultimo_email")
    }

    val sesion: Flow<Sesion?> = context.dataStore.data.map { prefs ->
        val id = prefs[Keys.USUARIO_ID] ?: return@map null
        Sesion(id, prefs[Keys.NOMBRE].orEmpty())
    }

    val ultimoEmail: Flow<String> = context.dataStore.data.map { it[Keys.ULTIMO_EMAIL].orEmpty() }

    suspend fun iniciarSesion(usuarioId: Long, nombre: String, email: String) {
        context.dataStore.edit {
            it[Keys.USUARIO_ID] = usuarioId
            it[Keys.NOMBRE] = nombre
            it[Keys.ULTIMO_EMAIL] = email
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit {
            it.remove(Keys.USUARIO_ID)
            it.remove(Keys.NOMBRE)
        }
    }
}
