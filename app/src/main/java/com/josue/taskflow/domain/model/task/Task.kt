package com.josue.taskflow.domain.model.task

/**
 * Modelo de dominio puro para representar una Tarea.
 * Independiente de bibliotecas de Android, Room, Compose o Hilt.
 */
data class Task(
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false
) : Comparable<Task> {
    override fun compareTo(other: Task): Int {
        return this.id.compareTo(other.id)
    }
}
