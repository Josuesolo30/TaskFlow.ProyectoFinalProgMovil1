package com.josue.taskflow.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Fuente de datos local basada en DataStore Preferences para preferencias del usuario.
 */
class SettingsLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val FILTRO_TAREAS_KEY = stringPreferencesKey("filtro_tareas_activo")
    }

    fun getFiltroGuardadoStream(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[FILTRO_TAREAS_KEY] ?: "TODAS"
        }
    }

    suspend fun guardarFiltro(filtroNombre: String) {
        dataStore.edit { preferences ->
            preferences[FILTRO_TAREAS_KEY] = filtroNombre
        }
    }
}
