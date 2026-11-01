package com.josue.taskflow.data.local.database.entity.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de Room para la tabla 'tareas'.
 * Versión 2: añade la columna 'created_at' con valor predeterminado 0 para datos históricos.
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
    val isCompleted: Boolean = false,

    @ColumnInfo(name = "created_at", defaultValue = "0")
    val createdAt: Long = 0L
)
