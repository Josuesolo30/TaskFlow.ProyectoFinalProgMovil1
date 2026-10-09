package com.josue.taskflow.presentacion.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.josue.taskflow.datos.db.TaskFlowDatabase
import com.josue.taskflow.datos.repository.TareaRepositoryImpl
import com.josue.taskflow.dominio.usecase.AgregarTareaUseCase
import com.josue.taskflow.dominio.usecase.CambiarEstadoTareaUseCase
import com.josue.taskflow.dominio.usecase.ObtenerTareasUseCase
import com.josue.taskflow.lab4.FiltroTareas
import com.josue.taskflow.lab4.FuentesTareasSimuladas
import com.josue.taskflow.lab4.Lab4Tareas
import com.josue.taskflow.presentacion.state.TareaUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TareaViewModel(
    private val obtenerTareasUseCase: ObtenerTareasUseCase,
    private val agregarTareaUseCase: AgregarTareaUseCase,
    private val cambiarEstadoTareaUseCase: CambiarEstadoTareaUseCase
) : ViewModel() {

    // El pipeline del laboratorio usa el scope del ViewModel.
    private val lab4 = Lab4Tareas(
        scope = viewModelScope,
        tareasIniciales = emptyList(),
        registrar = { mensaje ->
            Log.d("LAB4", mensaje)
        }
    )

    // Estado que observará Compose.
    val estadoLab4 = lab4.estado

    val tareasSimuladasLab4 = lab4.tareasSimuladas

    fun cambiarEstadoSimuladoLab4(id: Int) {
        lab4.cambiarEstadoTareaSimulada(id)
    }

    // Permite cancelar una demostración antes de iniciar otra.
    private var demoJob: Job? = null

    fun iniciarDemoLab4() {

        demoJob?.cancel()

        lab4.actualizarTareas(emptyList())
        lab4.seleccionarFiltro(FiltroTareas.TODAS)
        lab4.recargar()

        demoJob = viewModelScope.launch {

            // Ambas corrutinas son hijas del mismo trabajo.

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

    // Estado de las tareas reales almacenadas en Room.
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
            initialValue = TareaUiState(
                cargando = true
            )
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
class TareaViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(TareaViewModel::class.java)) {

            val database =
                TaskFlowDatabase.getDatabase(context)

            val repository =
                TareaRepositoryImpl(database.tareaDao())

            val obtenerTareasUseCase =
                ObtenerTareasUseCase(repository)

            val agregarTareaUseCase =
                AgregarTareaUseCase(repository)

            val cambiarEstadoTareaUseCase =
                CambiarEstadoTareaUseCase(repository)

            return TareaViewModel(
                obtenerTareasUseCase = obtenerTareasUseCase,
                agregarTareaUseCase = agregarTareaUseCase,
                cambiarEstadoTareaUseCase = cambiarEstadoTareaUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}