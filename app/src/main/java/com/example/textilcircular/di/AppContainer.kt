package com.example.textilcircular.di

import android.content.Context
import com.example.textilcircular.data.local.AppDatabase
import com.example.textilcircular.data.preferences.SessionManager
import com.example.textilcircular.data.repository.AuthRepository
import com.example.textilcircular.data.repository.DonacionRepository
import com.example.textilcircular.data.repository.EmprendedoraRepository
import com.example.textilcircular.util.ImageStorage
import com.example.textilcircular.util.LocationProvider

/** Inyección de dependencias manual: un único lugar donde se construyen las capas de datos. */
class AppContainer(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val sessionManager = SessionManager(context)

    val authRepository = AuthRepository(database.usuarioDao(), sessionManager)
    val emprendedoraRepository = EmprendedoraRepository(database.emprendedoraDao())
    val donacionRepository = DonacionRepository(database.donacionDao())
    val imageStorage = ImageStorage(context)
    val locationProvider = LocationProvider(context)
}
