package com.josue.taskflow.data.remote.datasource

import com.josue.taskflow.data.remote.api.TaskApiService
import com.josue.taskflow.data.remote.dto.TaskDto
import javax.inject.Inject

/**
 * Fuente de datos remota que consume la API REST mediante TaskApiService.
 */
class TaskRemoteDataSource @Inject constructor(
    private val apiService: TaskApiService
) {
    suspend fun fetchRemoteTasks(userId: Int = 1): List<TaskDto> {
        return apiService.getTodos(userId)
    }
}
