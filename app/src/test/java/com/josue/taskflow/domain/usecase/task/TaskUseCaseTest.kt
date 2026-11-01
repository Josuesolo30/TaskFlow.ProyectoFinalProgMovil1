package com.josue.taskflow.domain.usecase.task

import com.josue.taskflow.data.FakeSettingsRepository
import com.josue.taskflow.data.FakeTaskRepository
import com.josue.taskflow.domain.model.task.Task
import com.josue.taskflow.domain.model.task.TaskFilter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TaskUseCaseTest {

    private lateinit var taskRepository: FakeTaskRepository
    private lateinit var settingsRepository: FakeSettingsRepository
    private lateinit var taskUseCase: TaskUseCase

    @Before
    fun setup() {
        taskRepository = FakeTaskRepository()
        settingsRepository = FakeSettingsRepository()
        taskUseCase = TaskUseCase(taskRepository, settingsRepository)
    }

    @Test
    fun `addTask no debe agregar tareas vacias`() = runBlocking {
        val initialSize = taskUseCase.observeTasks().first().size
        taskUseCase.addTask("   ", "Description")

        val finalSize = taskUseCase.observeTasks().first().size
        assertEquals(initialSize, finalSize)
    }

    @Test
    fun `addTask debe agregar tarea valida haciendo trim de titulo`() = runBlocking {
        taskUseCase.addTask("  Nueva Tarea  ", "  Descripcion  ")

        val tasks = taskUseCase.observeTasks().first()
        val added = tasks.firstOrNull { it.title == "Nueva Tarea" }

        assertTrue(added != null)
        assertEquals("Descripcion", added?.description)
    }

    @Test
    fun `calculateStatistics debe retornar metricas correctas`() = runBlocking {
        val tasks = listOf(
            Task(id = 1, title = "A", isCompleted = true),
            Task(id = 2, title = "B", isCompleted = false),
            Task(id = 3, title = "C", isCompleted = true)
        )

        val stats = taskUseCase.calculateStatistics(tasks)

        assertEquals(3, stats.totalCount)
        assertEquals(2, stats.completedCount)
        assertEquals(1, stats.pendingCount)
        assertEquals(66, stats.completionPercentage)
        assertEquals(3, stats.maxIdTask?.id)
        assertEquals(1, stats.minIdTask?.id)
    }

    @Test
    fun `saveFilter y observeFilter deben persistir el filtro seleccionado`() = runBlocking {
        taskUseCase.saveFilter(TaskFilter.COMPLETED)
        val filter = taskUseCase.observeFilter().first()

        assertEquals(TaskFilter.COMPLETED, filter)
    }
}
