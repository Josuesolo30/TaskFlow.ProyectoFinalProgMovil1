package com.josue.taskflow.data

import com.josue.taskflow.domain.model.task.TaskFilter
import com.josue.taskflow.domain.repository.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSettingsRepository : SettingsRepository {
    private val filterState = MutableStateFlow(TaskFilter.ALL)

    override fun getFilterStream(): Flow<TaskFilter> = filterState.asStateFlow()

    override suspend fun saveFilter(filter: TaskFilter) {
        filterState.value = filter
    }
}
