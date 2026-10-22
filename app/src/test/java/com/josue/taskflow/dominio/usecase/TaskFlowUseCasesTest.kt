package com.josue.taskflow.dominio.usecase

import com.josue.taskflow.datos.repository.TareaRepositoryMemoria
import com.josue.taskflow.dominio.model.Tarea
import com.josue.taskflow.dominio.pipeline.construirPipelineTaskFlow
import com.josue.taskflow.presentacion.state.EstadoListaTaskFlow
import com.josue.taskflow.presentacion.state.FiltroTareas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TaskFlowUseCasesTest {

    private lateinit var repository: TareaRepositoryMemoria

    @Before
    fun setup() {
        repository = TareaRepositoryMemoria
    }

    @Test
    fun `ObtenerEstadisticasUseCase debe calcular correctamente usando ContenedorProcesable`() = runBlocking {
        val useCase = ObtenerEstadisticasUseCase()
        val tareas = listOf(
            Tarea(id = 1, titulo = "Tarea A", descripcion = "", completada = true),
            Tarea(id = 2, titulo = "Tarea B", descripcion = "", completada = false),
            Tarea(id = 3, titulo = "Tarea C", descripcion = "", completada = true)
        )

        val stats = useCase(tareas)

        assertEquals(3, stats.total)
        assertEquals(2, stats.completadas)
        assertEquals(1, stats.pendientes)
        assertEquals(66, stats.porcentajeCompletado)
        assertEquals(3, stats.tareaMaxId?.id)
        assertEquals(1, stats.tareaMinId?.id)
        assertTrue(stats.resumenTexto.contains("Tarea A"))
    }

    @Test
    fun `AgregarTareaUseCase no debe agregar tareas vacias`() = runBlocking {
        val agregarUseCase = AgregarTareaUseCase(repository)
        val obtenerUseCase = ObtenerTareasUseCase(repository)

        val totalInicial = obtenerUseCase().first().size

        agregarUseCase(titulo = "   ", descripcion = "Sin titulo")

        val totalFinal = obtenerUseCase().first().size
        assertEquals(totalInicial, totalFinal)
    }

    @Test
    fun `AgregarTareaUseCase debe agregar tareas validas limpiando espacios`() = runBlocking {
        val agregarUseCase = AgregarTareaUseCase(repository)
        val obtenerUseCase = ObtenerTareasUseCase(repository)

        agregarUseCase(titulo = "  Nueva Tarea Test  ", descripcion = "  Descripcion test  ")

        val tareas = obtenerUseCase().first()
        val agregada = tareas.firstOrNull { it.titulo == "Nueva Tarea Test" }

        assertTrue(agregada != null)
        assertEquals("Descripcion test", agregada?.descripcion)
    }

    @Test
    fun `construirPipelineTaskFlow debe filtrar por estado reactivamente`() = runBlocking {
        val fuenteTareas = MutableStateFlow(
            listOf(
                Tarea(id = 1, titulo = "T1", descripcion = "", completada = true),
                Tarea(id = 2, titulo = "T2", descripcion = "", completada = false)
            )
        )
        val fuenteFiltro = MutableStateFlow(FiltroTareas.PENDIENTES)

        val pipeline = construirPipelineTaskFlow(fuenteTareas, fuenteFiltro)

        val estado = pipeline.first { it is EstadoListaTaskFlow.Contenido } as EstadoListaTaskFlow.Contenido

        assertEquals(1, estado.tareas.size)
        assertEquals(2, estado.tareas.first().id)
        assertEquals(FiltroTareas.PENDIENTES, estado.filtro)
    }
}
