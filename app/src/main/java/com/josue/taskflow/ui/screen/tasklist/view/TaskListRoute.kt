package com.josue.taskflow.ui.screen.tasklist.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.josue.taskflow.ui.screen.tasklist.viewmodel.TaskListViewModel

/**
 * Ruta Compose para la pantalla de Lista de Tareas.
 */
@Composable
fun TaskListRoute(
    onOpenDetail: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeFilter by viewModel.activeFilter.collectAsStateWithLifecycle()

    TaskListScreen(
        uiState = uiState,
        activeFilter = activeFilter,
        onSelectFilter = viewModel::selectFilter,
        onAddTask = viewModel::addTask,
        onToggleCompletion = viewModel::toggleCompletion,
        onDeleteTask = viewModel::deleteTask,
        onOpenDetail = onOpenDetail,
        onBack = onBack
    )
}
