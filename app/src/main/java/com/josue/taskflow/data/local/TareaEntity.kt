package com.josue.taskflow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.josue.taskflow.domain.model.Tarea

/**
 * Entidad de Room para la tabla 'tareas'.
 */
@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val completada: Boolean = false,
)

fun TareaEntity.toDomain(): Tarea {
    return Tarea(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        completada = completada,
    )
}

fun Tarea.toEntity(): TareaEntity {
    return TareaEntity(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        completada = completada,
    )
}
