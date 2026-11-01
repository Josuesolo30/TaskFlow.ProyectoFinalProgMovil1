package com.josue.taskflow.data.mapper.task

import com.josue.taskflow.data.local.database.entity.task.TaskEntity
import com.josue.taskflow.domain.model.task.Task

/**
 * Mappers para convertir entre TaskEntity (Capa de Datos) y Task (Capa de Dominio).
 */
fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted
    )
}
