package com.josue.taskflow.ui.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import kotlinx.coroutines.flow.collect

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

    object DetalleSimulado : Pantalla("detalleSimulado/{itemId}") {
        fun crearRuta(id: Int): String {
            return "detalleSimulado/$id"
        }
    }
}

@Composable
fun AppNavigation() {
    val applicationContext =
        LocalContext.current.applicationContext

    val factory = remember(applicationContext) {
        TareaViewModelFactory(applicationContext)
    }

    val tareaViewModel: TareaViewModel = viewModel(
        factory = factory
    )

    // El flujo pertenece al mismo repositorio que utiliza
    // el ViewModel, incluso después de girar la pantalla.
    LaunchedEffect(tareaViewModel) {
        tareaViewModel.confirmacionEventosFlow.collect { mensaje ->
            Toast.makeText(
                applicationContext,
                mensaje,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val navController = rememberNavController()

    val uiState by tareaViewModel.uiState.collectAsState()

    val estadoLab4 by tareaViewModel.estadoLab4.collectAsState()

    val tareasSimuladas by
    tareaViewModel.tareasSimuladasLab4.collectAsState()

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
                    navController.navigate(Pantalla.Lista.ruta)
                }
            )
        }

        composable(
            route = Pantalla.Lista.ruta
        ) {
            ListaScreen(
                tareas = uiState.tareas,
                estadoLab4 = estadoLab4,

                onIniciarDemoLab4 =
                    tareaViewModel::iniciarDemoLab4,

                onFiltroLab4 =
                    tareaViewModel::seleccionarFiltroLab4,

                onFalloLab4 =
                    tareaViewModel::simularFalloLab4,

                onErrorLab4 =
                    tareaViewModel::simularErrorLab4,

                onRecargarLab4 =
                    tareaViewModel::recargarLab4,

                onCambiarEstadoSimuladoLab4 =
                    tareaViewModel::cambiarEstadoSimuladoLab4,

                onAbrirDetalleSimuladoLab4 = { id ->
                    navController.navigate(
                        Pantalla.DetalleSimulado.crearRuta(id)
                    )
                },

                onAgregarTarea = { titulo, descripcion ->
                    tareaViewModel.agregarTarea(
                        titulo,
                        descripcion
                    )
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

        // Detalle de una tarea guardada en Room.
        composable(
            route = Pantalla.Detalle.ruta,
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.IntType
                }
            )
        ) { backStack ->
            val id = backStack.arguments?.getInt("itemId") ?: 0

            val tareaSeleccionada = uiState.tareas.firstOrNull {
                it.id == id
            }

            DetalleScreen(
                tarea = tareaSeleccionada,

                onCambiarEstado = {
                    tareaViewModel.cambiarEstadoTarea(id)
                },

                onGuardarDescripcion = { descripcion ->
                    tareaViewModel.actualizarDescripcion(
                        id,
                        descripcion
                    )
                },

                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // Detalle de una tarea simulada del LAB 4.
        composable(
            route = Pantalla.DetalleSimulado.ruta,
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.IntType
                }
            )
        ) { backStack ->
            val id = backStack.arguments?.getInt("itemId") ?: 0

            val tareaSeleccionada = tareasSimuladas.firstOrNull {
                it.id == id
            }

            DetalleScreen(
                tarea = tareaSeleccionada,

                onCambiarEstado = {
                    tareaViewModel.cambiarEstadoSimuladoLab4(id)
                },

                onGuardarDescripcion = { descripcion ->
                    tareaViewModel.actualizarDescripcionSimuladaLab4(
                        id,
                        descripcion
                    )
                },

                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}