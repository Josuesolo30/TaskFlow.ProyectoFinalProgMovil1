package com.josue.taskflow.dominio.usecase

import com.josue.taskflow.dominio.repository.TareaRepository

/**
 * Caso de uso para validar y agregar una nueva tarea.
 */
class AgregarTareaUseCase(
    private val repository: TareaRepository,
) {
    suspend operator fun invoke(titulo: String) {
        val tituloLimpio = titulo.trim()
        if (tituloLimpio.isNotBlank()) {
            repository.agregarTarea(tituloLimpio)
        }
    }
}
