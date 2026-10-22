package com.josue.taskflow.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DefaultTareaRepositoryTest {

    private lateinit var repository: FakeTareaRepository

    @Before
    fun setup() {
        repository = FakeTareaRepository()
    }

    @Test
    fun `getTareasStream debe emitir la lista inicial de tareas`() = runBlocking {
        val tareas = repository.getTareasStream().first()
        assertEquals(3, tareas.size)
    }

    @Test
    fun `agregarTarea debe guardar tarea y emitir confirmacion`() = runBlocking {
        repository.agregarTarea("Probar Repositorio", "Descripcion")
        val tareas = repository.getTareasStream().first()

        val agregada = tareas.firstOrNull { it.titulo == "Probar Repositorio" }
        assertTrue(agregada != null)

        val confirmacion = repository.getConfirmacionesStream().first()
        assertTrue(confirmacion.contains("Probar Repositorio"))
    }

    @Test
    fun `eliminarTarea debe remover tarea de la lista`() = runBlocking {
        repository.eliminarTarea(1)
        val tareas = repository.getTareasStream().first()

        val eliminada = tareas.firstOrNull { it.id == 1 }
        assertTrue(eliminada == null)
    }
}
