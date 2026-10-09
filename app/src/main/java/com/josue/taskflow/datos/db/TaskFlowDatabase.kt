package com.josue.taskflow.datos.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Base de datos de Room para la aplicación TaskFlow.
 */
@Database(entities = [TareaEntity::class], version = 1, exportSchema = false)
abstract class TaskFlowDatabase : RoomDatabase() {

    abstract fun tareaDao(): TareaDao

    companion object {
        @Volatile
        private var INSTANCE: TaskFlowDatabase? = null

        fun getDatabase(context: Context): TaskFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskFlowDatabase::class.java,
                    "taskflow_database"
                )
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                prepoblarBaseDeDatos(database.tareaDao())
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun prepoblarBaseDeDatos(dao: TareaDao) {
            dao.insertarTarea(
                TareaEntity(
                    id = 1,
                    titulo = "Estudiar navegación",
                    descripcion = "Repasar NavController, NavHost y las rutas de cada pantalla."
                )
            )
            dao.insertarTarea(
                TareaEntity(
                    id = 2,
                    titulo = "Preparar el README",
                    descripcion = "Documentar funcionalidades, tecnologías y forma de ejecutar la aplicación.",
                    completada = true
                )
            )
            dao.insertarTarea(
                TareaEntity(
                    id = 3,
                    titulo = "Probar en el AVD",
                    descripcion = "Ejecutar las tres pantallas y verificar todos los botones."
                )
            )
        }
    }
}
