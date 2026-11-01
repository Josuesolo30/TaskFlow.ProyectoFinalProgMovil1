package com.josue.taskflow.data.repository.task

import com.josue.taskflow.data.local.database.entity.task.TaskEntity
import com.josue.taskflow.data.local.datasource.task.TaskLocalDataSource
import com.josue.taskflow.data.mapper.task.toDomain
import com.josue.taskflow.domain.model.task.Task
import com.josue.taskflow.domain.model.task.TaskOperationResult
import com.josue.taskflow.domain.repository.task.TaskRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación principal de TaskRepository utilizando Room mediante TaskLocalDataSource.
 * Maneja excepciones de base de datos dentro de la Capa de Datos y retorna resultados explícitos.
 */
@Singleton
class DefaultTaskRepository @Inject constructor(
    private val localDataSource: TaskLocalDataSource
) : TaskRepository {

    private val notificationChannel = Channel<String>(Channel.BUFFERED)

    override fun getNotificationsStream(): Flow<String> {
        return notificationChannel.receiveAsFlow()
    }

    override fun getTasksStream(): Flow<List<Task>> {
        return localDataSource.getTasksStream().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTaskStream(taskId: Int): Flow<Task?> {
        return localDataSource.getTaskStream(taskId).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun addTask(title: String, description: String): TaskOperationResult {
        val cleanTitle = title.trim()
        if (cleanTitle.isBlank()) {
            return TaskOperationResult.InvalidTitle
        }

        return try {
            localDataSource.insertTask(
                TaskEntity(
                    title = cleanTitle,
                    description = description.trim()
                )
            )
            notificationChannel.send("msg_task_saved:$cleanTitle")
            TaskOperationResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            TaskOperationResult.Failure(e)
        }
    }

    override suspend fun toggleCompletion(taskId: Int): TaskOperationResult {
        return try {
            val taskEntity = localDataSource.getTaskById(taskId)
                ?: return TaskOperationResult.TaskNotFound

            val updated = taskEntity.copy(isCompleted = !taskEntity.isCompleted)
            localDataSource.updateTask(updated)

            val msgKey = if (updated.isCompleted) "msg_task_completed" else "msg_task_pending"
            notificationChannel.send(msgKey)
            TaskOperationResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            TaskOperationResult.Failure(e)
        }
    }

    override suspend fun updateDescription(taskId: Int, description: String): TaskOperationResult {
        return try {
            val taskEntity = localDataSource.getTaskById(taskId)
                ?: return TaskOperationResult.TaskNotFound

            localDataSource.updateTask(taskEntity.copy(description = description.trim()))
            notificationChannel.send("msg_description_saved")
            TaskOperationResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            TaskOperationResult.Failure(e)
        }
    }

    override suspend fun deleteTask(taskId: Int): TaskOperationResult {
        return try {
            val taskEntity = localDataSource.getTaskById(taskId)
                ?: return TaskOperationResult.TaskNotFound

            localDataSource.deleteTask(taskId)
            notificationChannel.send("msg_task_deleted:${taskEntity.title}")
            TaskOperationResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            TaskOperationResult.Failure(e)
        }
    }
}
