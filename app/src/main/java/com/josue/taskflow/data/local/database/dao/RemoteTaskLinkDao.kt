package com.josue.taskflow.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.josue.taskflow.data.local.database.entity.task.RemoteTaskLinkEntity

/**
 * DAO para gestionar los enlaces de identidad entre tareas remotas y locales.
 */
@Dao
interface RemoteTaskLinkDao {

    @Query("SELECT * FROM remote_task_links WHERE provider = :provider AND remote_id = :remoteId LIMIT 1")
    suspend fun getLink(provider: String, remoteId: Int): RemoteTaskLinkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLink(link: RemoteTaskLinkEntity)

    @Query("UPDATE remote_task_links SET is_deleted = 1, local_task_id = NULL WHERE local_task_id = :localTaskId")
    suspend fun markDeletedByLocalTaskId(localTaskId: Int)
}
