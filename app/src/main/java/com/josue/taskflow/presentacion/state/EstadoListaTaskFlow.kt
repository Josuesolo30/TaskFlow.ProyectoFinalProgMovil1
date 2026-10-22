package com.josue.taskflow.presentacion.state

import com.josue.taskflow.dominio.model.Tarea

enum class FiltroTareas {
    TODAS,
    PENDIENTES,
    COMPLETADAS
}

/**
 * Estados reactivos de la pantalla de Lista en la Capa de Presentación (Lab 4).
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
