package com.josue.taskflow.ui.screen.tasklist.model

import com.josue.taskflow.domain.model.task.Task
import com.josue.taskflow.domain.model.task.TaskFilter

/**
 * Estados de la pantalla de Lista de Tareas.
 */
sealed interface TaskListUiState {
    data object Loading : TaskListUiState

    data class Content(
        val tasks: List<Task>,
        val activeFilter: TaskFilter
    ) : TaskListUiState

    data class Empty(
        val activeFilter: TaskFilter
    ) : TaskListUiState

    data class Error(
        val message: String
    ) : TaskListUiState
}
