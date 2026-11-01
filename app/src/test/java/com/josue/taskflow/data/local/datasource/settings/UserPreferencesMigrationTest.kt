package com.josue.taskflow.data.local.datasource.settings

import com.josue.taskflow.domain.model.task.TaskFilter
import org.junit.Assert.assertEquals
import org.junit.Test

class UserPreferencesMigrationTest {

    @Test
    fun `TaskFilter fromStorageKey decodifica claves legadas y nuevas`() {
        assertEquals(TaskFilter.PENDING, TaskFilter.fromStorageKey("PENDIENTES"))
        assertEquals(TaskFilter.COMPLETED, TaskFilter.fromStorageKey("COMPLETADAS"))
        assertEquals(TaskFilter.ALL, TaskFilter.fromStorageKey("TODAS"))

        assertEquals(TaskFilter.PENDING, TaskFilter.fromStorageKey("pending"))
        assertEquals(TaskFilter.COMPLETED, TaskFilter.fromStorageKey("completed"))
        assertEquals(TaskFilter.ALL, TaskFilter.fromStorageKey("all"))

        assertEquals(TaskFilter.ALL, TaskFilter.fromStorageKey(null))
        assertEquals(TaskFilter.ALL, TaskFilter.fromStorageKey("desconocido"))
    }
}
