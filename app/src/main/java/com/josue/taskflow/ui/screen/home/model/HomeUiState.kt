package com.josue.taskflow.ui.screen.home.model

import com.josue.taskflow.domain.model.statistics.TaskStatistics

/**
 * Estado de interfaz para la pantalla de Inicio.
 */
data class HomeUiState(
    val statistics: TaskStatistics = TaskStatistics(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)
