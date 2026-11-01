package com.josue.taskflow.domain.repository.task

import com.josue.taskflow.domain.model.task.Task
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de tareas para la Capa de Dominio.
 */
interface TaskRepository {
    fun getTasksStream(): Flow<List<Task>>

    fun getTaskStream(taskId: Int): Flow<Task?>

    fun getNotificationsStream(): Flow<String>

    suspend fun addTask(title: String, description: String = "")

    suspend fun toggleCompletion(taskId: Int)

    suspend fun updateDescription(taskId: Int, description: String)

    suspend fun deleteTask(taskId: Int)
}
