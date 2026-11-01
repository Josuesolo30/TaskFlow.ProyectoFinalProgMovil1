package com.josue.taskflow.domain.model.task

/**
 * Filtros disponibles para clasificar las tareas.
 * Incluye compatibilidad con claves de persistencia nuevas ("all", "pending", "completed")
 * y antiguas ("TODAS", "PENDIENTES", "COMPLETADAS").
 */
enum class TaskFilter(val storageKey: String) {
    ALL("all"),
    PENDING("pending"),
    COMPLETED("completed");

    companion object {
        fun fromStorageKey(rawKey: String?): TaskFilter {
            return when (rawKey?.lowercase()?.trim()) {
                "all", "todas" -> ALL
                "pending", "pendientes" -> PENDING
                "completed", "completadas", "hechas" -> COMPLETED
                else -> ALL
            }
        }
    }
}
