package com.josue.taskflow.dominio.pipeline

import com.josue.taskflow.dominio.model.Tarea
import com.josue.taskflow.presentacion.state.EstadoListaTaskFlow
import com.josue.taskflow.presentacion.state.FiltroTareas
import com.josue.taskflow.util.ContenedorProcesable
import com.josue.taskflow.util.ordenar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.transform
import java.io.IOException

private data class ResultadoFiltroReal(
    val tareas: List<Tarea>,
    val filtro: FiltroTareas
)

/**
 * Pipeline reactivo de Flow para las tareas reales del proyecto TaskFlow (Lab 4).
 *
 * Combina dos fuentes:
 * 1. [fuenteTareas]: Tareas reales emitidas desde Room mediante Flow.
 * 2. [fuenteFiltro]: Filtro activo seleccionado por el usuario vía StateFlow.
 */
fun construirPipelineTaskFlow(
    fuenteTareas: Flow<List<Tarea>>,
    fuenteFiltro: Flow<FiltroTareas>,
    registrarLog: (String) -> Unit = {}
): Flow<EstadoListaTaskFlow> {

    // 1. FILTER (Flow): descarta emisiones inválidas o corruptas (p.ej. IDs duplicados)
    val tareasValidas = fuenteTareas
        .filter { lista ->
            val esValida = lista.map { it.id }.distinct().size == lista.size
            if (!esValida) {
                registrarLog("FILTER Flow: Emisión descartada por IDs duplicados")
            }
            esValida
        }
        // 2. MAP (Flow): limpia títulos y ordena las tareas usando ContenedorProcesable (Lab 1+2)
        .map { lista ->
            val listaLimpia = lista
                .map { tarea -> tarea.copy(titulo = tarea.titulo.trim()) }
                .filter { tarea -> tarea.titulo.isNotBlank() }

            // Ordenamiento con ContenedorProcesable (type bounds Comparable<Tarea>)
            ContenedorProcesable(listaLimpia).ordenar().elementos
        }

    // 3. COMBINE: recalcula cuando cambian las tareas de Room o el filtro activo
    return combine(
        tareasValidas,
        fuenteFiltro
    ) { lista, filtro ->
        val tareasVisibles = when (filtro) {
            FiltroTareas.TODAS -> lista
            FiltroTareas.PENDIENTES -> lista.filter { !it.completada }
            FiltroTareas.COMPLETADAS -> lista.filter { it.completada }
        }
        ResultadoFiltroReal(tareasVisibles, filtro)
    }
        // 4. TRANSFORM (Flow): emite el estado de pantalla (Contenido o Vacia)
        .transform<ResultadoFiltroReal, EstadoListaTaskFlow> { resultado ->
            if (resultado.tareas.isEmpty()) {
                emit(EstadoListaTaskFlow.Vacia(filtro = resultado.filtro))
            } else {
                emit(
                    EstadoListaTaskFlow.Contenido(
                        tareas = resultado.tareas,
                        filtro = resultado.filtro
                    )
                )
            }
        }
        // 5. RETRY: reintenta hasta 2 veces ante errores recuperables de E/S de la base de datos
        .retry(retries = 2) { error ->
            if (error is IOException) {
                registrarLog("RETRY Flow: Error de E/S (${error.message}); reintentando...")
                delay(300)
                true
            } else {
                false
            }
        }
        // 6. ONSTART: avisa estado de carga inicial
        .onStart {
            emit(EstadoListaTaskFlow.Cargando)
        }
        // 7. CATCH: captura errores finales y emite EstadoListaTaskFlow.Error
        .catch { error ->
            registrarLog("CATCH Flow: ${error.message}")
            emit(
                EstadoListaTaskFlow.Error(
                    mensaje = error.message ?: "Error al cargar tareas de la base de datos"
                )
            )
        }
}
