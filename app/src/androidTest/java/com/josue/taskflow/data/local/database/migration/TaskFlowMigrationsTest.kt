package com.josue.taskflow.data.local.database.migration

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.josue.taskflow.data.local.database.TaskFlowDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba instrumentada para verificar la migración manual de Room de la versión 1 a la versión 2.
 */
@RunWith(AndroidJUnit4::class)
class TaskFlowMigrationsTest {

    private val TEST_DB = "migration-test-db"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        TaskFlowDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate1To2_preservesExistingTasksAndAddsCreatedAtColumn() {
        var db = helper.createDatabase(TEST_DB, 1).apply {
            execSQL(
                """
                INSERT INTO tareas (id, titulo, descripcion, completada)
                VALUES (1, 'Tarea 1', 'Descripcion 1', 0),
                       (5, 'Tarea 5', 'Descripcion 5', 1)
                """.trimIndent()
            )
            close()
        }

        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2)

        val cursor = db.query("SELECT id, titulo, descripcion, completada, created_at FROM tareas ORDER BY id ASC")
        assertEquals(2, cursor.count)

        assertTrue(cursor.moveToFirst())
        assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("id")))
        assertEquals("Tarea 1", cursor.getString(cursor.getColumnIndexOrThrow("titulo")))
        assertEquals("Descripcion 1", cursor.getString(cursor.getColumnIndexOrThrow("descripcion")))
        assertEquals(0, cursor.getInt(cursor.getColumnIndexOrThrow("completada")))
        assertEquals(0L, cursor.getLong(cursor.getColumnIndexOrThrow("created_at")))

        assertTrue(cursor.moveToNext())
        assertEquals(5, cursor.getInt(cursor.getColumnIndexOrThrow("id")))
        assertEquals("Tarea 5", cursor.getString(cursor.getColumnIndexOrThrow("titulo")))
        assertEquals("Descripcion 5", cursor.getString(cursor.getColumnIndexOrThrow("descripcion")))
        assertEquals(1, cursor.getInt(cursor.getColumnIndexOrThrow("completada")))
        assertEquals(0L, cursor.getLong(cursor.getColumnIndexOrThrow("created_at")))

        cursor.close()

        val versionCursor = db.query("PRAGMA user_version")
        assertTrue(versionCursor.moveToFirst())
        assertEquals(2, versionCursor.getInt(0))
        versionCursor.close()
    }
}
