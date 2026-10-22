package com.josue.taskflow.domain.repository

import com.josue.taskflow.domain.model.Tarea
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio de tareas para la Capa de Dominio.
 */
interface TareaRepository {
    fun getTareasStream(): Flow<List<Tarea>>

    fun getConfirmacionesStream(): Flow<String>

    suspend fun agregarTarea(titulo: String, descripcion: String = "")

    suspend fun cambiarEstadoTarea(id: Int)

    suspend fun actualizarDescripcion(id: Int, descripcion: String)

    suspend fun eliminarTarea(id: Int)
}
