package com.josue.taskflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josue.taskflow.core.collection.ContenedorProcesable
import com.josue.taskflow.core.collection.ordenar
import com.josue.taskflow.domain.repository.TareaRepository
import com.josue.taskflow.domain.usecase.ActualizarDescripcionUseCase
import com.josue.taskflow.domain.usecase.AgregarTareaUseCase
import com.josue.taskflow.domain.usecase.CambiarEstadoTareaUseCase
import com.josue.taskflow.domain.usecase.EliminarTareaUseCase
import com.josue.taskflow.domain.usecase.GuardarFiltroUseCase
import com.josue.taskflow.domain.usecase.ObtenerEstadisticasUseCase
import com.josue.taskflow.domain.usecase.ObtenerFiltroGuardadoUseCase
import com.josue.taskflow.domain.usecase.ObtenerTareasUseCase
import com.josue.taskflow.presentation.state.EstadoListaTaskFlow
import com.josue.taskflow.presentation.state.FiltroTareas
import com.josue.taskflow.presentation.state.TareaUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

/**
 * ViewModel principal inyectado con Hilt (@HiltViewModel).
 * Mantiene la separación entre dominio y presentación.
 */
@HiltViewModel
class TareaViewModel @Inject constructor(
    private val obtenerTareasUseCase: ObtenerTareasUseCase,
    private val agregarTareaUseCase: AgregarTareaUseCase,
    private val cambiarEstadoTareaUseCase: CambiarEstadoTareaUseCase,
    private val actualizarDescripcionUseCase: ActualizarDescripcionUseCase,
    private val eliminarTareaUseCase: EliminarTareaUseCase,
    private val obtenerEstadisticasUseCase: ObtenerEstadisticasUseCase,
    private val obtenerFiltroGuardadoUseCase: ObtenerFiltroGuardadoUseCase,
    private val guardarFiltroUseCase: GuardarFiltroUseCase,
    tareaRepository: TareaRepository
) : ViewModel() {

    // Canal de confirmaciones unicas emitidas por el repositorio
    val confirmacionEventosFlow: Flow<String> = tareaRepository.getConfirmacionesStream()

    // Filtro activo recuperado de forma persistente desde DataStore (Lab 6 / DataStore)
    val filtroActivo: StateFlow<FiltroTareas> = obtenerFiltroGuardadoUseCase()
        .map { nombre ->
            try {
                FiltroTareas.valueOf(nombre)
            } catch (e: Exception) {
                FiltroTareas.TODAS
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = FiltroTareas.TODAS
        )

    // Pipeline reactivo de lista (Lab 4): combina Room y DataStore
    val estadoLista: StateFlow<EstadoListaTaskFlow> = combine(
        obtenerTareasUseCase(),
        filtroActivo
    ) { lista, filtro ->
        val listaLimpia = lista
            .map { tarea -> tarea.copy(titulo = tarea.titulo.trim()) }
            .filter { tarea -> tarea.titulo.isNotBlank() }

        val ordenadas = ContenedorProcesable(listaLimpia).ordenar().elementos

        val visibles = when (filtro) {
            FiltroTareas.TODAS -> ordenadas
            FiltroTareas.PENDIENTES -> ordenadas.filter { !it.completada }
            FiltroTareas.COMPLETADAS -> ordenadas.filter { it.completada }
        }

        if (visibles.isEmpty()) {
            EstadoListaTaskFlow.Vacia(filtro)
        } else {
            EstadoListaTaskFlow.Contenido(visibles, filtro)
        }
    }
        .retry(2) { error ->
            if (error is IOException) {
                delay(300)
                true
            } else false
        }
        .catch { error ->
            emit(EstadoListaTaskFlow.Error(error.message ?: "Error al cargar tareas"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = EstadoListaTaskFlow.Cargando
        )

    // Estado general de UI para pantalla Inicio y resúmenes (UDF)
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
        viewModelScope.launch {
            guardarFiltroUseCase(filtro.name)
        }
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
