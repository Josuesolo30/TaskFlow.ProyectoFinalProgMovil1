package com.josue.taskflow.ui.screen.tasklist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josue.taskflow.core.collection.ProcessableContainer
import com.josue.taskflow.core.collection.sorted
import com.josue.taskflow.domain.model.sync.SyncStatus
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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

/**
 * ViewModel propio para la pantalla de Lista de Tareas.
 * Aplica operadores de Flow (filter, map, combine, transform, retry, catch)
 * para procesar la lista reactiva de Room y el filtro de DataStore,
 * exponiendo además 'syncStatus' para gestionar errores de red de forma aislada.
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

    val syncStatus: StateFlow<SyncStatus> = taskUseCase.observeSyncStatus()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SyncStatus.Idle
        )

    private data class FormState(
        val isSubmitting: Boolean,
        val operationError: String?
    )

    private val formStateFlow = combine(
        isSubmittingState,
        operationErrorState
    ) { isSubmitting, operationError ->
        FormState(isSubmitting, operationError)
    }

    private val processedTasksFlow = taskUseCase.observeTasks()
        .filter { tasks ->
            // Descarta emisiones con IDs duplicados, conservando listas vacías válidas
            tasks.isEmpty() || tasks.map { it.id }.distinct().size == tasks.size
        }
        .map { tasks ->
            // Normaliza títulos, elimina títulos en blanco y ordena por ID usando ProcessableContainer
            val cleanTasks = tasks
                .map { task -> task.copy(title = task.title.trim()) }
                .filter { task -> task.title.isNotBlank() }
            ProcessableContainer(cleanTasks).sorted().elements
        }

    val uiState: StateFlow<TaskListUiState> = combine(
        processedTasksFlow,
        activeFilter,
        formStateFlow
    ) { tasks, filter, formState ->
        Tuple4(tasks, filter, formState.isSubmitting, formState.operationError)
    }
        .transform { (tasks, filter, isSubmitting, operationError) ->
            val visibleTasks = when (filter) {
                TaskFilter.ALL -> tasks
                TaskFilter.PENDING -> tasks.filter { !it.isCompleted }
                TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
            }

            if (visibleTasks.isEmpty()) {
                emit(
                    TaskListUiState.Empty(
                        activeFilter = filter,
                        isSubmitting = isSubmitting,
                        operationError = operationError
                    )
                )
            } else {
                emit(
                    TaskListUiState.Content(
                        tasks = visibleTasks,
                        activeFilter = filter,
                        isSubmitting = isSubmitting,
                        operationError = operationError
                    )
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

    private data class Tuple4<A, B, C, D>(
        val tasks: A,
        val filter: B,
        val isSubmitting: C,
        val operationError: D
    )

    fun selectFilter(filter: TaskFilter) {
        viewModelScope.launch {
            taskUseCase.saveFilter(filter)
        }
    }

    fun retrySync() {
        viewModelScope.launch {
            taskUseCase.syncRemoteTasks()
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
