package com.josue.taskflow.domain.usecase

import com.josue.taskflow.core.collection.ContenedorProcesable
import com.josue.taskflow.core.collection.obtenerMaximo
import com.josue.taskflow.core.collection.obtenerMinimo
import com.josue.taskflow.domain.model.Tarea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class EstadisticasTareas(
    val total: Int = 0,
    val completadas: Int = 0,
    val pendientes: Int = 0,
    val porcentajeCompletado: Int = 0,
    val tareaMaxId: Tarea? = null,
    val tareaMinId: Tarea? = null,
    val resumenTexto: String = ""
)

/**
 * Caso de Uso que procesa estadísticas y resúmenes con ContenedorProcesable y corrutinas concurrentes.
 */
class ObtenerEstadisticasUseCase @Inject constructor() {

    suspend operator fun invoke(tareas: List<Tarea>): EstadisticasTareas = withContext(Dispatchers.Default) {
        if (tareas.isEmpty()) {
            return@withContext EstadisticasTareas()
        }

        val contenedor = ContenedorProcesable(tareas)

        coroutineScope {
            val diferidoConteo = async {
                val completadas = contenedor.filtrar { it.completada }.elementos.size
                val pendientes = contenedor.filtrar { !it.completada }.elementos.size
                val total = contenedor.elementos.size
                val porcentaje = if (total > 0) (completadas * 100) / total else 0
                Triple(completadas, pendientes, porcentaje)
            }

            val diferidoExtremosYResumen = async {
                val maxTarea = contenedor.obtenerMaximo()
                val minTarea = contenedor.obtenerMinimo()

                val resumen = contenedor.acumular("Resumen de tareas:\n") { acum, tarea ->
                    val estado = if (tarea.completada) "[✓]" else "[ ]"
                    "$acum- $estado ${tarea.titulo}\n"
                }
                Triple(maxTarea, minTarea, resumen)
            }

            val (completadas, pendientes, porcentaje) = diferidoConteo.await()
            val (maxTarea, minTarea, resumen) = diferidoExtremosYResumen.await()

            EstadisticasTareas(
                total = tareas.size,
                completadas = completadas,
                pendientes = pendientes,
                porcentajeCompletado = porcentaje,
                tareaMaxId = maxTarea,
                tareaMinId = minTarea,
                resumenTexto = resumen
            )
        }
    }
}
