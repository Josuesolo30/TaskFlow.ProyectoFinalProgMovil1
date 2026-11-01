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
