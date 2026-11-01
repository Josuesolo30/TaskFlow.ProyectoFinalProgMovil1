package com.josue.taskflow.data.repository.task

import com.josue.taskflow.data.local.database.dao.RemoteTaskLinkDao
import com.josue.taskflow.data.local.database.entity.task.RemoteTaskLinkEntity
import com.josue.taskflow.data.local.database.entity.task.TaskEntity
import com.josue.taskflow.data.local.datasource.task.TaskLocalDataSource
import com.josue.taskflow.data.mapper.task.toDomain
import com.josue.taskflow.data.mapper.task.toEntity
import com.josue.taskflow.data.remote.datasource.TaskRemoteDataSource
import com.josue.taskflow.domain.model.task.Task
import com.josue.taskflow.domain.model.task.TaskOperationResult
import com.josue.taskflow.domain.repository.task.TaskRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación de TaskRepository que combina la base local de Room con la API REST remota Retrofit.
 * Sigue una estrategia 'Room como fuente de verdad' con importación idempotente.
 */
@Singleton
class DefaultTaskRepository @Inject constructor(
    private val localDataSource: TaskLocalDataSource,
    private val remoteDataSource: TaskRemoteDataSource,
    private val remoteTaskLinkDao: RemoteTaskLinkDao
) : TaskRepository {

    private val notificationChannel = Channel<String>(Channel.BUFFERED)
    private val syncMutex = Mutex()
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    override fun getNotificationsStream(): Flow<String> {
        return notificationChannel.receiveAsFlow()
    }

    override fun getTasksStream(): Flow<List<Task>> {
        return localDataSource.getTasksStream()
            .onStart {
                repositoryScope.launch {
                    try {
                        fetchAndSyncRemoteTasks()
                    } catch (_: Exception) {
                        // Errores de red se suprimen si ya existen datos locales
                    }
                }
            }
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override fun getTaskStream(taskId: Int): Flow<Task?> {
        return localDataSource.getTaskStream(taskId).map { entity ->
            entity?.toDomain()
        }
    }

    suspend fun fetchAndSyncRemoteTasks() {
        syncMutex.withLock {
            val remoteDtos = remoteDataSource.fetchRemoteTasks(userId = 1)
            for (dto in remoteDtos) {
                val existingLink = remoteTaskLinkDao.getLink("jsonplaceholder", dto.id)
                if (existingLink == null) {
                    val newLocalId = localDataSource.insertTask(dto.toEntity()).toInt()
                    if (newLocalId > 0) {
                        remoteTaskLinkDao.insertOrUpdateLink(
                            RemoteTaskLinkEntity(
                                provider = "jsonplaceholder",
                                remoteId = dto.id,
                                localTaskId = newLocalId,
                                isDeleted = false
                            )
                        )
                    }
                }
                // Si el enlace existe y isDeleted == true, se omite (no se resucita la tarea).
                // Si el enlace existe y localTaskId != null, se omite (no se sobrescriben cambios locales).
            }
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

            remoteTaskLinkDao.markDeletedByLocalTaskId(taskId)
            notificationChannel.send("msg_task_deleted:${existingTask.title}")
            TaskOperationResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            TaskOperationResult.Failure(e)
        }
    }
}
