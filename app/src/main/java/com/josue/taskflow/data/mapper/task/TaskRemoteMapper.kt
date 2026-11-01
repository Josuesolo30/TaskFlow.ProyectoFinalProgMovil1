package com.josue.taskflow.data.mapper.task

import com.josue.taskflow.data.local.database.entity.task.TaskEntity
import com.josue.taskflow.data.remote.dto.TaskDto
import com.josue.taskflow.domain.model.task.Task

/**
 * Mapper explícito de TaskDto a modelos de la aplicación.
 * El id local autogenerado por Room independiza la identidad local de la remota.
 */
fun TaskDto.toDomain(): Task {
    return Task(
        id = 0,
        title = title.trim(),
        description = "",
        isCompleted = completed,
        createdAt = 0L
    )
}

fun TaskDto.toEntity(): TaskEntity {
    return TaskEntity(
        id = 0,
        title = title.trim(),
        description = "",
        isCompleted = completed,
        createdAt = 0L
    )
}
