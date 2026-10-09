package com.josue.taskflow.dominio.repository

import com.josue.taskflow.dominio.model.Tarea
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de repositorio en la capa de Dominio.
 * Define el contrato abstracto para la gestión de datos (Inversión de Dependencias).
 */
interface TareaRepository {
    fun obtenerTareas(): Flow<List<Tarea>>
    suspend fun agregarTarea(titulo: String)
    suspend fun cambiarEstadoTarea(id: Int)
}
