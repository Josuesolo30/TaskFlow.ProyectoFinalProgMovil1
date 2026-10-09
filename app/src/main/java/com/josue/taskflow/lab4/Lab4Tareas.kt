package com.josue.taskflow.lab4

import com.josue.taskflow.dominio.model.Tarea
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform

// Las opciones disponibles para filtrar las tareas.
enum class FiltroTareas {
    TODAS,
    PENDIENTES,
    COMPLETADAS
}

// Los posibles estados que puede mostrar el panel.
sealed interface EstadoListaLab4 {

    data object Cargando : EstadoListaLab4

    data class Contenido(
        val tareas: List<Tarea>,
        val filtro: FiltroTareas
    ) : EstadoListaLab4

    data class Vacia(
        val filtro: FiltroTareas
    ) : EstadoListaLab4

    data class Error(
        val mensaje: String
    ) : EstadoListaLab4
}

// Resultado intermedio, antes de convertirlo en un estado de pantalla.
private data class ResultadoFiltro(
    val tareas: List<Tarea>,
    val filtro: FiltroTareas
)

/**
 * Construye el pipeline reactivo del LAB 4.
 *
 * Recibe dos fuentes:
 * 1. Una fuente de listas de tareas.
 * 2. Una fuente de filtros.
 */
fun construirPipelineTareas(
    fuenteTareas: Flow<List<Tarea>>,
    fuenteFiltro: Flow<FiltroTareas>,
    registrar: (String) -> Unit = {}
): Flow<EstadoListaLab4> {

    val tareasValidas = fuenteTareas

        // FILTER de Flow:
        // descarta una emisión completa si contiene IDs duplicados.
        .filter { lista ->
            val valida =
                lista.map { it.id }.distinct().size == lista.size

            if (!valida) {
                registrar("FILTER lista descartada: IDs duplicados")
            }

            valida
        }

        // MAP de Flow:
        // transforma cada lista que llega.
        .map { lista ->
            lista
                // Elimina espacios sobrantes del título.
                .map { tarea ->
                    tarea.copy(titulo = tarea.titulo.trim())
                }

                // Elimina tareas con títulos vacíos.
                .filter { tarea ->
                    tarea.titulo.isNotBlank()
                }

                // Ordena las tareas por ID.
                .sortedBy { tarea ->
                    tarea.id
                }
        }

    // COMBINE:
    // vuelve a calcular el resultado cuando cambia cualquiera
    // de las dos fuentes, usando sus valores más recientes.
    return combine(
        tareasValidas,
        fuenteFiltro
    ) { lista, filtro ->

        val visibles = when (filtro) {

            FiltroTareas.TODAS -> lista

            FiltroTareas.PENDIENTES ->
                lista.filter { tarea ->
                    !tarea.completada
                }

            FiltroTareas.COMPLETADAS ->
                lista.filter { tarea ->
                    tarea.completada
                }
        }

        ResultadoFiltro(
            tareas = visibles,
            filtro = filtro
        )
    }

        // TRANSFORM:
        // decide qué estado debe recibir la pantalla.
        .transform<ResultadoFiltro, EstadoListaLab4> { resultado ->

            if (resultado.tareas.isEmpty()) {

                emit(
                    EstadoListaLab4.Vacia(
                        filtro = resultado.filtro
                    )
                )

            } else {

                emit(
                    EstadoListaLab4.Contenido(
                        tareas = resultado.tareas,
                        filtro = resultado.filtro
                    )
                )
            }
        }

        // RETRY:
        // realiza hasta dos reintentos adicionales
        // cuando ocurre un error de entrada/salida.
        .retry(retries = 2) { error ->

            if (error is IOException) {

                registrar(
                    "RETRY: ${error.message}; espera=400ms"
                )

                // Espera sin bloquear el hilo.
                delay(400)

                true

            } else {

                false
            }
        }

        // Antes de comenzar, avisa que está cargando.
        .onStart {
            emit(EstadoListaLab4.Cargando)
        }

        // CATCH:
        // convierte el error final en un estado visible.
        .catch { error ->

            registrar("CATCH: ${error.message}")

            emit(
                EstadoListaLab4.Error(
                    mensaje = error.message ?: "Error desconocido"
                )
            )
        }

        // Registra cada estado para verlo en Logcat.
        .onEach { estado ->
            registrar("ESTADO: $estado")
        }
}

/**
 * Administra las fuentes y expone el resultado como StateFlow.
 *
 * El scope lo proporciona el ViewModel.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class Lab4Tareas(
    scope: CoroutineScope,
    tareasIniciales: List<Tarea>,
    filtroInicial: FiltroTareas = FiltroTareas.TODAS,
    private val registrar: (String) -> Unit = {}
) {

    private val fuenteTareas =
        MutableStateFlow(tareasIniciales.toList())

    private val fuenteFiltro =
        MutableStateFlow(filtroInicial)

    private val recarga =
        MutableStateFlow(0)

    private val fallosPendientes =
        AtomicInteger(0)

    fun actualizarTareas(tareas: List<Tarea>) {
        fuenteTareas.value = tareas.toList()
    }

    // Permite consultar todas las tareas simuladas,
// incluso cuando el filtro oculta alguna.
    val tareasSimuladas: StateFlow<List<Tarea>>
        get() = fuenteTareas

    // Cambia el check de una tarea simulada.
    fun cambiarEstadoTareaSimulada(id: Int) {
        fuenteTareas.value = fuenteTareas.value.map { tarea ->
            if (tarea.id == id) {
                tarea.copy(completada = !tarea.completada)
            } else {
                tarea
            }
        }
    }

    fun seleccionarFiltro(filtro: FiltroTareas) {
        fuenteFiltro.value = filtro
    }

    // Falla una vez y después permite recuperarse.
    fun simularFalloTemporal() {
        fallosPendientes.set(1)
        recarga.value++
    }

    // Falla tres veces:
    // intento inicial + dos reintentos.
    fun simularError() {
        fallosPendientes.set(3)
        recarga.value++
    }

    fun recargar() {
        fallosPendientes.set(0)
        recarga.value++
    }

    private fun cargarTareas(): Flow<List<Tarea>> = flow {

        registrar("CARGA inicio")

        try {

            // Simula una operación de entrada/salida.
            delay(300)

            val pendientes = fallosPendientes.getAndUpdate { cantidad ->
                if (cantidad > 0) {
                    cantidad - 1
                } else {
                    0
                }
            }

            if (pendientes > 0) {
                throw IOException(
                    "No se pudieron cargar las tareas"
                )
            }

            // Continúa observando las actualizaciones de tareas.
            emitAll(fuenteTareas)

        } finally {

            registrar("CARGA fin")
        }
    }

    val estado: StateFlow<EstadoListaLab4> = recarga

        // Una nueva recarga cancela la carga anterior.
        .flatMapLatest {
            construirPipelineTareas(
                fuenteTareas = cargarTareas(),
                fuenteFiltro = fuenteFiltro,
                registrar = registrar
            )
        }

        // Convierte el Flow en un estado observable.
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(
                stopTimeoutMillis = 0,
                replayExpirationMillis = 0
            ),
            initialValue = EstadoListaLab4.Cargando
        )
}

