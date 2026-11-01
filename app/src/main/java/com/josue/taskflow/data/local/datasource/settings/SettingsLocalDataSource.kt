package com.josue.taskflow.data.local.datasource.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.josue.taskflow.domain.model.task.TaskFilter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Fuente de datos local para DataStore Preferences.
 */
class SettingsLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val TASK_FILTER_KEY = stringPreferencesKey("filtro_tareas_activo")
    }

    fun getFilterStream(): Flow<TaskFilter> {
        return dataStore.data.map { preferences ->
            val rawValue = preferences[TASK_FILTER_KEY]
            TaskFilter.fromStorageKey(rawValue)
        }
    }

    suspend fun saveFilter(filter: TaskFilter) {
        dataStore.edit { preferences ->
            preferences[TASK_FILTER_KEY] = filter.storageKey
        }
    }
}
