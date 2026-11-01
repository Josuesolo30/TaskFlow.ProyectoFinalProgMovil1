package com.josue.taskflow.domain.model.task

import org.junit.Assert.assertEquals
import org.junit.Test

class TaskFilterTest {

    @Test
    fun `fromStorageKey debe decodificar correctamente claves antiguas y nuevas`() {
        assertEquals(TaskFilter.ALL, TaskFilter.fromStorageKey("TODAS"))
        assertEquals(TaskFilter.ALL, TaskFilter.fromStorageKey("all"))
        assertEquals(TaskFilter.PENDING, TaskFilter.fromStorageKey("PENDIENTES"))
        assertEquals(TaskFilter.PENDING, TaskFilter.fromStorageKey("pending"))
        assertEquals(TaskFilter.COMPLETED, TaskFilter.fromStorageKey("COMPLETADAS"))
        assertEquals(TaskFilter.COMPLETED, TaskFilter.fromStorageKey("completed"))
        assertEquals(TaskFilter.ALL, TaskFilter.fromStorageKey("clave_desconocida"))
        assertEquals(TaskFilter.ALL, TaskFilter.fromStorageKey(null))
    }
}
