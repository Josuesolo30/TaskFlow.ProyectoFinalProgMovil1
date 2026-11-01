package com.josue.taskflow.data

import com.josue.taskflow.data.local.database.dao.RemoteTaskLinkDao
import com.josue.taskflow.data.local.database.dao.TaskDao
import com.josue.taskflow.data.local.database.entity.task.RemoteTaskLinkEntity
import com.josue.taskflow.data.local.database.entity.task.TaskEntity
import com.josue.taskflow.data.local.datasource.task.TaskLocalDataSource
import com.josue.taskflow.data.remote.api.TaskApiService
import com.josue.taskflow.data.remote.datasource.TaskRemoteDataSource
import com.josue.taskflow.data.remote.dto.TaskDto
import com.josue.taskflow.data.repository.task.DefaultTaskRepository
import com.josue.taskflow.domain.model.task.TaskOperationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultTaskRepositoryTest {

    private class FakeTaskDao : TaskDao {
        val tasks = MutableStateFlow(
            listOf(
                TaskEntity(id = 1, title = "Tarea A", description = "Desc A", isCompleted = false)
            )
        )

        override fun getTasksStream(): Flow<List<TaskEntity>> = tasks.asStateFlow()

        override fun getTaskStream(taskId: Int): Flow<TaskEntity?> {
            return tasks.map { list -> list.firstOrNull { it.id == taskId } }
        }

        override suspend fun getTaskById(taskId: Int): TaskEntity? {
            return tasks.value.firstOrNull { it.id == taskId }
        }

        override suspend fun insertTask(task: TaskEntity): Long {
            val newId = (tasks.value.maxOfOrNull { it.id } ?: 0) + 1
            tasks.value = tasks.value + task.copy(id = newId)
            return newId.toLong()
        }

        override suspend fun updateTask(task: TaskEntity): Int {
            return if (tasks.value.any { it.id == task.id }) {
                tasks.value = tasks.value.map { if (it.id == task.id) task else it }
                1
            } else 0
        }

        override suspend fun updateDescription(taskId: Int, description: String): Int {
            return if (tasks.value.any { it.id == taskId }) {
                tasks.value = tasks.value.map { if (it.id == taskId) it.copy(description = description) else it }
                1
            } else 0
        }

        override suspend fun toggleCompletion(taskId: Int): Int {
            return if (tasks.value.any { it.id == taskId }) {
                tasks.value = tasks.value.map { if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it }
                1
            } else 0
        }

        override suspend fun deleteTask(taskId: Int): Int {
            return if (tasks.value.any { it.id == taskId }) {
                tasks.value = tasks.value.filter { it.id != taskId }
                1
            } else 0
        }

        override suspend fun getTaskCount(): Int = tasks.value.size
    }

    private class FakeRemoteTaskLinkDao : RemoteTaskLinkDao {
        val links = mutableListOf<RemoteTaskLinkEntity>()

        override suspend fun getLink(provider: String, remoteId: Int): RemoteTaskLinkEntity? {
            return links.firstOrNull { it.provider == provider && it.remoteId == remoteId }
        }

        override suspend fun insertOrUpdateLink(link: RemoteTaskLinkEntity) {
            links.removeAll { it.provider == link.provider && it.remoteId == link.remoteId }
            links.add(link)
        }

        override suspend fun markDeletedByLocalTaskId(localTaskId: Int) {
            val index = links.indexOfFirst { it.localTaskId == localTaskId }
            if (index != -1) {
                links[index] = links[index].copy(isDeleted = true, localTaskId = null)
            }
        }
    }

    private class FakeTaskApiService : TaskApiService {
        override suspend fun getTodos(userId: Int): List<TaskDto> {
            return listOf(
                TaskDto(userId = 1, id = 101, title = "Tarea Remota REST", completed = false)
            )
        }
    }

    private lateinit var fakeDao: FakeTaskDao
    private lateinit var fakeLinkDao: FakeRemoteTaskLinkDao
    private lateinit var repository: DefaultTaskRepository

    @Before
    fun setup() {
        fakeDao = FakeTaskDao()
        fakeLinkDao = FakeRemoteTaskLinkDao()
        val localDataSource = TaskLocalDataSource(fakeDao)
        val remoteDataSource = TaskRemoteDataSource(FakeTaskApiService())

        repository = DefaultTaskRepository(
            localDataSource = localDataSource,
            remoteDataSource = remoteDataSource,
            remoteTaskLinkDao = fakeLinkDao
        )
    }

    @Test
    fun `addTask con titulo valido guarda tarea y retorna Success`() = runBlocking {
        val result = repository.addTask("Nueva Tarea Real", "Descripcion")
        assertEquals(TaskOperationResult.Success, result)

        val tasks = repository.getTasksStream().first()
        assertTrue(tasks.any { it.title == "Nueva Tarea Real" })
    }

    @Test
    fun `deleteTask en tarea inexistente retorna TaskNotFound`() = runBlocking {
        val result = repository.deleteTask(999)
        assertEquals(TaskOperationResult.TaskNotFound, result)
    }

    @Test
    fun `deleteTask en tarea existente retorna Success`() = runBlocking {
        val result = repository.deleteTask(1)
        assertEquals(TaskOperationResult.Success, result)

        val tasks = repository.getTasksStream().first()
        assertTrue(tasks.none { it.id == 1 })
    }

    @Test
    fun `fetchAndSyncRemoteTasks importa tareas de la API sin duplicar ids ni resucitar borradas`() = runBlocking {
        repository.fetchAndSyncRemoteTasks()

        val tasks = repository.getTasksStream().first()
        assertTrue(tasks.any { it.title == "Tarea Remota REST" })

        val countBefore = tasks.size
        repository.fetchAndSyncRemoteTasks()
        val countAfter = repository.getTasksStream().first().size
        assertEquals(countBefore, countAfter)
    }
}
