package com.josue.taskflow.datos.repository

import com.josue.taskflow.datos.db.TareaDao
import com.josue.taskflow.datos.db.TareaEntity
import com.josue.taskflow.datos.db.toDomain
import com.josue.taskflow.dominio.model.Tarea
import com.josue.taskflow.dominio.repository.TareaRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Capa de Datos: Implementación de TareaRepository usando la base de datos Room.
 * 
 * Cumple con los requerimientos del Lab 6:
 * - Reemplaza la fuente en memoria por Room.
 * - Implementa la misma interfaz TareaRepository de la capa de Dominio expuesta como Flow.
 * - Incluye un evento one-time con Channel para confirmaciones ("elemento guardado").
 */
class TareaRepositoryImpl(
    private val tareaDao: TareaDao,
) : TareaRepository {

    // Canal para eventos one-time de confirmación (ej. "elemento guardado")
    private val _confirmacionChannel = Channel<String>(Channel.BUFFERED)
    val confirmacionEventosFlow: Flow<String> = _confirmacionChannel.receiveAsFlow()

    override fun obtenerTareas(): Flow<List<Tarea>> {
        return tareaDao.obtenerTareas().map { listaEntities ->
            listaEntities.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun agregarTarea(titulo: String) {
        val nuevaTarea = TareaEntity(
            titulo = titulo,
            descripcion = "Tarea guardada en la base de datos Room."
        )
        tareaDao.insertarTarea(nuevaTarea)
        _confirmacionChannel.send("Elemento guardado en Room: '$titulo'")
    }

    override suspend fun cambiarEstadoTarea(id: Int) {
        val tareaExistente = tareaDao.obtenerTareaPorId(id)
        if (tareaExistente != null) {
            val tareaActualizada = tareaExistente.copy(
                completada = !tareaExistente.completada
            )
            tareaDao.actualizarTarea(tareaActualizada)
            val estadoText = if (tareaActualizada.completada) "completada" else "pendiente"
            _confirmacionChannel.send("Estado actualizado a $estadoText (ID: $id)")
        }
    }
}
