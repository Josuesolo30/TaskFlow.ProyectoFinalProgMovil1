package com.josue.taskflow.domain.repository.settings

import com.josue.taskflow.domain.model.task.TaskFilter
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de configuraciones/preferencias para la Capa de Dominio.
 */
interface SettingsRepository {
    fun getFilterStream(): Flow<TaskFilter>

    suspend fun saveFilter(filter: TaskFilter)
}
