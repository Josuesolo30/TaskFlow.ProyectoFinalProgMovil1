package com.josue.taskflow.data.repository.settings

import com.josue.taskflow.data.local.datasource.settings.SettingsLocalDataSource
import com.josue.taskflow.domain.model.task.TaskFilter
import com.josue.taskflow.domain.repository.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementación principal de SettingsRepository utilizando DataStore Preferences.
 */
@Singleton
class DefaultSettingsRepository @Inject constructor(
    private val localDataSource: SettingsLocalDataSource
) : SettingsRepository {

    override fun getFilterStream(): Flow<TaskFilter> {
        return localDataSource.getFilterStream()
    }

    override suspend fun saveFilter(filter: TaskFilter) {
        localDataSource.saveFilter(filter)
    }
}
