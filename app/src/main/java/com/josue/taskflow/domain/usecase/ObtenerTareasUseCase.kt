package com.josue.taskflow.domain.usecase

import com.josue.taskflow.domain.model.Tarea
import com.josue.taskflow.domain.repository.TareaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerTareasUseCase @Inject constructor(
    private val repository: TareaRepository
) {
    operator fun invoke(): Flow<List<Tarea>> {
        return repository.getTareasStream()
    }
}
