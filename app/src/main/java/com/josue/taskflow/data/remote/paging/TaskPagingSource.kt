package com.josue.taskflow.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.josue.taskflow.data.remote.api.TaskApiService
import com.josue.taskflow.data.remote.dto.TaskDto
import retrofit2.HttpException
import java.io.IOException

/**
 * PagingSource de Paging 3 para solicitar páginas de tareas desde la API REST.
 * Maneja claves de página, parámetros de carga, continuación y errores.
 */
class TaskPagingSource(
    private val apiService: TaskApiService,
    private val userId: Int = 1,
    private val pageSize: Int = 10
) : PagingSource<Int, TaskDto>() {

    override fun getRefreshKey(state: PagingState<Int, TaskDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TaskDto> {
        val pageKey = params.key ?: 1
        val limit = params.loadSize.coerceAtMost(pageSize)

        return try {
            val response = apiService.getTodosPaged(
                userId = userId,
                page = pageKey,
                limit = limit
            )

            val prevKey = if (pageKey == 1) null else pageKey - 1
            val nextKey = if (response.isEmpty() || response.size < limit) null else pageKey + 1

            LoadResult.Page(
                data = response,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
