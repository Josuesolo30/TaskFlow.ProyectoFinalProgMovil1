package com.josue.taskflow.dominio.usecase

import com.josue.taskflow.dominio.repository.TareaRepository

class ActualizarDescripcionUseCase(
    private val repository: TareaRepository
) {
    suspend operator fun invoke(
        id: Int,
        descripcion: String
    ) {
        repository.actualizarDescripcion(
            id = id,
            descripcion = descripcion.trim()
        )
    }
}