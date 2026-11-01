package com.josue.taskflow.domain.model.statistics

import com.josue.taskflow.domain.model.task.Task

/**
 * Modelo de dominio con métricas y estadísticas calculadas sobre la lista de tareas.
 */
data class TaskStatistics(
    val totalCount: Int = 0,
    val completedCount: Int = 0,
    val pendingCount: Int = 0,
    val completionPercentage: Int = 0,
    val maxIdTask: Task? = null,
    val minIdTask: Task? = null,
    val summaryText: String = ""
)
