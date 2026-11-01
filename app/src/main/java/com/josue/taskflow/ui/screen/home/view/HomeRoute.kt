package com.josue.taskflow.ui.screen.home.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.josue.taskflow.ui.screen.home.viewmodel.HomeViewModel

/**
 * Ruta Compose para la pantalla de Inicio.
 * Conecta el ViewModel con el HomeScreen y recolecta el estado respetando el ciclo de vida.
 */
@Composable
fun HomeRoute(
    onNavigateToTasks: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onNavigateToTasks = onNavigateToTasks
    )
}
