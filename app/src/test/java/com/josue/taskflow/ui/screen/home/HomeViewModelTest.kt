package com.josue.taskflow.ui.screen.home

import com.josue.taskflow.data.FakeSettingsRepository
import com.josue.taskflow.data.FakeTaskRepository
import com.josue.taskflow.domain.usecase.task.TaskUseCase
import com.josue.taskflow.ui.screen.home.model.HomeUiState
import com.josue.taskflow.ui.screen.home.viewmodel.HomeViewModel
import com.josue.taskflow.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var taskRepository: FakeTaskRepository
    private lateinit var settingsRepository: FakeSettingsRepository
    private lateinit var taskUseCase: TaskUseCase
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        taskRepository = FakeTaskRepository()
        settingsRepository = FakeSettingsRepository()
        taskUseCase = TaskUseCase(taskRepository, settingsRepository)
        viewModel = HomeViewModel(taskUseCase)
    }

    @Test
    fun `uiState debe emitir Content con las estadisticas correctas`() = runTest {
        val state = viewModel.uiState.first { it is HomeUiState.Content } as HomeUiState.Content

        assertEquals(3, state.statistics.totalCount)
        assertEquals(1, state.statistics.completedCount)
        assertEquals(2, state.statistics.pendingCount)
    }
}
