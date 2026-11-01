package com.josue.taskflow.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.josue.taskflow.data.local.database.dao.TaskDao
import com.josue.taskflow.data.local.database.entity.task.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
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
                    .addCallback(
                        object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)

                                db.execSQL(
                                    """
                                    INSERT INTO tareas (titulo, descripcion, completada)
                                    VALUES (?, ?, ?)
                                    """.trimIndent(),
                                    arrayOf<Any>(
                                        "Estudiar navegación",
                                        "Repasar Inicio, Lista y Detalle.",
                                        0
                                    )
                                )

                                db.execSQL(
                                    """
                                    INSERT INTO tareas (titulo, descripcion, completada)
                                    VALUES (?, ?, ?)
                                    """.trimIndent(),
                                    arrayOf<Any>(
                                        "Preparar el README",
                                        "Documentar TaskFlow.",
                                        1
                                    )
                                )

                                db.execSQL(
                                    """
                                    INSERT INTO tareas (titulo, descripcion, completada)
                                    VALUES (?, ?, ?)
                                    """.trimIndent(),
                                    arrayOf<Any>(
                                        "Probar la aplicación",
                                        "Verificar todos los botones.",
                                        0
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
