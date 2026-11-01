package com.josue.taskflow.domain.model.task

/**
 * Modelo de dominio puro para representar una Tarea.
 * Incluye el timestamp 'createdAt' para indicar la fecha/hora de creación.
 */
data class Task(
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val isCompleted: Boolean = false,
    val createdAt: Long = 0L
) : Comparable<Task> {
    override fun compareTo(other: Task): Int {
        return this.id.compareTo(other.id)
    }
}
