package com.josue.taskflow.datos.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.josue.taskflow.dominio.model.Tarea

/**
 * Entidad de Room para la tabla 'tareas'.
 * Ubicada exclusivamente en la Capa de Datos.
 */
@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val completada: Boolean = false,
)

/**
 * Función de extensión para mapear TareaEntity (Capa de Datos) a Tarea (Capa de Dominio).
 */
fun TareaEntity.toDomain(): Tarea {
    return Tarea(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        completada = completada,
    )
}

/**
 * Función de extensión para mapear Tarea (Capa de Dominio) a TareaEntity (Capa de Datos).
 */
fun Tarea.toEntity(): TareaEntity {
    return TareaEntity(
        id = id,
        titulo = titulo,
        descripcion = descripcion,
        completada = completada,
    )
}
