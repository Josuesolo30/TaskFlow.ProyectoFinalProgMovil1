package com.josue.taskflow.data

import com.josue.taskflow.data.local.SettingsLocalDataSource
import com.josue.taskflow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación del contrato SettingsRepository con DataStore Preferences.
 */
@Singleton
class DefaultSettingsRepository @Inject constructor(
    private val settingsLocalDataSource: SettingsLocalDataSource
) : SettingsRepository {

    override fun getFiltroGuardadoStream(): Flow<String> {
        return settingsLocalDataSource.getFiltroGuardadoStream()
    }

    override suspend fun guardarFiltro(filtroNombre: String) {
        settingsLocalDataSource.guardarFiltro(filtroNombre)
    }
}
