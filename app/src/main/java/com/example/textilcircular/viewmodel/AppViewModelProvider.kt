package com.example.textilcircular.viewmodel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.textilcircular.TextilCircularApp
import com.example.textilcircular.di.AppContainer

/** Fábrica única de ViewModels: les entrega sus dependencias desde el AppContainer. */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { SesionViewModel(container().authRepository) }
        initializer { LoginViewModel(container().authRepository) }
        initializer { RegistroViewModel(container().authRepository) }
        initializer {
            HomeViewModel(
                container().authRepository,
                container().emprendedoraRepository,
                container().donacionRepository
            )
        }
        initializer { DirectorioViewModel(container().emprendedoraRepository) }
        initializer {
            DetalleViewModel(
                createSavedStateHandle(),
                container().emprendedoraRepository,
                container().imageStorage
            )
        }
        initializer {
            EmprendedoraFormViewModel(
                createSavedStateHandle(),
                container().emprendedoraRepository,
                container().authRepository,
                container().imageStorage,
                container().locationProvider
            )
        }
        initializer {
            DonacionViewModel(
                container().donacionRepository,
                container().authRepository,
                container().locationProvider
            )
        }
    }
}

private fun CreationExtras.container(): AppContainer =
    (this[APPLICATION_KEY] as TextilCircularApp).container
