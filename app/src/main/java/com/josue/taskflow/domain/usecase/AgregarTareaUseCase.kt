package com.josue.taskflow.domain.usecase

import com.josue.taskflow.domain.repository.TareaRepository
import javax.inject.Inject

class AgregarTareaUseCase @Inject constructor(
    private val repository: TareaRepository
) {
    suspend operator fun invoke(titulo: String, descripcion: String = "") {
        val tituloLimpio = titulo.trim()
        if (tituloLimpio.isNotBlank()) {
            repository.agregarTarea(titulo = tituloLimpio, descripcion = descripcion.trim())
        }
    }
}
