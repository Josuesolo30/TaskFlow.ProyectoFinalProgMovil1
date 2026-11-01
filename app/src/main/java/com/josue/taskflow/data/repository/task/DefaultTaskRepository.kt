package com.josue.taskflow.data.repository.task

import androidx.paging.PagingSource
import com.josue.taskflow.data.local.database.dao.RemoteTaskLinkDao
import com.josue.taskflow.data.local.database.entity.task.RemoteTaskLinkEntity
import com.josue.taskflow.data.local.database.entity.task.TaskEntity
import com.josue.taskflow.data.local.datasource.task.TaskLocalDataSource
import com.josue.taskflow.data.mapper.task.toDomain
import com.josue.taskflow.data.mapper.task.toEntity
import com.josue.taskflow.data.remote.datasource.TaskRemoteDataSource
import com.josue.taskflow.data.remote.dto.TaskDto
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
 * Implementación de TaskRepository que combina la base local de Room con Paging 3 y Retrofit.
 * Mantiene la estrategia 'Room como fuente de verdad' con paginación interna idempotente.
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
                        fetchAndSyncRemoteTasksPaged(maxPagesToFetch = 2)
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

    suspend fun fetchAndSyncRemoteTasksPaged(maxPagesToFetch: Int = 2) {
        syncMutex.withLock {
            val pagingSource = remoteDataSource.createPagingSource(userId = 1, pageSize = 10)
            var currentKey: Int? = 1
            var pageCount = 0

            while (currentKey != null && pageCount < maxPagesToFetch) {
                val params = PagingSource.LoadParams.Refresh(
                    key = currentKey,
                    loadSize = 10,
                    placeholdersEnabled = false
                )

                when (val loadResult = pagingSource.load(params)) {
                    is PagingSource.LoadResult.Page -> {
                        val dtos = loadResult.data
                        if (dtos.isNotEmpty()) {
                            savePageToRoom(dtos)
                        }
                        currentKey = loadResult.nextKey
                        pageCount++
                    }
                    is PagingSource.LoadResult.Error -> {
                        if (pageCount == 0) {
                            throw loadResult.throwable
                        }
                        break
                    }
                    is PagingSource.LoadResult.Invalid -> break
                }
            }
        }
    }

    private suspend fun savePageToRoom(dtos: List<TaskDto>) {
        for (dto in dtos) {
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
