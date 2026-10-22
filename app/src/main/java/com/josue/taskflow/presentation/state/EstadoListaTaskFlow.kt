package com.josue.taskflow.presentation.state

import com.josue.taskflow.domain.model.Tarea

enum class FiltroTareas {
    TODAS,
    PENDIENTES,
    COMPLETADAS
}

/**
 * Estados de la pantalla de Lista (Capa de Presentación).
 */
sealed interface EstadoListaTaskFlow {
    data object Cargando : EstadoListaTaskFlow

    data class Contenido(
        val tareas: List<Tarea>,
        val filtro: FiltroTareas
    ) : EstadoListaTaskFlow

    data class Vacia(
        val filtro: FiltroTareas
    ) : EstadoListaTaskFlow

    data class Error(
        val mensaje: String
    ) : EstadoListaTaskFlow
}
