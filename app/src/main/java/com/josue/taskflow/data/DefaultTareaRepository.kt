package com.josue.taskflow.data

import com.josue.taskflow.data.local.TareaEntity
import com.josue.taskflow.data.local.TareaLocalDataSource
import com.josue.taskflow.data.local.toDomain
import com.josue.taskflow.domain.model.Tarea
import com.josue.taskflow.domain.repository.TareaRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación principal del contrato TareaRepository usando Room vía TareaLocalDataSource.
 */
@Singleton
class DefaultTareaRepository @Inject constructor(
    private val localDataSource: TareaLocalDataSource
) : TareaRepository {

    private val confirmacionChannel = Channel<String>(Channel.BUFFERED)

    override fun getConfirmacionesStream(): Flow<String> {
        return confirmacionChannel.receiveAsFlow()
    }

    override fun getTareasStream(): Flow<List<Tarea>> {
        return localDataSource.getTareasStream().map { entidades ->
            entidades.map { it.toDomain() }
        }
    }

    override suspend fun agregarTarea(titulo: String, descripcion: String) {
        localDataSource.insertarTarea(
            TareaEntity(
                titulo = titulo,
                descripcion = descripcion
            )
        )
        confirmacionChannel.send("Tarea guardada: $titulo")
    }

    override suspend fun cambiarEstadoTarea(id: Int) {
        val tarea = localDataSource.getTareaPorId(id) ?: return
        val actualizada = tarea.copy(completada = !tarea.completada)
        localDataSource.actualizarTarea(actualizada)

        val estado = if (actualizada.completada) "completada" else "pendiente"
        confirmacionChannel.send("Tarea marcada como $estado")
    }

    override suspend fun actualizarDescripcion(id: Int, descripcion: String) {
        val tarea = localDataSource.getTareaPorId(id) ?: return
        localDataSource.actualizarTarea(tarea.copy(descripcion = descripcion))
        confirmacionChannel.send("Descripción guardada")
    }

    override suspend fun eliminarTarea(id: Int) {
        val tarea = localDataSource.getTareaPorId(id)
        localDataSource.eliminarTarea(id)
        val titulo = tarea?.titulo ?: "Tarea"
        confirmacionChannel.send("Tarea eliminada: $titulo")
    }
}
