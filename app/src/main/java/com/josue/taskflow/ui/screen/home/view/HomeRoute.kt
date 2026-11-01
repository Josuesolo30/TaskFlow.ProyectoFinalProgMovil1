package com.josue.taskflow.ui.screen.home.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.josue.taskflow.ui.screen.home.viewmodel.HomeViewModel

/**
 * Ruta Compose para la pantalla de Inicio.
 */
@Composable
fun HomeRoute(
    onNavigateToTasks: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onNavigateToTasks = onNavigateToTasks,
        onRetry = viewModel::retry
    )
}
