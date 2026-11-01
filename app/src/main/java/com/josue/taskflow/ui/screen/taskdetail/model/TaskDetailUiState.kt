package com.josue.taskflow.ui.screen.taskdetail.model

import com.josue.taskflow.domain.model.task.Task

/**
 * Estados de la pantalla de Detalle de Tarea.
 */
sealed interface TaskDetailUiState {
    data object Loading : TaskDetailUiState

    data class Content(
        val task: Task,
        val isSaving: Boolean = false,
        val isDeleting: Boolean = false,
        val operationError: String? = null,
        val isDeletionSuccessful: Boolean = false,
        val saveSuccessEvent: Boolean = false
    ) : TaskDetailUiState

    data object DeletionSuccess : TaskDetailUiState

    data object NotFound : TaskDetailUiState

    data class Error(
        val message: String
    ) : TaskDetailUiState
}
