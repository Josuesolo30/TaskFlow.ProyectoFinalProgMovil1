package com.josue.taskflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
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

sealed class Pantalla(
    val ruta: String
) {
    object Inicio : Pantalla("inicio")
    object Lista : Pantalla("lista")
    object Detalle : Pantalla("detalle/{itemId}") {
        fun crearRuta(id: Int): String {
            return "detalle/$id"
        }
    }
}

@Composable
fun AppNavigation() {
    val context = LocalContext.current

    val tareaViewModel: TareaViewModel = viewModel(
        factory = TareaViewModelFactory(
            context.applicationContext
        )
    )

    val navController = rememberNavController()

    // Estado de las tareas reales guardadas en Room.
    val uiState by tareaViewModel.uiState.collectAsState()

    // Estado del pipeline del LAB 4.
    val estadoLab4 by tareaViewModel.estadoLab4.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Pantalla.Inicio.ruta
    ) {
        composable(
            route = Pantalla.Inicio.ruta
        ) {
            InicioScreen(
                totalTareas = uiState.totalTareas,
                tareasCompletadas = uiState.completadas,
                onVerTareas = {
                    navController.navigate(
                        Pantalla.Lista.ruta
                    )
                }
            )
        }

        composable(
            route = Pantalla.Lista.ruta
        ) {
            ListaScreen(
                tareas = uiState.tareas,
                estadoLab4 = estadoLab4,
                onIniciarDemoLab4 = tareaViewModel::iniciarDemoLab4,
                onFiltroLab4 = tareaViewModel::seleccionarFiltroLab4,
                onFalloLab4 = tareaViewModel::simularFalloLab4,
                onErrorLab4 = tareaViewModel::simularErrorLab4,
                onRecargarLab4 = tareaViewModel::recargarLab4,
                onCambiarEstadoSimuladoLab4 = tareaViewModel::cambiarEstadoSimuladoLab4,
                onAbrirDetalleSimuladoLab4 = { id ->
                    navController.navigate(
                        Pantalla.Detalle.crearRuta(id)
                    )
                },
                onAgregarTarea = { titulo ->
                    tareaViewModel.agregarTarea(titulo)
                },
                onCambiarEstado = { id ->
                    tareaViewModel.cambiarEstadoTarea(id)
                },
                onAbrirDetalle = { id ->
                    navController.navigate(
                        Pantalla.Detalle.crearRuta(id)
                    )
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

            val tareaSeleccionada = uiState.tareas.firstOrNull { tarea ->
                tarea.id == id
            }

            DetalleScreen(
                tarea = tareaSeleccionada,
                onCambiarEstado = {
                    tareaViewModel.cambiarEstadoTarea(id)
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}
