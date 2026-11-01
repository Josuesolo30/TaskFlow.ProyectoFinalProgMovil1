package com.josue.taskflow.domain.model.task

/**
 * Resultado explícito de dominio para operaciones de escritura sobre tareas.
 * Independiente de bibliotecas o recursos de Android.
 */
sealed interface TaskOperationResult {
    data object Success : TaskOperationResult
    data object InvalidTitle : TaskOperationResult
    data object TaskNotFound : TaskOperationResult
    data class Failure(val cause: Throwable? = null) : TaskOperationResult
}
