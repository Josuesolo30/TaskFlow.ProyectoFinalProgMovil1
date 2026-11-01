# Reporte de Implementación del Laboratorio 7 — TaskFlow

## 1. Archivos Modificados y sus Responsabilidades

- `app/build.gradle.kts`:
  - Configurado `ksp { arg("room.schemaLocation", "$projectDir/schemas") }` para exportar esquemas JSON auténticos.
  - Añadido `androidTest` sourceSet assets para incluir los esquemas en pruebas instrumentadas.
  - Añadida dependencia `androidx.room:room-testing:2.6.1`.
- `app/schemas/.../1.json` y `2.json`:
  - Esquemas de base de datos exportados automáticamente por KSP.
- `data/local/database/entity/task/TaskEntity.kt`:
  - Añadida columna `@ColumnInfo(name = "created_at", defaultValue = "0") val createdAt: Long = 0L`.
- `data/local/database/migration/TaskFlowMigrations.kt`:
  - Definida la migración manual `MIGRATION_1_2`: `ALTER TABLE tareas ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0`.
- `data/local/database/TaskFlowDatabase.kt`:
  - Incrementada la versión a `2`, habilitado `exportSchema = true` y registrada `addMigrations(MIGRATION_1_2)`.
- `domain/model/task/Task.kt` y `data/mapper/task/TaskMapper.kt`:
  - Transportan `createdAt` en la capa de dominio y mappers en ambas direcciones.
- `data/repository/task/DefaultTaskRepository.kt`:
  - Asigna `System.currentTimeMillis()` a `createdAt` únicamente al crear nuevas tareas de usuario. Al editar o marcar se preserva la fecha original.
- `di/DataStoreModule.kt`:
  - Configurado `SharedPreferencesMigration` para migrar `filtro_tareas_activo` desde `taskflow_legacy_preferences` a `user_preferences`.
- `app/src/androidTest/.../TaskFlowMigrationsTest.kt`:
  - Prueba instrumentada con `MigrationTestHelper` validando la migración de versión 1 a versión 2.
- `app/src/test/.../UserPreferencesMigrationTest.kt`:
  - Prueba unitaria de decodificación de claves legadas y nuevas de `TaskFilter`.

---

## 2. Explicación para Presentar al Profesor

> *"Profesor, en el Laboratorio 7 implementamos las migraciones de almacenamiento en la capa Data de TaskFlow:*
> 
> 1. ***Migración Manual de Room (Versión 1 a 2)***:
>    - *Exportamos el esquema auténtico `1.json` con KSP.*
>    - *Añadimos el campo `created_at` a la entidad `TaskEntity` con valor predeterminado `0` para no inventar fechas en datos históricos.*
>    - *Implementamos la migración manual `MIGRATION_1_2` con el comando SQL `ALTER TABLE tareas ADD COLUMN created_at INTEGER NOT NULL DEFAULT 0`.*
>    - *Incrementamos la versión de `TaskFlowDatabase` a 2 y registramos la migración con `.addMigrations(MIGRATION_1_2)`.*
>    - *Validamos con `MigrationTestHelper` que la migración conserva el 100% de los datos y filas anteriores.*
> 2. ***Migración de Preferencias (SharedPreferences a DataStore)***:
>    - *Configuramos `SharedPreferencesMigration` en `DataStoreModule` mediante `produceMigrations`.*
>    - *Migra el filtro activo desde el almacén legado `taskflow_legacy_preferences` hacia `user_preferences.pb` sin perder la preferencia guardada por el usuario.*
> 3. ***Integración y Pruebas***:
>    - *Todas las 31 pruebas unitarias e instrumentadas pasan al 100% y la aplicación compila exitosamente."*
