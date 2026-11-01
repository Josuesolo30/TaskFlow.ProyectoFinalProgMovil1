package com.josue.taskflow.data.local.database.entity.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Room para la tabla 'tareas'.
 * Conserva los nombres de tabla y columnas SQL existentes para mantener compatibilidad total de datos.
 */
@Entity(tableName = "tareas")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "titulo")
    val title: String,

    @ColumnInfo(name = "descripcion")
    val description: String,

    @ColumnInfo(name = "completada")
    val isCompleted: Boolean = false
)
