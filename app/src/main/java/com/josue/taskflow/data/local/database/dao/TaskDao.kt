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
 * Las operaciones de modificación retornan el número de filas afectadas para verificar el éxito real.
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
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity): Int

    @Query("UPDATE tareas SET descripcion = :description WHERE id = :taskId")
    suspend fun updateDescription(taskId: Int, description: String): Int

    @Query("UPDATE tareas SET completada = CASE WHEN completada = 1 THEN 0 ELSE 1 END WHERE id = :taskId")
    suspend fun toggleCompletion(taskId: Int): Int

    @Query("DELETE FROM tareas WHERE id = :taskId")
    suspend fun deleteTask(taskId: Int): Int

    @Query("SELECT COUNT(*) FROM tareas")
    suspend fun getTaskCount(): Int
}
