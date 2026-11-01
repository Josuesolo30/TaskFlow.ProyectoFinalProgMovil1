package com.josue.taskflow.data.remote.paging

import androidx.paging.PagingSource
import com.josue.taskflow.data.remote.api.TaskApiService
import com.josue.taskflow.data.remote.dto.TaskDto
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TaskPagingSourceTest {

    private class FakeTaskApiService : TaskApiService {
        override suspend fun getTodos(userId: Int): List<TaskDto> {
            return (1..20).map { id ->
                TaskDto(userId = userId, id = id, title = "Task $id", completed = false)
            }
        }

        override suspend fun getTodosPaged(userId: Int, page: Int, limit: Int): List<TaskDto> {
            if (page > 3) return emptyList()
            val startId = (page - 1) * limit + 1
            return (startId until startId + limit).map { id ->
                TaskDto(userId = userId, id = id, title = "Task $id", completed = false)
            }
        }
    }

    private lateinit var apiService: TaskApiService
    private lateinit var pagingSource: TaskPagingSource

    @Before
    fun setup() {
        apiService = FakeTaskApiService()
        pagingSource = TaskPagingSource(apiService, userId = 1, pageSize = 10)
    }

    @Test
    fun `load Pagina 1 retorna LoadResult Page con nextKey igual a 2`() = runBlocking {
        val params = PagingSource.LoadParams.Refresh(
            key = 1,
            loadSize = 10,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(params) as PagingSource.LoadResult.Page

        assertEquals(10, result.data.size)
        assertEquals(null, result.prevKey)
        assertEquals(2, result.nextKey)
        assertEquals("Task 1", result.data.first().title)
    }

    @Test
    fun `load Pagina 2 retorna LoadResult Page con prevKey 1 y nextKey 3`() = runBlocking {
        val params = PagingSource.LoadParams.Append(
            key = 2,
            loadSize = 10,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(params) as PagingSource.LoadResult.Page

        assertEquals(10, result.data.size)
        assertEquals(1, result.prevKey)
        assertEquals(3, result.nextKey)
        assertEquals("Task 11", result.data.first().title)
    }

    @Test
    fun `load Pagina mayor al limite retorna nextKey null`() = runBlocking {
        val params = PagingSource.LoadParams.Append(
            key = 4,
            loadSize = 10,
            placeholdersEnabled = false
        )

        val result = pagingSource.load(params) as PagingSource.LoadResult.Page

        assertTrue(result.data.isEmpty())
        assertEquals(null, result.nextKey)
    }
}
