package com.josue.taskflow.data.repository.task

import com.josue.taskflow.data.local.database.entity.task.TaskEntity
import com.josue.taskflow.data.local.datasource.task.TaskLocalDataSource
import com.josue.taskflow.data.mapper.task.toDomain
import com.josue.taskflow.domain.model.task.Task
import com.josue.taskflow.domain.repository.task.TaskRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación principal de TaskRepository utilizando Room mediante TaskLocalDataSource.
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

    override suspend fun addTask(title: String, description: String) {
        localDataSource.insertTask(
            TaskEntity(
                title = title,
                description = description
            )
        )
        notificationChannel.send("msg_task_saved:$title")
    }

    override suspend fun toggleCompletion(taskId: Int) {
        val taskEntity = localDataSource.getTaskById(taskId) ?: return
        val updated = taskEntity.copy(isCompleted = !taskEntity.isCompleted)
        localDataSource.updateTask(updated)

        val msgKey = if (updated.isCompleted) "msg_task_completed" else "msg_task_pending"
        notificationChannel.send(msgKey)
    }

    override suspend fun updateDescription(taskId: Int, description: String) {
        val taskEntity = localDataSource.getTaskById(taskId) ?: return
        localDataSource.updateTask(taskEntity.copy(description = description))
        notificationChannel.send("msg_description_saved")
    }

    override suspend fun deleteTask(taskId: Int) {
        val taskEntity = localDataSource.getTaskById(taskId)
        localDataSource.deleteTask(taskId)
        val title = taskEntity?.title ?: "Tarea"
        notificationChannel.send("msg_task_deleted:$title")
    }
}
