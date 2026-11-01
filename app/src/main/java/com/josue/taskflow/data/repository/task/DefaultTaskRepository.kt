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
 * Asigna System.currentTimeMillis() a createdAt únicamente al crear nuevas tareas de usuario.
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
            val rowId = localDataSource.insertTask(
                TaskEntity(
                    title = cleanTitle,
                    description = description.trim(),
                    createdAt = System.currentTimeMillis()
                )
            )
            if (rowId > 0) {
                notificationChannel.send("msg_task_saved:$cleanTitle")
                TaskOperationResult.Success
            } else {
                TaskOperationResult.Failure(IllegalStateException("No se pudo insertar la fila"))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            TaskOperationResult.Failure(e)
        }
    }

    override suspend fun toggleCompletion(taskId: Int): TaskOperationResult {
        return try {
            val rowsAffected = localDataSource.toggleCompletion(taskId)
            if (rowsAffected <= 0) {
                return TaskOperationResult.TaskNotFound
            }

            val updatedTask = localDataSource.getTaskById(taskId)
            val msgKey = if (updatedTask?.isCompleted == true) "msg_task_completed" else "msg_task_pending"
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
            val rowsAffected = localDataSource.updateDescription(taskId, description.trim())
            if (rowsAffected <= 0) {
                return TaskOperationResult.TaskNotFound
            }

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
            val existingTask = localDataSource.getTaskById(taskId)
                ?: return TaskOperationResult.TaskNotFound

            val rowsAffected = localDataSource.deleteTask(taskId)
            if (rowsAffected <= 0) {
                return TaskOperationResult.TaskNotFound
            }

            notificationChannel.send("msg_task_deleted:${existingTask.title}")
            TaskOperationResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            TaskOperationResult.Failure(e)
        }
    }
}
