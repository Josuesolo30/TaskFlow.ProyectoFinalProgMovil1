package com.josue.taskflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.josue.taskflow.presentacion.viewmodel.TareaViewModel
import com.josue.taskflow.presentacion.viewmodel.TareaViewModelFactory
import com.josue.taskflow.ui.screens.DetalleScreen
import com.josue.taskflow.ui.screens.InicioScreen
import com.josue.taskflow.ui.screens.ListaScreen

/**
 * Las rutas se concentran en una sola clase sellada de navegación.
 */
sealed class Pantalla(val ruta: String) {
    object Inicio : Pantalla("inicio")
    object Lista : Pantalla("lista")
    object Detalle : Pantalla("detalle/{itemId}") {
        fun crearRuta(id: Int): String = "detalle/$id"
    }
}

@Composable
fun AppNavigation(
    tareaViewModel: TareaViewModel = viewModel(factory = TareaViewModelFactory())
) {
    val navController = rememberNavController()

    // Estado expuesto por el ViewModel mediante StateFlow (UDF: State Down, Events Up)
    val uiState by tareaViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Pantalla.Inicio.ruta
    ) {
        composable(Pantalla.Inicio.ruta) {
            InicioScreen(
                totalTareas = uiState.totalTareas,
                tareasCompletadas = uiState.completadas,
                onVerTareas = {
                    navController.navigate(Pantalla.Lista.ruta)
                }
            )
        }

        composable(Pantalla.Lista.ruta) {
            ListaScreen(
                tareas = uiState.tareas,
                onAgregarTarea = { titulo -> tareaViewModel.agregarTarea(titulo) },
                onCambiarEstado = { id -> tareaViewModel.cambiarEstadoTarea(id) },
                onAbrirDetalle = { id ->
                    navController.navigate(Pantalla.Detalle.crearRuta(id))
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Pantalla.Detalle.ruta,
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.IntType
                }
            )
        ) { backStack ->
            val id = backStack.arguments?.getInt("itemId") ?: 0
            val tareaSeleccionada = uiState.tareas.firstOrNull { it.id == id }

            DetalleScreen(
                tarea = tareaSeleccionada,
                onCambiarEstado = { tareaViewModel.cambiarEstadoTarea(id) },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}
