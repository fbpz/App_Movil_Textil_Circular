package com.example.textilcircular

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.textilcircular.data.AppDatabase
import com.example.textilcircular.ui.theme.TextilCircularTheme
import com.example.textilcircular.view.CameraScreen
import com.example.textilcircular.view.FormularioScreen
import com.example.textilcircular.viewmodel.EmprendedoraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inicializar la Base de Datos Local
        val database = AppDatabase.getDatabase(this)
        val dao = database.emprendedoraDao()

        // 2. Crear un Factory para inyectar la BD al ViewModel
        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EmprendedoraViewModel(dao) as T
            }
        }

        setContent {
            TextilCircularTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: EmprendedoraViewModel = viewModel(factory = factory)

                    // 3. Sistema de Navegación con Animaciones de deslizamiento
                    NavHost(
                        navController = navController,
                        startDestination = "formulario",
                        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(500)) },
                        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(500)) },
                        popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(500)) },
                        popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(500)) }
                    ) {
                        composable("formulario") {
                            FormularioScreen(
                                viewModel = viewModel,
                                onNavigateToCamera = { navController.navigate("camara") }
                            )
                        }
                        composable("camara") {
                            CameraScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}