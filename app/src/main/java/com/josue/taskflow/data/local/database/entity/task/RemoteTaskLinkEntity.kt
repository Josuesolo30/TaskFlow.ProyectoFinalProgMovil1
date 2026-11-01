package com.josue.taskflow.data.local.database.entity.task

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * Entidad de enlace para asociar identidades remotas de la API (provider + remote_id)
 * con los IDs locales autogenerados por Room en 'tareas'.
 * Mantiene la marca 'is_deleted' para evitar reimportar tareas que el usuario eliminó localmente.
 */
@Entity(
    tableName = "remote_task_links",
    primaryKeys = ["provider", "remote_id"]
)
data class RemoteTaskLinkEntity(
    @ColumnInfo(name = "provider")
    val provider: String = "jsonplaceholder",

    @ColumnInfo(name = "remote_id")
    val remoteId: Int,

    @ColumnInfo(name = "local_task_id")
    val localTaskId: Int? = null,

    @ColumnInfo(name = "is_deleted", defaultValue = "0")
    val isDeleted: Boolean = false
)
