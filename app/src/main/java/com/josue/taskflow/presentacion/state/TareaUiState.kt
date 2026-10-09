package com.josue.taskflow.presentacion.state

import com.josue.taskflow.dominio.model.Tarea

/**
 * Capa de Presentación: Estado inmutable de la interfaz de usuario.
 * Sigue el patrón Unidirectional Data Flow (UDF).
 */
data class TareaUiState(
    val tareas: List<Tarea> = emptyList(),
    val totalTareas: Int = 0,
    val completadas: Int = 0,
    val cargando: Boolean = false,
)
