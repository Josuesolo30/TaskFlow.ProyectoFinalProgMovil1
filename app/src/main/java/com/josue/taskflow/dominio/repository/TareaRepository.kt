package com.josue.taskflow.dominio.repository

import com.josue.taskflow.dominio.model.Tarea
import kotlinx.coroutines.flow.Flow

interface TareaRepository {
    fun obtenerTareas(): Flow<List<Tarea>>

    suspend fun agregarTarea(
        titulo: String,
        descripcion: String = ""
    )

    suspend fun cambiarEstadoTarea(id: Int)

    suspend fun actualizarDescripcion(
        id: Int,
        descripcion: String
    )
}