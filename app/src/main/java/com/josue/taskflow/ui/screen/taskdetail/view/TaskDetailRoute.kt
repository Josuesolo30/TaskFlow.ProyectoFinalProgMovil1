package com.josue.taskflow.ui.screen.taskdetail.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.josue.taskflow.domain.model.task.TaskOperationResult
import com.josue.taskflow.ui.screen.taskdetail.viewmodel.TaskDetailViewModel

/**
 * Ruta Compose para la pantalla de Detalle de Tarea.
 */
@Composable
fun TaskDetailRoute(
    onBack: () -> Unit,
    viewModel: TaskDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TaskDetailScreen(
        uiState = uiState,
        onToggleCompletion = viewModel::toggleCompletion,
        onSaveDescription = viewModel::updateDescription,
        onDeleteTask = { onResult ->
            viewModel.deleteTask { result ->
                onResult(result)
                if (result is TaskOperationResult.Success) {
                    onBack()
                }
            }
        },
        onBack = onBack
    )
}
