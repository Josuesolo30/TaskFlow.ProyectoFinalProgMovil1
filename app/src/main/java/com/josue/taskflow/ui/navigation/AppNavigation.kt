package com.josue.taskflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.josue.taskflow.data.Tarea
import com.josue.taskflow.data.TareaRepository
import com.josue.taskflow.ui.screens.DetalleScreen
import com.josue.taskflow.ui.screens.InicioScreen
import com.josue.taskflow.ui.screens.ListaScreen

/**
 * Las rutas se concentran en una sola clase para evitar errores al escribirlas.
 */
sealed class Pantalla(val ruta: String) {
    object Inicio : Pantalla("inicio")
    object Lista : Pantalla("lista")
    object Detalle : Pantalla("detalle/{itemId}") {
        fun crearRuta(id: Int): String = "detalle/$id"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Estado compartido por ListaScreen y DetalleScreen.
    val tareas = remember {
        mutableStateListOf<Tarea>().apply {
            addAll(TareaRepository.tareasIniciales)
        }
    }

    var siguienteId by remember { mutableStateOf(4) }

    fun agregarTarea(titulo: String) {
        val tituloLimpio = titulo.trim()
        if (tituloLimpio.isBlank()) return

        tareas.add(
            Tarea(
                id = siguienteId,
                titulo = tituloLimpio,
                descripcion = "Tarea creada por el usuario."
            )
        )
        siguienteId++
    }

    fun cambiarEstado(id: Int) {
        val indice = tareas.indexOfFirst { it.id == id }
        if (indice != -1) {
            val tareaActual = tareas[indice]
            tareas[indice] = tareaActual.copy(
                completada = !tareaActual.completada
            )
        }
    }

    NavHost(
        navController = navController,
        startDestination = Pantalla.Inicio.ruta
    ) {
        composable(Pantalla.Inicio.ruta) {
            InicioScreen(
                totalTareas = tareas.size,
                tareasCompletadas = tareas.count { it.completada },
                onVerTareas = {
                    navController.navigate(Pantalla.Lista.ruta)
                }
            )
        }

        composable(Pantalla.Lista.ruta) {
            ListaScreen(
                tareas = tareas,
                onAgregarTarea = ::agregarTarea,
                onCambiarEstado = ::cambiarEstado,
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
            val tareaSeleccionada = tareas.firstOrNull { it.id == id }

            DetalleScreen(
                tarea = tareaSeleccionada,
                onCambiarEstado = { cambiarEstado(id) },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}
