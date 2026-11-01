package com.josue.taskflow.ui.screen.tasklist

import com.josue.taskflow.data.FakeSettingsRepository
import com.josue.taskflow.data.FakeTaskRepository
import com.josue.taskflow.domain.model.task.TaskFilter
import com.josue.taskflow.domain.model.task.TaskOperationResult
import com.josue.taskflow.domain.usecase.task.TaskUseCase
import com.josue.taskflow.ui.screen.tasklist.model.TaskListUiState
import com.josue.taskflow.ui.screen.tasklist.viewmodel.TaskListViewModel
import com.josue.taskflow.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var taskRepository: FakeTaskRepository
    private lateinit var settingsRepository: FakeSettingsRepository
    private lateinit var taskUseCase: TaskUseCase
    private lateinit var viewModel: TaskListViewModel

    @Before
    fun setup() {
        taskRepository = FakeTaskRepository()
        settingsRepository = FakeSettingsRepository()
        taskUseCase = TaskUseCase(taskRepository, settingsRepository)
        viewModel = TaskListViewModel(taskUseCase)
    }

    @Test
    fun `addTask con titulo vacio no debe cerrar formulario y retorna InvalidTitle`() = runTest {
        var result: TaskOperationResult? = null
        viewModel.addTask("   ", "Descripcion") { res ->
            result = res
        }

        assertEquals(TaskOperationResult.InvalidTitle, result)

        val state = viewModel.uiState.first { it is TaskListUiState.Content } as TaskListUiState.Content
        assertTrue(state.operationError != null)
    }

    @Test
    fun `addTask valido agrega tarea y retorna Success`() = runTest {
        var result: TaskOperationResult? = null
        viewModel.addTask("  Nueva Tarea  ", "Descripcion") { res ->
            result = res
        }

        assertEquals(TaskOperationResult.Success, result)

        val state = viewModel.uiState.first { it is TaskListUiState.Content } as TaskListUiState.Content
        assertTrue(state.tasks.any { it.title == "Nueva Tarea" })
    }

    @Test
    fun `selectFilter actualiza el filtro en DataStore`() = runTest {
        viewModel.selectFilter(TaskFilter.COMPLETED)

        val activeFilter = viewModel.activeFilter.first()
        assertEquals(TaskFilter.COMPLETED, activeFilter)
    }
}
