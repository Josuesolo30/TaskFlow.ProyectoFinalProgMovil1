package com.josue.taskflow.ui.screen.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josue.taskflow.domain.usecase.task.TaskUseCase
import com.josue.taskflow.ui.screen.home.model.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * ViewModel para la pantalla de Inicio.
 * Mantiene la recolección del retryTrigger viva ante errores de lectura de estadísticas.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskUseCase: TaskUseCase
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<HomeUiState> = retryTrigger
        .flatMapLatest {
            taskUseCase.observeStatistics()
                .map<_, HomeUiState> { stats ->
                    HomeUiState.Content(stats)
                }
                .onStart {
                    emit(HomeUiState.Loading)
                }
                .catch { error ->
                    emit(HomeUiState.Error(error.message ?: "Error al cargar estadísticas"))
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Loading
        )

    fun retry() {
        retryTrigger.value++
    }
}
