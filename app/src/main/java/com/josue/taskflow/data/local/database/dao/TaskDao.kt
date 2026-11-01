package com.josue.taskflow.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.josue.taskflow.data.local.database.entity.task.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO de Room para operaciones SQL sobre la tabla 'tareas'.
 */
@Dao
interface TaskDao {

    @Query("SELECT * FROM tareas ORDER BY id ASC")
    fun getTasksStream(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tareas WHERE id = :taskId LIMIT 1")
    fun getTaskStream(taskId: Int): Flow<TaskEntity?>

    @Query("SELECT * FROM tareas WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tareas WHERE id = :taskId")
    suspend fun deleteTask(taskId: Int)

    @Query("SELECT COUNT(*) FROM tareas")
    suspend fun getTaskCount(): Int
}
