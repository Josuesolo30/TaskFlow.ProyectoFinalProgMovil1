package com.josue.taskflow.domain.repository.task

import com.josue.taskflow.domain.model.task.Task
import com.josue.taskflow.domain.model.task.TaskOperationResult
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de tareas para la Capa de Dominio.
 */
interface TaskRepository {
    fun getTasksStream(): Flow<List<Task>>

    fun getTaskStream(taskId: Int): Flow<Task?>

    fun getNotificationsStream(): Flow<String>

    suspend fun addTask(title: String, description: String = ""): TaskOperationResult

    suspend fun toggleCompletion(taskId: Int): TaskOperationResult

    suspend fun updateDescription(taskId: Int, description: String): TaskOperationResult

    suspend fun deleteTask(taskId: Int): TaskOperationResult
}
