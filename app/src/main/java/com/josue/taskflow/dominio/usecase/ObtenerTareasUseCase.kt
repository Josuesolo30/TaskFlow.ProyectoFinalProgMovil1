package com.josue.taskflow.dominio.usecase

import com.josue.taskflow.dominio.model.Tarea
import com.josue.taskflow.dominio.repository.TareaRepository
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso para obtener el flujo continuo de tareas desde el repositorio.
 */
class ObtenerTareasUseCase(
    private val repository: TareaRepository,
) {
    operator fun invoke(): Flow<List<Tarea>> {
        return repository.obtenerTareas()
    }
}
