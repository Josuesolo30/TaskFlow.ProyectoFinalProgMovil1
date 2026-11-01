package com.josue.taskflow.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.josue.taskflow.data.local.database.dao.TaskDao
import com.josue.taskflow.data.local.database.entity.task.TaskEntity
import com.josue.taskflow.data.local.database.migration.MIGRATION_1_2

@Database(
    entities = [TaskEntity::class],
    version = 2,
    exportSchema = true
)
abstract class TaskFlowDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {

        @Volatile
        private var INSTANCE: TaskFlowDatabase? = null

        fun getDatabase(context: Context): TaskFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TaskFlowDatabase::class.java,
                    "taskflow_database"
                )
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(
                        object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)

                                db.execSQL(
                                    """
                                    INSERT INTO tareas (titulo, descripcion, completada, created_at)
                                    VALUES (?, ?, ?, ?)
                                    """.trimIndent(),
                                    arrayOf<Any>(
                                        "Estudiar navegación",
                                        "Repasar Inicio, Lista y Detalle.",
                                        0,
                                        0L
                                    )
                                )

                                db.execSQL(
                                    """
                                    INSERT INTO tareas (titulo, descripcion, completada, created_at)
                                    VALUES (?, ?, ?, ?)
                                    """.trimIndent(),
                                    arrayOf<Any>(
                                        "Preparar el README",
                                        "Documentar TaskFlow.",
                                        1,
                                        0L
                                    )
                                )

                                db.execSQL(
                                    """
                                    INSERT INTO tareas (titulo, descripcion, completada, created_at)
                                    VALUES (?, ?, ?, ?)
                                    """.trimIndent(),
                                    arrayOf<Any>(
                                        "Probar la aplicación",
                                        "Verificar todos los botones.",
                                        0,
                                        0L
                                    )
                                )
                            }
                        }
                    )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
