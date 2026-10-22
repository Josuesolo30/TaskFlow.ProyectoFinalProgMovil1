package com.josue.taskflow.domain.usecase

import com.josue.taskflow.domain.repository.TareaRepository
import javax.inject.Inject

class ActualizarDescripcionUseCase @Inject constructor(
    private val repository: TareaRepository
) {
    suspend operator fun invoke(id: Int, descripcion: String) {
        repository.actualizarDescripcion(id = id, descripcion = descripcion.trim())
    }
}
