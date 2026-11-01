package com.josue.taskflow.ui.screen.taskdetail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josue.taskflow.domain.model.task.TaskOperationResult
import com.josue.taskflow.domain.usecase.task.TaskUseCase
import com.josue.taskflow.ui.screen.taskdetail.model.TaskDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
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
 * Administra las operaciones de escritura y los estados de resultado de borrado/guardado.
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
    private val isDeletionSuccessfulState = MutableStateFlow(false)
    private val saveSuccessEventState = MutableStateFlow(false)

    private data class InternalDetailState(
        val isSaving: Boolean,
        val isDeleting: Boolean,
        val operationError: String?,
        val isDeletionSuccessful: Boolean,
        val saveSuccessEvent: Boolean
    )

    private val internalState = combine(
        isSavingState,
        isDeletingState,
        operationErrorState,
        isDeletionSuccessfulState,
        saveSuccessEventState
    ) { isSaving, isDeleting, operationError, isDeletionSuccessful, saveSuccessEvent ->
        InternalDetailState(
            isSaving = isSaving,
            isDeleting = isDeleting,
            operationError = operationError,
            isDeletionSuccessful = isDeletionSuccessful,
            saveSuccessEvent = saveSuccessEvent
        )
    }

    val uiState: StateFlow<TaskDetailUiState> = combine(
        taskUseCase.observeTask(taskId),
        internalState
    ) { task, internal ->
        if (internal.isDeletionSuccessful) {
            TaskDetailUiState.DeletionSuccess
        } else if (task != null) {
            TaskDetailUiState.Content(
                task = task,
                isSaving = internal.isSaving,
                isDeleting = internal.isDeleting,
                operationError = internal.operationError,
                isDeletionSuccessful = internal.isDeletionSuccessful,
                saveSuccessEvent = internal.saveSuccessEvent
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
            operationErrorState.value = null
            try {
                val result = taskUseCase.toggleCompletion(taskId)
                if (result is TaskOperationResult.Failure) {
                    operationErrorState.value = result.cause?.message ?: "Error al cambiar estado"
                } else if (result is TaskOperationResult.TaskNotFound) {
                    operationErrorState.value = "La tarea ya no existe"
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                operationErrorState.value = e.message ?: "Error inesperado"
            }
        }
    }

    fun updateDescription(description: String) {
        viewModelScope.launch {
            isSavingState.value = true
            operationErrorState.value = null
            try {
                when (val result = taskUseCase.updateDescription(taskId, description)) {
                    TaskOperationResult.Success -> {
                        saveSuccessEventState.value = true
                    }
                    TaskOperationResult.TaskNotFound -> {
                        operationErrorState.value = "La tarea no fue encontrada"
                    }
                    TaskOperationResult.InvalidTitle -> {
                        operationErrorState.value = "Título inválido"
                    }
                    is TaskOperationResult.Failure -> {
                        operationErrorState.value = result.cause?.message ?: "Error al guardar la descripción"
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                operationErrorState.value = e.message ?: "Error al guardar descripción"
            } finally {
                isSavingState.value = false
            }
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            isDeletingState.value = true
            operationErrorState.value = null
            try {
                when (val result = taskUseCase.deleteTask(taskId)) {
                    TaskOperationResult.Success -> {
                        isDeletionSuccessfulState.value = true
                    }
                    TaskOperationResult.TaskNotFound -> {
                        operationErrorState.value = "La tarea no fue encontrada"
                    }
                    TaskOperationResult.InvalidTitle -> {}
                    is TaskOperationResult.Failure -> {
                        operationErrorState.value = result.cause?.message ?: "Error al eliminar la tarea"
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                operationErrorState.value = e.message ?: "Error al eliminar la tarea"
            } finally {
                isDeletingState.value = false
            }
        }
    }

    fun consumeSaveSuccessEvent() {
        saveSuccessEventState.value = false
    }

    fun clearOperationError() {
        operationErrorState.value = null
    }
}
