package com.josue.taskflow.ui.screen.taskdetail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josue.taskflow.domain.usecase.task.TaskUseCase
import com.josue.taskflow.ui.screen.taskdetail.model.TaskDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel propio para la pantalla de Detalle de Tarea.
 * Obtiene taskId a través de SavedStateHandle.
 */
@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskUseCase: TaskUseCase
) : ViewModel() {

    val taskId: Int = checkNotNull(savedStateHandle["taskId"])

    val uiState: StateFlow<TaskDetailUiState> = taskUseCase.observeTask(taskId)
        .map { task ->
            if (task != null) {
                TaskDetailUiState.Content(task)
            } else {
                TaskDetailUiState.NotFound
            }
        }
        .catch { error ->
            emit(TaskDetailUiState.Error(error.message ?: "Error al cargar el detalle de la tarea"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskDetailUiState.Loading
        )

    fun toggleCompletion() {
        viewModelScope.launch {
            taskUseCase.toggleCompletion(taskId)
        }
    }

    fun updateDescription(description: String) {
        viewModelScope.launch {
            taskUseCase.updateDescription(taskId, description)
        }
    }

    fun deleteTask(onSuccess: () -> Unit) {
        viewModelScope.launch {
            taskUseCase.deleteTask(taskId)
            onSuccess()
        }
    }
}
