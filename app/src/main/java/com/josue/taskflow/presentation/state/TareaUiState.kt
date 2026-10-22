package com.josue.taskflow.presentation.state

import com.josue.taskflow.domain.model.Tarea

/**
 * Estado inmutable de la pantalla principal en la Capa de Presentación (UDF).
 */
data class TareaUiState(
    val tareas: List<Tarea> = emptyList(),
    val totalTareas: Int = 0,
    val completadas: Int = 0,
    val pendientes: Int = 0,
    val cargando: Boolean = false
)
