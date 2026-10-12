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


/**
 * Repositorio de tareas con persistencia en Room.
 */
class TareaRepositoryImpl(
    private val tareaDao: TareaDao
) : TareaRepository {

    private val confirmacionChannel =
        Channel<String>(Channel.BUFFERED)

    val confirmacionEventosFlow: Flow<String> =
        confirmacionChannel.receiveAsFlow()

    override fun obtenerTareas(): Flow<List<Tarea>> {
        return tareaDao.obtenerTareas().map { entidades ->
            entidades.map { entidad ->
                entidad.toDomain()
            }
        }
    }

    override suspend fun agregarTarea(
        titulo: String,
        descripcion: String
    ) {
        tareaDao.insertarTarea(
            TareaEntity(
                titulo = titulo,
                descripcion = descripcion
            )
        )

        confirmacionChannel.send(
            "Tarea guardada: $titulo"
        )
    }

    override suspend fun cambiarEstadoTarea(id: Int) {
        val tarea = tareaDao.obtenerTareaPorId(id) ?: return

        val actualizada = tarea.copy(
            completada = !tarea.completada
        )

        tareaDao.actualizarTarea(actualizada)

        val estado = if (actualizada.completada) {
            "completada"
        } else {
            "pendiente"
        }

        confirmacionChannel.send(
            "Tarea marcada como $estado"
        )
    }

    override suspend fun actualizarDescripcion(
        id: Int,
        descripcion: String
    ) {
        val tarea = tareaDao.obtenerTareaPorId(id) ?: return

        tareaDao.actualizarTarea(
            tarea.copy(descripcion = descripcion)
        )

        confirmacionChannel.send(
            "Descripción guardada"
        )
    }
}



