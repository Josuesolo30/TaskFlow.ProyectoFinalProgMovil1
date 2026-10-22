package com.josue.taskflow.presentacion.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josue.taskflow.dominio.pipeline.construirPipelineTaskFlow
import com.josue.taskflow.dominio.usecase.ActualizarDescripcionUseCase
import com.josue.taskflow.dominio.usecase.AgregarTareaUseCase
import com.josue.taskflow.dominio.usecase.CambiarEstadoTareaUseCase
import com.josue.taskflow.dominio.usecase.EliminarTareaUseCase
import com.josue.taskflow.dominio.usecase.ObtenerEstadisticasUseCase
import com.josue.taskflow.dominio.usecase.ObtenerTareasUseCase
import com.josue.taskflow.presentacion.state.EstadoListaTaskFlow
import com.josue.taskflow.presentacion.state.FiltroTareas
import com.josue.taskflow.presentacion.state.TareaUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel principal de la aplicación TaskFlow.
 *
 * Cumple con los patrones MVVM y Clean Architecture (Lab 5):
 * - Depende exclusivamente de los Casos de Uso.
 * - Expone el pipeline reactivo de Flow (Lab 4) conectando Room y el filtro activo.
 * - Expone el flujo de eventos de confirmación de Room vía Channel (Lab 6).
 */
class TareaViewModel(
    private val obtenerTareasUseCase: ObtenerTareasUseCase,
    private val agregarTareaUseCase: AgregarTareaUseCase,
    private val cambiarEstadoTareaUseCase: CambiarEstadoTareaUseCase,
    private val actualizarDescripcionUseCase: ActualizarDescripcionUseCase,
    private val eliminarTareaUseCase: EliminarTareaUseCase,
    private val obtenerEstadisticasUseCase: ObtenerEstadisticasUseCase = ObtenerEstadisticasUseCase(),
    val confirmacionEventosFlow: Flow<String> = emptyFlow()
) : ViewModel() {

    // Fuente de filtro seleccionada por el usuario (StateFlow)
    private val _filtroActivo = MutableStateFlow(FiltroTareas.TODAS)
    val filtroActivo: StateFlow<FiltroTareas> = _filtroActivo.asStateFlow()

    // Pipeline reactivo de Flow (Lab 4) para las tareas reales de Room
    val estadoLista: StateFlow<EstadoListaTaskFlow> = construirPipelineTaskFlow(
        fuenteTareas = obtenerTareasUseCase(),
        fuenteFiltro = _filtroActivo,
        registrarLog = { mensaje -> Log.d("TaskFlow_Flow", mensaje) }
    ).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EstadoListaTaskFlow.Cargando
    )

    // Estado general de la UI con resumen de tareas reales (UDF)
    val uiState: StateFlow<TareaUiState> = obtenerTareasUseCase()
        .map { lista ->
            val stats = obtenerEstadisticasUseCase(lista)
            TareaUiState(
                tareas = lista,
                totalTareas = stats.total,
                completadas = stats.completadas,
                pendientes = stats.pendientes,
                cargando = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TareaUiState(cargando = true)
        )

    fun seleccionarFiltro(filtro: FiltroTareas) {
        _filtroActivo.value = filtro
    }

    fun agregarTarea(titulo: String, descripcion: String = "") {
        viewModelScope.launch {
            agregarTareaUseCase(titulo, descripcion)
        }
    }

    fun cambiarEstadoTarea(id: Int) {
        viewModelScope.launch {
            cambiarEstadoTareaUseCase(id)
        }
    }

    fun actualizarDescripcion(id: Int, descripcion: String) {
        viewModelScope.launch {
            actualizarDescripcionUseCase(id, descripcion)
        }
    }

    fun eliminarTarea(id: Int) {
        viewModelScope.launch {
            eliminarTareaUseCase(id)
        }
    }
}
