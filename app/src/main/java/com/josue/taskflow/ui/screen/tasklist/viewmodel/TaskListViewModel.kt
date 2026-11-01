package com.josue.taskflow.ui.screen.tasklist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josue.taskflow.core.collection.ProcessableContainer
import com.josue.taskflow.core.collection.sorted
import com.josue.taskflow.domain.model.task.TaskFilter
import com.josue.taskflow.domain.model.task.TaskOperationResult
import com.josue.taskflow.domain.usecase.task.TaskUseCase
import com.josue.taskflow.ui.screen.tasklist.model.TaskListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

/**
 * ViewModel propio para la pantalla de Lista de Tareas.
 */
@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val taskUseCase: TaskUseCase
) : ViewModel() {

    private val isSubmittingState = MutableStateFlow(false)
    private val operationErrorState = MutableStateFlow<String?>(null)

    val activeFilter: StateFlow<TaskFilter> = taskUseCase.observeFilter()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskFilter.ALL
        )

    val uiState: StateFlow<TaskListUiState> = combine(
        taskUseCase.observeTasks(),
        activeFilter,
        isSubmittingState,
        operationErrorState
    ) { tasks, filter, isSubmitting, operationError ->
        val cleanTasks = tasks
            .map { task -> task.copy(title = task.title.trim()) }
            .filter { task -> task.title.isNotBlank() }

        val sortedTasks = ProcessableContainer(cleanTasks).sorted().elements

        val visibleTasks = when (filter) {
            TaskFilter.ALL -> sortedTasks
            TaskFilter.PENDING -> sortedTasks.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> sortedTasks.filter { it.isCompleted }
        }

        if (visibleTasks.isEmpty()) {
            TaskListUiState.Empty(
                activeFilter = filter,
                isSubmitting = isSubmitting,
                operationError = operationError
            )
        } else {
            TaskListUiState.Content(
                tasks = visibleTasks,
                activeFilter = filter,
                isSubmitting = isSubmitting,
                operationError = operationError
            )
        }
    }
        .retry(2) { error ->
            if (error is IOException) {
                delay(300)
                true
            } else false
        }
        .catch { error ->
            emit(TaskListUiState.Error(message = error.message ?: "Error al cargar tareas"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskListUiState.Loading
        )

    fun selectFilter(filter: TaskFilter) {
        viewModelScope.launch {
            taskUseCase.saveFilter(filter)
        }
    }

    fun addTask(
        title: String,
        description: String = "",
        onResult: (TaskOperationResult) -> Unit
    ) {
        viewModelScope.launch {
            isSubmittingState.value = true
            operationErrorState.value = null
            val result = taskUseCase.addTask(title, description)
            isSubmittingState.value = false

            when (result) {
                TaskOperationResult.Success -> {
                    onResult(result)
                }
                TaskOperationResult.InvalidTitle -> {
                    operationErrorState.value = "El título de la tarea no puede estar vacío"
                    onResult(result)
                }
                is TaskOperationResult.Failure -> {
                    operationErrorState.value = result.cause?.message ?: "Error al guardar la tarea"
                    onResult(result)
                }
                TaskOperationResult.TaskNotFound -> {
                    onResult(result)
                }
            }
        }
    }

    fun toggleCompletion(taskId: Int) {
        viewModelScope.launch {
            taskUseCase.toggleCompletion(taskId)
        }
    }

    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            taskUseCase.deleteTask(taskId)
        }
    }

    fun clearOperationError() {
        operationErrorState.value = null
    }
}
