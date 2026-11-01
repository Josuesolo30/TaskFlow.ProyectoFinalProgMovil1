package com.josue.taskflow.data.remote.api

import com.josue.taskflow.data.remote.dto.TaskDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Servicio Retrofit para consumir la API REST pública de JSONPlaceholder con soporte de paginación.
 */
interface TaskApiService {

    @GET("todos")
    suspend fun getTodos(
        @Query("userId") userId: Int = 1
    ): List<TaskDto>

    @GET("todos")
    suspend fun getTodosPaged(
        @Query("userId") userId: Int = 1,
        @Query("_page") page: Int = 1,
        @Query("_limit") limit: Int = 10
    ): List<TaskDto>
}
