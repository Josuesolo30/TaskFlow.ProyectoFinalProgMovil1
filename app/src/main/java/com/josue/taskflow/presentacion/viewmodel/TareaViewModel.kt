package com.josue.taskflow.presentacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.josue.taskflow.datos.repository.TareaRepositoryImpl
import com.josue.taskflow.dominio.usecase.AgregarTareaUseCase
import com.josue.taskflow.dominio.usecase.CambiarEstadoTareaUseCase
import com.josue.taskflow.dominio.usecase.ObtenerTareasUseCase
import com.josue.taskflow.presentacion.state.TareaUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Capa de Presentación: ViewModel que administra el estado de la UI
 * e invoca únicamente los Casos de Uso (Use Cases) de la capa de Dominio.
 */
class TareaViewModel(
    private val obtenerTareasUseCase: ObtenerTareasUseCase,
    private val agregarTareaUseCase: AgregarTareaUseCase,
    private val cambiarEstadoTareaUseCase: CambiarEstadoTareaUseCase,
) : ViewModel() {

    val uiState: StateFlow<TareaUiState> = obtenerTareasUseCase()
        .map { lista ->
            TareaUiState(
                tareas = lista,
                totalTareas = lista.size,
                completadas = lista.count { it.completada },
                cargando = false,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TareaUiState(cargando = true),
        )

    fun agregarTarea(titulo: String) {
        viewModelScope.launch {
            agregarTareaUseCase(titulo)
        }
    }

    fun cambiarEstadoTarea(id: Int) {
        viewModelScope.launch {
            cambiarEstadoTareaUseCase(id)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class TareaViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TareaViewModel::class.java)) {
            val repository = TareaRepositoryImpl()
            val obtenerTareasUseCase = ObtenerTareasUseCase(repository)
            val agregarTareaUseCase = AgregarTareaUseCase(repository)
            val cambiarEstadoTareaUseCase = CambiarEstadoTareaUseCase(repository)
            return TareaViewModel(
                obtenerTareasUseCase,
                agregarTareaUseCase,
                cambiarEstadoTareaUseCase,
            ) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
