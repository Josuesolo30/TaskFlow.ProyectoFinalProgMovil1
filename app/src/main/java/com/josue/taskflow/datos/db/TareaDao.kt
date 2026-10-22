package com.josue.taskflow.datos.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) de Room para la tabla 'tareas'.
 */
@Dao
interface TareaDao {

    @Query("SELECT * FROM tareas ORDER BY id ASC")
    fun obtenerTareas(): Flow<List<TareaEntity>>

    @Query("SELECT * FROM tareas WHERE id = :id LIMIT 1")
    suspend fun obtenerTareaPorId(id: Int): TareaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTarea(tarea: TareaEntity)

    @Update
    suspend fun actualizarTarea(tarea: TareaEntity)

    @Query("DELETE FROM tareas WHERE id = :id")
    suspend fun eliminarTarea(id: Int)

    @Query("SELECT COUNT(*) FROM tareas")
    suspend fun contarTareas(): Int
}
