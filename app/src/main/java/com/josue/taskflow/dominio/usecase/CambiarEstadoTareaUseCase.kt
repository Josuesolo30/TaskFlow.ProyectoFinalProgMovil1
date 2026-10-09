package com.josue.taskflow.dominio.usecase

import com.josue.taskflow.dominio.repository.TareaRepository

/**
 * Caso de uso para cambiar el estado (completada / pendiente) de una tarea.
 */
class CambiarEstadoTareaUseCase(
    private val repository: TareaRepository,
) {
    suspend operator fun invoke(id: Int) {
        repository.cambiarEstadoTarea(id)
    }
}
