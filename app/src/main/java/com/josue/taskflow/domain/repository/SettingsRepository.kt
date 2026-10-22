package com.josue.taskflow.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Contrato de repositorio de configuraciones y preferencias del usuario para la Capa de Dominio.
 */
interface SettingsRepository {
    fun getFiltroGuardadoStream(): Flow<String>

    suspend fun guardarFiltro(filtroNombre: String)
}
