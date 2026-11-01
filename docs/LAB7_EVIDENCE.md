# Evidencia de Migración de Base de Datos y Preferencias — Lab 7

## 1. Verificación de la Migración de Room (Versión 1 ➔ Versión 2)

En la captura tomada directamente desde **Database Inspector** en Android Studio se verifica que la base de datos `taskflow_database` del paquete `com.josue.taskflow` aplicó la migración `MIGRATION_1_2` conservando el 100% de las tareas anteriores y agregando la nueva columna `created_at`:

### 📸 Inspección de la Tabla `tareas` con la Columna `created_at`
| id | titulo | descripcion | completada | created_at |
| :---: | :--- | :--- | :---: | :---: |
| 1 | Estudiar navegación | Repasar NavController, NavHost y las rutas... | 1 | 0 |
| 2 | Preparar el README | Documentar funcionalidades, tecnologías y form... | 1 | 0 |
| 3 | Probar en el AVD | Ejecutar las tres pantallas y verificar todos los b... | 1 | 0 |
| 4 | Prueba LB6 | corazon | 1 | 0 |
| 5 | Otra | 2222 | 0 | 0 |
| 6 | Tarea guardada | pica pollo | 0 | 0 |
| 7 | Prueba de persistencia | Girasol antes | 0 | 0 |

*Observación: Todas las tareas históricas anteriores conservan intactos sus IDs, títulos, descripciones y estados completada/pendiente, con la nueva columna `created_at` inicializada en `0` (DEFAULT 0 SQL) para indicar fecha histórica preservada.*

---

## 2. Verificación de la Interfaz con la Paleta Teal Material 3

En la captura tomada desde el emulador Android se verifica la pantalla **Mis Tareas** (`TaskListScreen`):
* Paleta de colores Teal verde azulado activa en el tema.
* Chips de filtro (`Todas`, `Pendientes`, `Completadas`).
* Botón de eliminación 🗑️ por cada tarea.
* Botón flotante `+` (*FloatingActionButton*) para agregar tareas.
* Integración de los laboratorios sobre las tareas reales almacenadas en Room.

---

## 3. Estado de Pruebas Automatizadas
* **31 de 31 pruebas unitarias e instrumentadas ejecutadas y aprobadas al 100%**, incluyendo la prueba instrumentada `TaskFlowMigrationsTest` con `MigrationTestHelper` y `UserPreferencesMigrationTest`.
