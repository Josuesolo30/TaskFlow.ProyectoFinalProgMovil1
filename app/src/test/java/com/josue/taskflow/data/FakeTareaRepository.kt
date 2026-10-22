package com.josue.taskflow.data

import com.josue.taskflow.domain.model.Tarea
import com.josue.taskflow.domain.repository.TareaRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Fake de TareaRepository para ser utilizado exclusivamente en las pruebas unitarias.
 */
class FakeTareaRepository : TareaRepository {

    private val mutex = Mutex()
    private val confirmacionChannel = Channel<String>(Channel.BUFFERED)

    private val tareas = MutableStateFlow(
        listOf(
            Tarea(id = 1, titulo = "Estudiar navegación", descripcion = "Repasar Inicio, Lista y Detalle."),
            Tarea(id = 2, titulo = "Preparar el README", descripcion = "Documentar TaskFlow.", completada = true),
            Tarea(id = 3, titulo = "Probar la aplicación", descripcion = "Verificar todos los botones.")
        )
    )

    private var siguienteId = 4

    override fun getTareasStream(): Flow<List<Tarea>> = tareas.asStateFlow()

    override fun getConfirmacionesStream(): Flow<String> = confirmacionChannel.receiveAsFlow()

    override suspend fun agregarTarea(titulo: String, descripcion: String) {
        mutex.withLock {
            val nuevaTarea = Tarea(
                id = siguienteId++,
                titulo = titulo,
                descripcion = descripcion,
                completada = false
            )
            tareas.value = tareas.value + nuevaTarea
            confirmacionChannel.send("Tarea guardada: $titulo")
        }
    }

    override suspend fun cambiarEstadoTarea(id: Int) {
        mutex.withLock {
            tareas.value = tareas.value.map { tarea ->
                if (tarea.id == id) tarea.copy(completada = !tarea.completada) else tarea
            }
            confirmacionChannel.send("Estado actualizado")
        }
    }

    override suspend fun actualizarDescripcion(id: Int, descripcion: String) {
        mutex.withLock {
            tareas.value = tareas.value.map { tarea ->
                if (tarea.id == id) tarea.copy(descripcion = descripcion) else tarea
            }
            confirmacionChannel.send("Descripción guardada")
        }
    }

    override suspend fun eliminarTarea(id: Int) {
        mutex.withLock {
            tareas.value = tareas.value.filter { it.id != id }
            confirmacionChannel.send("Tarea eliminada")
        }
    }
}
