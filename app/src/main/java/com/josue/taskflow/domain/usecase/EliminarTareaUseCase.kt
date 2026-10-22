package com.josue.taskflow.domain.usecase

import com.josue.taskflow.domain.repository.TareaRepository
import javax.inject.Inject

class EliminarTareaUseCase @Inject constructor(
    private val repository: TareaRepository
) {
    suspend operator fun invoke(id: Int) {
        repository.eliminarTarea(id)
    }
}
