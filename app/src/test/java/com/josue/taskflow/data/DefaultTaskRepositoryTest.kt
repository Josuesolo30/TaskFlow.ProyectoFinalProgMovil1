package com.josue.taskflow.data

import com.josue.taskflow.data.local.database.dao.TaskDao
import com.josue.taskflow.data.local.database.entity.task.TaskEntity
import com.josue.taskflow.data.local.datasource.task.TaskLocalDataSource
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
            tasks.value = tasks.value + task.copy(id = 2)
            return 2L
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

    private lateinit var fakeDao: FakeTaskDao
    private lateinit var repository: DefaultTaskRepository

    @Before
    fun setup() {
        fakeDao = FakeTaskDao()
        val dataSource = TaskLocalDataSource(fakeDao)
        repository = DefaultTaskRepository(dataSource)
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
        assertTrue(tasks.isEmpty())
    }
}
