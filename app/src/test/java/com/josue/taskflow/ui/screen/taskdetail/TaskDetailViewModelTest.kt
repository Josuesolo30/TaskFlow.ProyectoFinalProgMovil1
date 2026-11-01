package com.josue.taskflow.ui.screen.taskdetail

import androidx.lifecycle.SavedStateHandle
import com.josue.taskflow.data.FakeSettingsRepository
import com.josue.taskflow.data.FakeTaskRepository
import com.josue.taskflow.domain.usecase.task.TaskUseCase
import com.josue.taskflow.ui.screen.taskdetail.model.TaskDetailUiState
import com.josue.taskflow.ui.screen.taskdetail.viewmodel.TaskDetailViewModel
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
class TaskDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

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
    fun `uiState debe emitir Content cuando la tarea existe`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("taskId" to 1))
        val viewModel = TaskDetailViewModel(savedStateHandle, taskUseCase)

        val state = viewModel.uiState.first { it is TaskDetailUiState.Content } as TaskDetailUiState.Content
        assertEquals(1, state.task.id)
    }

    @Test
    fun `deleteTask en tarea existente produce DeletionSuccess`() = runTest {
        val savedStateHandle = SavedStateHandle(mapOf("taskId" to 1))
        val viewModel = TaskDetailViewModel(savedStateHandle, taskUseCase)

        viewModel.deleteTask()

        val state = viewModel.uiState.first { it is TaskDetailUiState.DeletionSuccess }
        assertEquals(TaskDetailUiState.DeletionSuccess, state)
    }

    @Test
    fun `deleteTask cuando falla muestra error y permanece en la pantalla`() = runTest {
        taskRepository.shouldFail = true
        val savedStateHandle = SavedStateHandle(mapOf("taskId" to 1))
        val viewModel = TaskDetailViewModel(savedStateHandle, taskUseCase)

        viewModel.deleteTask()

        val state = viewModel.uiState.first { it is TaskDetailUiState.Content && it.operationError != null } as TaskDetailUiState.Content
        assertTrue(state.operationError != null)
    }
}
