package com.josue.taskflow.data.local

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Fuente de datos local para operaciones sobre Room.
 */
class TareaLocalDataSource @Inject constructor(
    private val tareaDao: TareaDao
) {
    fun getTareasStream(): Flow<List<TareaEntity>> {
        return tareaDao.obtenerTareasStream()
    }

    suspend fun getTareaPorId(id: Int): TareaEntity? {
        return tareaDao.obtenerTareaPorId(id)
    }

    suspend fun insertarTarea(tarea: TareaEntity) {
        tareaDao.insertarTarea(tarea)
    }

    suspend fun actualizarTarea(tarea: TareaEntity) {
        tareaDao.actualizarTarea(tarea)
    }

    suspend fun eliminarTarea(id: Int) {
        tareaDao.eliminarTarea(id)
    }
}
