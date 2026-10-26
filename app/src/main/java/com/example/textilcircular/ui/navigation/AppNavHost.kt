package com.example.textilcircular.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.textilcircular.ui.screens.auth.LoginScreen
import com.example.textilcircular.ui.screens.auth.RegistroScreen
import com.example.textilcircular.ui.screens.directorio.DetalleScreen
import com.example.textilcircular.ui.screens.directorio.DirectorioScreen
import com.example.textilcircular.ui.screens.donacion.DonacionScreen
import com.example.textilcircular.ui.screens.emprendedora.FormularioEmprendedoraScreen
import com.example.textilcircular.ui.screens.home.HomeScreen
import com.example.textilcircular.ui.screens.perfil.PerfilScreen
import com.example.textilcircular.ui.screens.splash.SplashScreen
import com.example.textilcircular.ui.theme.Carbon
import com.example.textilcircular.ui.theme.Cyan
import com.example.textilcircular.ui.theme.GrisMedio
import com.example.textilcircular.ui.theme.Negro

object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val HOME = "home"
    const val DIRECTORIO = "directorio"
    const val DETALLE = "detalle/{id}"
    const val FORMULARIO = "formulario?id={id}"
    const val DONACION = "donacion"
    const val PERFIL = "perfil"

    fun detalle(id: Long) = "detalle/$id"
    fun formulario(id: Long? = null) = if (id == null) "formulario" else "formulario?id=$id"
}

private data class ItemNav(val ruta: String, val etiqueta: String, val icono: ImageVector, val iconoSeleccionado: ImageVector)

private val itemsNav = listOf(
    ItemNav(Rutas.HOME, "Inicio", Icons.Outlined.Home, Icons.Filled.Home),
    ItemNav(Rutas.DIRECTORIO, "Directorio", Icons.Outlined.Storefront, Icons.Filled.Storefront),
    ItemNav(Rutas.DONACION, "Donar", Icons.Outlined.VolunteerActivism, Icons.Filled.VolunteerActivism),
    ItemNav(Rutas.PERFIL, "Perfil", Icons.Outlined.Person, Icons.Filled.Person)
)

private const val DURACION = 400

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val mostrarBarra = itemsNav.any { it.ruta == rutaActual }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            AnimatedVisibility(
                visible = mostrarBarra,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                BarraNavegacion(rutaActual) { ruta -> navController.navegarPestana(ruta) }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.SPLASH,
            modifier = Modifier.padding(padding).consumeWindowInsets(padding),
            enterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(DURACION)) + fadeIn(tween(DURACION))
            },
            exitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(DURACION)) + fadeOut(tween(DURACION))
            },
            popEnterTransition = {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(DURACION)) + fadeIn(tween(DURACION))
            },
            popExitTransition = {
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(DURACION)) + fadeOut(tween(DURACION))
            }
        ) {
            composable(
                Rutas.SPLASH,
                exitTransition = { fadeOut(tween(DURACION)) }
            ) {
                SplashScreen(onTerminar = { haySesion ->
                    navController.navigate(if (haySesion) Rutas.HOME else Rutas.LOGIN) {
                        popUpTo(Rutas.SPLASH) { inclusive = true }
                    }
                })
            }
            composable(Rutas.LOGIN, enterTransition = { fadeIn(tween(DURACION)) }) {
                LoginScreen(
                    onLoginExitoso = { navController.irAInicioLimpiando() },
                    onIrRegistro = { navController.navigate(Rutas.REGISTRO) }
                )
            }
            composable(Rutas.REGISTRO) {
                RegistroScreen(
                    onRegistrado = { navController.irAInicioLimpiando() },
                    onVolver = { navController.popBackStack() }
                )
            }

            // Pestañas principales: transición de fundido para no confundir con "avanzar"
            composable(Rutas.HOME, enterTransition = { fadeIn(tween(DURACION)) }, exitTransition = { fadeOut(tween(DURACION)) }) {
                HomeScreen(
                    onIrDirectorio = { navController.navegarPestana(Rutas.DIRECTORIO) },
                    onIrDonar = { navController.navegarPestana(Rutas.DONACION) },
                    onNuevaEmprendedora = { navController.navigate(Rutas.formulario()) },
                    onVerDetalle = { navController.navigate(Rutas.detalle(it)) }
                )
            }
            composable(Rutas.DIRECTORIO, enterTransition = { fadeIn(tween(DURACION)) }, exitTransition = { fadeOut(tween(DURACION)) }) {
                DirectorioScreen(
                    onVerDetalle = { navController.navigate(Rutas.detalle(it)) },
                    onNueva = { navController.navigate(Rutas.formulario()) }
                )
            }
            composable(Rutas.DONACION, enterTransition = { fadeIn(tween(DURACION)) }, exitTransition = { fadeOut(tween(DURACION)) }) {
                DonacionScreen()
            }
            composable(Rutas.PERFIL, enterTransition = { fadeIn(tween(DURACION)) }, exitTransition = { fadeOut(tween(DURACION)) }) {
                PerfilScreen(onSesionCerrada = {
                    navController.navigate(Rutas.LOGIN) { popUpTo(navController.graph.id) { inclusive = true } }
                })
            }

            composable(
                Rutas.DETALLE,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) {
                DetalleScreen(
                    onVolver = { navController.popBackStack() },
                    onEditar = { navController.navigate(Rutas.formulario(it)) }
                )
            }
            composable(
                Rutas.FORMULARIO,
                arguments = listOf(navArgument("id") { type = NavType.LongType; defaultValue = -1L })
            ) {
                FormularioEmprendedoraScreen(
                    onVolver = { navController.popBackStack() },
                    onGuardado = { id ->
                        navController.popBackStack()
                        // Si era un registro nuevo, se abre su ficha recién creada
                        if (navController.currentBackStackEntry?.destination?.route != Rutas.DETALLE) {
                            navController.navigate(Rutas.detalle(id))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun BarraNavegacion(rutaActual: String?, onNavegar: (String) -> Unit) {
    NavigationBar(containerColor = Carbon) {
        itemsNav.forEach { item ->
            val seleccionado = rutaActual == item.ruta
            NavigationBarItem(
                selected = seleccionado,
                onClick = { onNavegar(item.ruta) },
                icon = { Icon(if (seleccionado) item.iconoSeleccionado else item.icono, contentDescription = item.etiqueta) },
                label = { Text(item.etiqueta) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Negro,
                    selectedTextColor = Cyan,
                    indicatorColor = Cyan,
                    unselectedIconColor = GrisMedio,
                    unselectedTextColor = GrisMedio
                )
            )
        }
    }
}

/** Navegación entre pestañas sin apilar pantallas repetidas y conservando su estado. */
private fun NavHostController.navegarPestana(ruta: String) {
    navigate(ruta) {
        popUpTo(Rutas.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavHostController.irAInicioLimpiando() {
    navigate(Rutas.HOME) { popUpTo(graph.id) { inclusive = true } }
}
