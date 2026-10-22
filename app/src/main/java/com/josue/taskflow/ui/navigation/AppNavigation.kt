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

sealed class Pantalla(
    val ruta: String
) {
    object Inicio : Pantalla("inicio")

    object Lista : Pantalla("lista")

    object Detalle : Pantalla("detalle/{itemId}") {
        fun crearRuta(id: Int): String = "detalle/$id"
    }
}

@Composable
fun AppNavigation() {
    val applicationContext = LocalContext.current.applicationContext

    val factory = remember(applicationContext) {
        TareaViewModelFactory(applicationContext)
    }

    val tareaViewModel: TareaViewModel = viewModel(factory = factory)

    // Observa eventos one-time de confirmación desde el Channel de Room (Lab 6)
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
    val estadoLista by tareaViewModel.estadoLista.collectAsState()
    val filtroActivo by tareaViewModel.filtroActivo.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Pantalla.Inicio.ruta
    ) {
        composable(route = Pantalla.Inicio.ruta) {
            InicioScreen(
                totalTareas = uiState.totalTareas,
                tareasCompletadas = uiState.completadas,
                tareasPendientes = uiState.pendientes,
                onVerTareas = {
                    navController.navigate(Pantalla.Lista.ruta)
                }
            )
        }

        composable(route = Pantalla.Lista.ruta) {
            ListaScreen(
                estadoLista = estadoLista,
                filtroActivo = filtroActivo,
                onSeleccionarFiltro = tareaViewModel::seleccionarFiltro,
                onAgregarTarea = { titulo, descripcion ->
                    tareaViewModel.agregarTarea(titulo, descripcion)
                },
                onCambiarEstado = { id ->
                    tareaViewModel.cambiarEstadoTarea(id)
                },
                onEliminarTarea = { id ->
                    tareaViewModel.eliminarTarea(id)
                },
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
                navArgument("itemId") { type = NavType.IntType }
            )
        ) { backStack ->
            val id = backStack.arguments?.getInt("itemId") ?: 0
            val tareaSeleccionada = uiState.tareas.firstOrNull { it.id == id }

            DetalleScreen(
                tarea = tareaSeleccionada,
                onCambiarEstado = {
                    tareaViewModel.cambiarEstadoTarea(id)
                },
                onGuardarDescripcion = { descripcion ->
                    tareaViewModel.actualizarDescripcion(id, descripcion)
                },
                onEliminarTarea = {
                    tareaViewModel.eliminarTarea(id)
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}
