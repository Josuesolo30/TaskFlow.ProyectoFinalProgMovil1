package com.josue.taskflow.data.remote.datasource

import com.josue.taskflow.data.remote.api.TaskApiService
import com.josue.taskflow.data.remote.dto.TaskDto
import com.josue.taskflow.data.remote.paging.TaskPagingSource
import javax.inject.Inject

/**
 * Fuente de datos remota que consume la API REST mediante TaskApiService y Paging 3.
 */
class TaskRemoteDataSource @Inject constructor(
    private val apiService: TaskApiService
) {
    suspend fun fetchRemoteTasks(userId: Int = 1): List<TaskDto> {
        return apiService.getTodos(userId)
    }

    suspend fun fetchRemoteTasksPaged(userId: Int = 1, page: Int = 1, limit: Int = 10): List<TaskDto> {
        return apiService.getTodosPaged(userId, page, limit)
    }

    fun createPagingSource(userId: Int = 1, pageSize: Int = 10): TaskPagingSource {
        return TaskPagingSource(apiService, userId, pageSize)
    }
}
