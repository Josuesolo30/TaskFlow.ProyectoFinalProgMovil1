package com.josue.taskflow.presentacion.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.josue.taskflow.dominio.usecase.ActualizarDescripcionUseCase
import com.josue.taskflow.dominio.usecase.AgregarTareaUseCase
import com.josue.taskflow.dominio.usecase.CambiarEstadoTareaUseCase
import com.josue.taskflow.dominio.usecase.ObtenerTareasUseCase
import com.josue.taskflow.lab4.FiltroTareas
import com.josue.taskflow.lab4.FuentesTareasSimuladas
import com.josue.taskflow.lab4.Lab4Tareas
import com.josue.taskflow.presentacion.state.TareaUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TareaViewModel(
    private val obtenerTareasUseCase: ObtenerTareasUseCase,
    private val agregarTareaUseCase: AgregarTareaUseCase,
    private val cambiarEstadoTareaUseCase: CambiarEstadoTareaUseCase,
    private val actualizarDescripcionUseCase: ActualizarDescripcionUseCase,
    val confirmacionEventosFlow: Flow<String> = emptyFlow()
) : ViewModel() {

    private val lab4 = Lab4Tareas(
        scope = viewModelScope,
        tareasIniciales = emptyList(),
        registrar = { mensaje ->
            Log.d("LAB4", mensaje)
        }
    )

    val estadoLab4 = lab4.estado

    val tareasSimuladasLab4 = lab4.tareasSimuladas

    private var demoJob: Job? = null

    fun iniciarDemoLab4() {
        demoJob?.cancel()

        lab4.actualizarTareas(emptyList())
        lab4.seleccionarFiltro(FiltroTareas.TODAS)
        lab4.recargar()

        demoJob = viewModelScope.launch {
            launch {
                FuentesTareasSimuladas.tareas().collect { tareas ->
                    lab4.actualizarTareas(tareas)
                }
            }

            launch {
                FuentesTareasSimuladas.filtro().collect { filtro ->
                    lab4.seleccionarFiltro(filtro)
                }
            }
        }
    }

    fun seleccionarFiltroLab4(filtro: FiltroTareas) {
        lab4.seleccionarFiltro(filtro)
    }

    fun simularFalloLab4() {
        lab4.simularFalloTemporal()
    }

    fun simularErrorLab4() {
        lab4.simularError()
    }

    fun recargarLab4() {
        lab4.recargar()
    }

    fun cambiarEstadoSimuladoLab4(id: Int) {
        // Detiene la demo para que una emisión posterior
        // no sobrescriba el cambio manual.
        demoJob?.cancel()

        lab4.cambiarEstadoTareaSimulada(id)
    }

    fun actualizarDescripcionSimuladaLab4(
        id: Int,
        descripcion: String
    ) {
        demoJob?.cancel()

        val tareasActualizadas = tareasSimuladasLab4.value.map { tarea ->
            if (tarea.id == id) {
                tarea.copy(descripcion = descripcion.trim())
            } else {
                tarea
            }
        }

        lab4.actualizarTareas(tareasActualizadas)
    }

    val uiState: StateFlow<TareaUiState> = obtenerTareasUseCase()
        .map { lista ->
            TareaUiState(
                tareas = lista,
                totalTareas = lista.size,
                completadas = lista.count { tarea ->
                    tarea.completada
                },
                cargando = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TareaUiState(cargando = true)
        )

    fun agregarTarea(
        titulo: String,
        descripcion: String = ""
    ) {
        viewModelScope.launch {
            agregarTareaUseCase(titulo, descripcion)
        }
    }

    fun cambiarEstadoTarea(id: Int) {
        viewModelScope.launch {
            cambiarEstadoTareaUseCase(id)
        }
    }

    fun actualizarDescripcion(
        id: Int,
        descripcion: String
    ) {
        viewModelScope.launch {
            actualizarDescripcionUseCase(id, descripcion)
        }
    }
}