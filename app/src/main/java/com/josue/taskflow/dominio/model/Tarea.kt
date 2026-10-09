package com.josue.taskflow.dominio.model

/**
 * Modelo de dominio puro para una Tarea.
 * Capa de Dominio: Sin dependencias de Android, Room, Retrofit u otros frameworks.
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
