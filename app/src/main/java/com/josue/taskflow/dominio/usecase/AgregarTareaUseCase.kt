package com.josue.taskflow.dominio.usecase

import com.josue.taskflow.dominio.repository.TareaRepository

class AgregarTareaUseCase(
    private val repository: TareaRepository
) {
    suspend operator fun invoke(
        titulo: String,
        descripcion: String = ""
    ) {
        val tituloLimpio = titulo.trim()

        if (tituloLimpio.isNotBlank()) {
            repository.agregarTarea(
                titulo = tituloLimpio,
                descripcion = descripcion.trim()
            )
        }
    }
}