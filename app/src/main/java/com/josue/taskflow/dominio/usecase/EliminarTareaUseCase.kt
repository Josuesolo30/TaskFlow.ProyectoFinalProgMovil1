package com.josue.taskflow.dominio.usecase

import com.josue.taskflow.dominio.repository.TareaRepository

/**
 * Caso de Uso para eliminar una tarea por su ID.
 */
class EliminarTareaUseCase(
    private val repository: TareaRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.eliminarTarea(id)
    }
}
