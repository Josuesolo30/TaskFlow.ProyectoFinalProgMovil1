package com.josue.taskflow.ui.screen.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josue.taskflow.domain.usecase.task.TaskUseCase
import com.josue.taskflow.ui.screen.home.model.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel propio para la pantalla de Inicio.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    taskUseCase: TaskUseCase
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = taskUseCase.observeStatistics()
        .map { stats ->
            HomeUiState(
                statistics = stats,
                isLoading = false
            )
        }
        .catch { error ->
            emit(
                HomeUiState(
                    isLoading = false,
                    errorMessage = error.message ?: "Error al cargar estadísticas"
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(isLoading = true)
        )
}
