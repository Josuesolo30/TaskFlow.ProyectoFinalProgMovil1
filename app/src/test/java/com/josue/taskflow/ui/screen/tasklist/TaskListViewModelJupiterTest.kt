package com.josue.taskflow.ui.screen.tasklist

import app.cash.turbine.test
import com.josue.taskflow.domain.model.task.Task
import com.josue.taskflow.domain.model.task.TaskFilter
import com.josue.taskflow.domain.model.task.TaskOperationResult
import com.josue.taskflow.domain.repository.settings.SettingsRepository
import com.josue.taskflow.domain.repository.task.TaskRepository
import com.josue.taskflow.domain.usecase.task.TaskUseCase
import com.josue.taskflow.ui.screen.tasklist.model.TaskListUiState
import com.josue.taskflow.ui.screen.tasklist.viewmodel.TaskListViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException

/**
 * Suite de pruebas unitarias para TaskListViewModel utilizando JUnit 5 Jupiter, MockK,
 * StandardTestDispatcher, runTest y Turbine.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelJupiterTest {

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var taskRepository: TaskRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var taskUseCase: TaskUseCase
    private lateinit var viewModel: TaskListViewModel

    private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())
    private val filterFlow = MutableStateFlow(TaskFilter.ALL)
    private val notificationsFlow = MutableStateFlow("")

    @BeforeEach
    fun setUp() {
        testDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        taskRepository = mockk(relaxed = true)
        settingsRepository = mockk(relaxed = true)

        every { taskRepository.getTasksStream() } returns tasksFlow
        every { taskRepository.getNotificationsStream() } returns notificationsFlow
        every { settingsRepository.getFilterStream() } returns filterFlow

        taskUseCase = TaskUseCase(taskRepository, settingsRepository)
        viewModel = TaskListViewModel(taskUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `1 Carga y contenido inicial emite Content con tareas ordenadas y limpias`() = runTest(testDispatcher) {
        val backgroundJob = backgroundScope.launch { viewModel.uiState.collect {} }

        tasksFlow.value = listOf(
            Task(id = 3, title = "  Tarea 3  ", description = "D3"),
            Task(id = 1, title = "Tarea 1", description = "D1"),
            Task(id = 2, title = "   ", description = "Blank Title")
        )
        runCurrent()

        val state = viewModel.uiState.first { it is TaskListUiState.Content } as TaskListUiState.Content

        assertEquals(2, state.tasks.size)
        assertEquals(1, state.tasks[0].id)
        assertEquals(3, state.tasks[1].id)
        assertEquals("Tarea 3", state.tasks[1].title)

        backgroundJob.cancel()
    }

    @Test
    fun `2 Lista vacia emite Empty con el filtro activo`() = runTest(testDispatcher) {
        val backgroundJob = backgroundScope.launch { viewModel.uiState.collect {} }

        tasksFlow.value = emptyList()
        runCurrent()

        val state = viewModel.uiState.first { it is TaskListUiState.Empty } as TaskListUiState.Empty

        assertEquals(TaskFilter.ALL, state.activeFilter)

        backgroundJob.cancel()
    }

    @Test
    fun `3 Seleccion de filtro guarda la preferencia en el repositorio`() = runTest(testDispatcher) {
        val backgroundJob = backgroundScope.launch { viewModel.uiState.collect {} }

        coEvery { settingsRepository.saveFilter(TaskFilter.COMPLETED) } returns Unit

        viewModel.selectFilter(TaskFilter.COMPLETED)
        advanceUntilIdle()

        coVerify { settingsRepository.saveFilter(TaskFilter.COMPLETED) }

        backgroundJob.cancel()
    }

    @Test
    fun `4 Alta exitosa valida titulo limpio y retorna Success`() = runTest(testDispatcher) {
        val backgroundJob = backgroundScope.launch { viewModel.uiState.collect {} }

        coEvery { taskRepository.addTask("Nueva Tarea", "Descripcion") } returns TaskOperationResult.Success

        var capturedResult: TaskOperationResult? = null
        viewModel.addTask("  Nueva Tarea  ", "  Descripcion  ") { result ->
            capturedResult = result
        }

        advanceUntilIdle()

        assertEquals(TaskOperationResult.Success, capturedResult)
        coVerify { taskRepository.addTask("Nueva Tarea", "Descripcion") }

        backgroundJob.cancel()
    }

    @Test
    fun `5 Titulo invalido retorna InvalidTitle y no llama al repositorio`() = runTest(testDispatcher) {
        val backgroundJob = backgroundScope.launch { viewModel.uiState.collect {} }

        var capturedResult: TaskOperationResult? = null
        viewModel.addTask("   ", "Descripcion") { result ->
            capturedResult = result
        }

        advanceUntilIdle()

        assertEquals(TaskOperationResult.InvalidTitle, capturedResult)
        coVerify(exactly = 0) { taskRepository.addTask(any(), any()) }

        val state = viewModel.uiState.value
        assertTrue(state is TaskListUiState.Empty && state.operationError != null)

        backgroundJob.cancel()
    }

    @Test
    fun `6 Fallo de guardado retorna Failure y permite limpiar el error`() = runTest(testDispatcher) {
        val backgroundJob = backgroundScope.launch { viewModel.uiState.collect {} }

        val exception = RuntimeException("Error de base de datos")
        coEvery { taskRepository.addTask("Tarea Error", "Desc") } returns TaskOperationResult.Failure(exception)

        var capturedResult: TaskOperationResult? = null
        viewModel.addTask("Tarea Error", "Desc") { result ->
            capturedResult = result
        }

        advanceUntilIdle()

        assertTrue(capturedResult is TaskOperationResult.Failure)

        viewModel.clearOperationError()
        runCurrent()

        val state = viewModel.uiState.value
        if (state is TaskListUiState.Empty) {
            assertEquals(null, state.operationError)
        }

        backgroundJob.cancel()
    }

    @Test
    fun `7 Fallo al leer y reintentos muestra Error cuando se agotan los reintentos`() = runTest(testDispatcher) {
        val failingFlow = flow<List<Task>> {
            throw IOException("Fallo de E/S")
        }
        every { taskRepository.getTasksStream() } returns failingFlow

        val localViewModel = TaskListViewModel(taskUseCase)
        val backgroundJob = backgroundScope.launch { localViewModel.uiState.collect {} }

        advanceTimeBy(1000)
        advanceUntilIdle()

        val state = localViewModel.uiState.value
        assertTrue(state is TaskListUiState.Error)
        assertEquals("Fallo de E/S", (state as TaskListUiState.Error).message)

        backgroundJob.cancel()
    }

    @Test
    fun `8 Acciones de lista toggleCompletion y deleteTask envian ID correcto`() = runTest(testDispatcher) {
        val backgroundJob = backgroundScope.launch { viewModel.uiState.collect {} }

        coEvery { taskRepository.toggleCompletion(10) } returns TaskOperationResult.Success
        coEvery { taskRepository.deleteTask(10) } returns TaskOperationResult.Success

        viewModel.toggleCompletion(10)
        viewModel.deleteTask(10)
        advanceUntilIdle()

        coVerify { taskRepository.toggleCompletion(10) }
        coVerify { taskRepository.deleteTask(10) }

        backgroundJob.cancel()
    }

    @Test
    fun `9 Alta con descripcion predeterminada utiliza cadena vacia`() = runTest(testDispatcher) {
        val backgroundJob = backgroundScope.launch { viewModel.uiState.collect {} }

        coEvery { taskRepository.addTask("Tarea Solo Titulo", "") } returns TaskOperationResult.Success

        var capturedResult: TaskOperationResult? = null
        viewModel.addTask("Tarea Solo Titulo") { result ->
            capturedResult = result
        }

        advanceUntilIdle()

        assertEquals(TaskOperationResult.Success, capturedResult)
        coVerify { taskRepository.addTask("Tarea Solo Titulo", "") }

        backgroundJob.cancel()
    }

    @Test
    fun `10 Ver solo tareas completadas usando Turbine prueba las emisiones reactivas`() = runTest(testDispatcher) {
        val taskPending = Task(id = 1, title = "Tarea Pendiente", description = "D1", isCompleted = false)
        val taskCompleted = Task(id = 2, title = "Tarea Completada", description = "D2", isCompleted = true)

        tasksFlow.value = listOf(taskPending, taskCompleted)
        filterFlow.value = TaskFilter.ALL

        viewModel.uiState.test {
            val loadingItem = awaitItem()
            assertTrue(loadingItem is TaskListUiState.Loading)

            val initialContent = awaitItem() as TaskListUiState.Content
            assertEquals(2, initialContent.tasks.size)
            assertEquals(TaskFilter.ALL, initialContent.activeFilter)

            coEvery { settingsRepository.saveFilter(TaskFilter.COMPLETED) } answers {
                filterFlow.value = TaskFilter.COMPLETED
            }

            viewModel.selectFilter(TaskFilter.COMPLETED)

            val filteredContent = awaitItem() as TaskListUiState.Content
            assertEquals(1, filteredContent.tasks.size)
            assertEquals(2, filteredContent.tasks.first().id)
            assertEquals(TaskFilter.COMPLETED, filteredContent.activeFilter)

            tasksFlow.value = listOf(taskPending)

            val emptyState = awaitItem() as TaskListUiState.Empty
            assertEquals(TaskFilter.COMPLETED, emptyState.activeFilter)

            cancelAndIgnoreRemainingEvents()
        }
    }
}
