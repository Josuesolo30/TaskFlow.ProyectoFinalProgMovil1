package com.josue.taskflow.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultTaskRepositoryTest {

    private lateinit var repository: FakeTaskRepository

    @Before
    fun setup() {
        repository = FakeTaskRepository()
    }

    @Test
    fun `getTasksStream debe emitir la lista inicial de tareas`() = runBlocking {
        val tasks = repository.getTasksStream().first()
        assertEquals(3, tasks.size)
    }

    @Test
    fun `addTask debe guardar tarea y emitir notificacion`() = runBlocking {
        repository.addTask("Probar Repositorio", "Descripcion")
        val tasks = repository.getTasksStream().first()

        val added = tasks.firstOrNull { it.title == "Probar Repositorio" }
        assertTrue(added != null)

        val notification = repository.getNotificationsStream().first()
        assertTrue(notification.contains("Probar Repositorio"))
    }

    @Test
    fun `deleteTask debe remover tarea de la lista`() = runBlocking {
        repository.deleteTask(1)
        val tasks = repository.getTasksStream().first()

        val deleted = tasks.firstOrNull { it.id == 1 }
        assertTrue(deleted == null)
    }
}
