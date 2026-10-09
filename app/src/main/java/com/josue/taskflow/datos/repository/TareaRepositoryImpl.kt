package com.josue.taskflow.datos.repository

import com.josue.taskflow.dominio.model.Tarea
import com.josue.taskflow.dominio.repository.TareaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Capa de Datos: Implementación del repositorio usando una fuente de datos en memoria.
 */
class TareaRepositoryImpl : TareaRepository {

    private var siguienteId = 4

    private val _tareasFlow = MutableStateFlow(
        listOf(
            Tarea(
                id = 1,
                titulo = "Estudiar navegación",
                descripcion = "Repasar NavController, NavHost y las rutas de cada pantalla."
            ),
            Tarea(
                id = 2,
                titulo = "Preparar el README",
                descripcion = "Documentar funcionalidades, tecnologías y forma de ejecutar la aplicación.",
                completada = true
            ),
            Tarea(
                id = 3,
                titulo = "Probar en el AVD",
                descripcion = "Ejecutar las tres pantallas y verificar todos los botones."
            )
        )
    )

    override fun obtenerTareas(): Flow<List<Tarea>> {
        return _tareasFlow.asStateFlow()
    }

    override suspend fun agregarTarea(titulo: String) {
        _tareasFlow.update { listaActual ->
            val nuevaTarea = Tarea(
                id = siguienteId++,
                titulo = titulo,
                descripcion = "Tarea creada por el usuario."
            )
            listaActual + nuevaTarea
        }
    }

    override suspend fun cambiarEstadoTarea(id: Int) {
        _tareasFlow.update { listaActual ->
            listaActual.map { tarea ->
                if (tarea.id == id) {
                    tarea.copy(completada = !tarea.completada)
                } else {
                    tarea
                }
            }
        }
    }
}
