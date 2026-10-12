package com.josue.taskflow.datos.repository

import com.josue.taskflow.dominio.model.Tarea
import com.josue.taskflow.dominio.repository.TareaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object TareaRepositoryMemoria : TareaRepository {

    private val mutex = Mutex()

    private val tareas = MutableStateFlow(
        listOf(
            Tarea(
                id = 1,
                titulo = "Estudiar navegación",
                descripcion = "Repasar Inicio, Lista y Detalle."
            ),
            Tarea(
                id = 2,
                titulo = "Preparar el README",
                descripcion = "Documentar TaskFlow.",
                completada = true
            ),
            Tarea(
                id = 3,
                titulo = "Probar la aplicación",
                descripcion = "Verificar todos los botones."
            )
        )
    )

    private var siguienteId = 4

    override fun obtenerTareas(): Flow<List<Tarea>> {
        return tareas.asStateFlow()
    }

    override suspend fun agregarTarea(
        titulo: String,
        descripcion: String
    ) {
        mutex.withLock {
            val nuevaTarea = Tarea(
                id = siguienteId++,
                titulo = titulo,
                descripcion = descripcion,
                completada = false
            )

            tareas.value = tareas.value + nuevaTarea
        }
    }

    override suspend fun cambiarEstadoTarea(id: Int) {
        mutex.withLock {
            tareas.value = tareas.value.map { tarea ->
                if (tarea.id == id) {
                    tarea.copy(completada = !tarea.completada)
                } else {
                    tarea
                }
            }
        }
    }

    override suspend fun actualizarDescripcion(
        id: Int,
        descripcion: String
    ) {
        mutex.withLock {
            tareas.value = tareas.value.map { tarea ->
                if (tarea.id == id) {
                    tarea.copy(descripcion = descripcion)
                } else {
                    tarea
                }
            }
        }
    }
}