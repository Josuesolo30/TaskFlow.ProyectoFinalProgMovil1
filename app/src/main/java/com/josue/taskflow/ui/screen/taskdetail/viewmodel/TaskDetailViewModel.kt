package com.josue.taskflow.ui.screen.taskdetail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josue.taskflow.domain.model.task.TaskOperationResult
import com.josue.taskflow.domain.usecase.task.TaskUseCase
import com.josue.taskflow.ui.screen.taskdetail.model.TaskDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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

    private val isSavingState = MutableStateFlow(false)
    private val isDeletingState = MutableStateFlow(false)
    private val operationErrorState = MutableStateFlow<String?>(null)

    val uiState: StateFlow<TaskDetailUiState> = combine(
        taskUseCase.observeTask(taskId),
        isSavingState,
        isDeletingState,
        operationErrorState
    ) { task, isSaving, isDeleting, operationError ->
        if (task != null) {
            TaskDetailUiState.Content(
                task = task,
                isSaving = isSaving,
                isDeleting = isDeleting,
                operationError = operationError
            )
        } else {
            TaskDetailUiState.NotFound
        }
    }
        .catch { error ->
            emit(TaskDetailUiState.Error(error.message ?: "Error al cargar la tarea"))
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

    fun updateDescription(
        description: String,
        onResult: (TaskOperationResult) -> Unit = {}
    ) {
        viewModelScope.launch {
            isSavingState.value = true
            operationErrorState.value = null
            val result = taskUseCase.updateDescription(taskId, description)
            isSavingState.value = false

            if (result is TaskOperationResult.Failure) {
                operationErrorState.value = result.cause?.message ?: "Error al guardar la descripción"
            }
            onResult(result)
        }
    }

    fun deleteTask(onResult: (TaskOperationResult) -> Unit) {
        viewModelScope.launch {
            isDeletingState.value = true
            operationErrorState.value = null
            val result = taskUseCase.deleteTask(taskId)
            isDeletingState.value = false

            if (result is TaskOperationResult.Failure) {
                operationErrorState.value = result.cause?.message ?: "Error al eliminar la tarea"
            }
            onResult(result)
        }
    }

    fun clearOperationError() {
        operationErrorState.value = null
    }
}
