package com.josue.taskflow.ui.screen.taskdetail.model

import com.josue.taskflow.domain.model.task.Task

/**
 * Estados de la pantalla de Detalle de Tarea.
 */
sealed interface TaskDetailUiState {
    data object Loading : TaskDetailUiState

    data class Content(
        val task: Task
    ) : TaskDetailUiState

    data object NotFound : TaskDetailUiState

    data class Error(
        val message: String
    ) : TaskDetailUiState
}
