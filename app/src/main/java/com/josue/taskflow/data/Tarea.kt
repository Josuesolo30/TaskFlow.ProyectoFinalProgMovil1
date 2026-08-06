package com.josue.taskflow.data

/**
 * Modelo de datos de una tarea.
 * data class permite utilizar copy() para crear una versión actualizada.
 */
data class Tarea(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val completada: Boolean = false
)
