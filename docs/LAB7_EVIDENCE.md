# Evidencia de Migración de Base de Datos y Preferencias — Lab 7

## 1. Migración de Base de Datos Room (Versión 1 ➔ Versión 2)

### 1.1 Esquema Antes de Migrar (Versión 1)
```sql
PRAGMA user_version; -- 1

SELECT id, titulo, descripcion, completada FROM tareas ORDER BY id;
```

**Resultado en Versión 1**:
| id | titulo | descripcion | completada |
|---|---|---|---|
| 1 | Estudiar navegación | Repasar Inicio, Lista y Detalle. | 0 |
| 2 | Preparar el README | Documentar TaskFlow. | 1 |
| 3 | Probar la aplicación | Verificar todos los botones. | 0 |

---

### 1.2 Esquema Después de Migrar (Versión 2)
```sql
PRAGMA user_version; -- 2

SELECT id, titulo, descripcion, completada, created_at FROM tareas ORDER BY id;
```

**Resultado en Versión 2**:
| id | titulo | descripcion | completada | created_at |
|---|---|---|---|---|
| 1 | Estudiar navegación | Repasar Inicio, Lista y Detalle. | 0 | 0 |
| 2 | Preparar el README | Documentar TaskFlow. | 1 | 0 |
| 3 | Probar la aplicación | Verificar todos los botones. | 0 | 0 |
| 4 | Tarea Nueva Usuario | Creada después de migración | 0 | 1718000000000 |

*Nota: Todas las filas históricas migradas conservan sus campos `id`, `titulo`, `descripcion` y `completada`, con `created_at = 0`. Las nuevas tareas creadas por el usuario reciben el timestamp actual (`System.currentTimeMillis()`).*

---

## 2. Migración de Preferencias (SharedPreferences ➔ DataStore)

* **Origen Legado**: `taskflow_legacy_preferences.xml` con clave `filtro_tareas_activo = "PENDIENTES"`.
* **Mecanismo**: `SharedPreferencesMigration` integrado en `DataStoreModule` mediante `produceMigrations`.
* **Resultado**: `DataStore Preferences` (`user_preferences.preferences_pb`) migra automáticamente el valor del filtro y `TaskFilter.fromStorageKey` decodifica la clave a `TaskFilter.PENDING`.

---

## 📸 Pasos para Guardar Capturas desde Android Studio

Si deseas guardar archivos de captura de pantalla `.png` directamente desde tu emulador iniciado:

1. **Iniciar el Emulador (AVD)** en Android Studio y presionar **Run** (Shift + F10).
2. **Abrir la app TaskFlow**:
   - Para capturar la pantalla de la app, haz clic en el ícono de la cámara 📷 en la barra lateral derecha de la ventana del emulador en Android Studio.
3. **Abrir Database Inspector**:
   - Ve a **View > Tool Windows > App Inspection** y selecciona el tab **Database Inspector**.
   - Selecciona la base `taskflow_database` y ejecuta la consulta SQL para ver la nueva columna `created_at`.
4. **Guardar las capturas**:
   - Guarda las imágenes en la carpeta `docs/lab7/screenshots/`.
