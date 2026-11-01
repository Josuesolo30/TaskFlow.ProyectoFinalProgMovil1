package com.josue.taskflow.ui.screen.home.model

import com.josue.taskflow.domain.model.statistics.TaskStatistics

/**
 * Estados de la pantalla de Inicio.
 */
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Content(val statistics: TaskStatistics) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
