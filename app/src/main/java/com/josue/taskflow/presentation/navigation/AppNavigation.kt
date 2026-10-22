package com.josue.taskflow.presentation.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.josue.taskflow.presentation.screens.DetalleScreen
import com.josue.taskflow.presentation.screens.InicioScreen
import com.josue.taskflow.presentation.screens.ListaScreen
import com.josue.taskflow.presentation.viewmodel.TareaViewModel

sealed class Pantalla(val ruta: String) {
    object Inicio : Pantalla("inicio")
    object Lista : Pantalla("lista")
    object Detalle : Pantalla("detalle/{itemId}") {
        fun crearRuta(id: Int): String = "detalle/$id"
    }
}

@Composable
fun AppNavigation(
    tareaViewModel: TareaViewModel = hiltViewModel()
) {
    val applicationContext = LocalContext.current.applicationContext

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

    val uiState by tareaViewModel.uiState.collectAsStateWithLifecycle()
    val estadoLista by tareaViewModel.estadoLista.collectAsStateWithLifecycle()
    val filtroActivo by tareaViewModel.filtroActivo.collectAsStateWithLifecycle()

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
