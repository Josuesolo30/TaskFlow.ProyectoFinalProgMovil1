package com.josue.taskflow.domain.model

/**
 * Modelo puro de dominio para representar una Tarea en la aplicación.
 * Independiente de bibliotecas de Android, Room, Compose o Hilt.
 */
data class Tarea(
    val id: Int = 0,
    val titulo: String,
    val descripcion: String = "",
    val completada: Boolean = false
) : Comparable<Tarea> {
    override fun compareTo(other: Tarea): Int {
        return this.id.compareTo(other.id)
    }
}
