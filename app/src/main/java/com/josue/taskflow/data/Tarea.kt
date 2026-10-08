package com.josue.taskflow.data

/**
 * Modelo de datos de una tarea.
 * Implementa [Comparable] para permitir operaciones de comparación y ordenamiento en el contenedor genérico.
 */
data class Tarea(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val completada: Boolean = false,
) : Comparable<Tarea> {
    override fun compareTo(other: Tarea): Int {
        return this.id.compareTo(other.id)
    }
}
