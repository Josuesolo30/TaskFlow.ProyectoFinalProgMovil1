package com.josue.taskflow.data.local.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migración manual de Room de la versión 1 a la versión 2.
 * Añade la columna 'created_at' como INTEGER NOT NULL DEFAULT 0 para preservar todos los datos históricos.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE tareas ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0")
    }
}

/**
 * Migración manual de Room de la versión 2 a la versión 3.
 * Crea la tabla 'remote_task_links' para asociar tareas remotas con locales sin colisión de IDs.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `remote_task_links` (
                `provider` TEXT NOT NULL,
                `remote_id` INTEGER NOT NULL,
                `local_task_id` INTEGER,
                `is_deleted` INTEGER NOT NULL DEFAULT 0,
                PRIMARY KEY(`provider`, `remote_id`)
            )
            """.trimIndent()
        )
    }
}
