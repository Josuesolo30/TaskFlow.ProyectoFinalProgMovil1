# Evidencia de Paginación Paging 3 y Funcionamiento Offline-First — Lab 9

## 1. Funcionamiento Offline-First con Room como Única Fuente de Verdad

1. **Carga Inmediata Local**: Al abrir la aplicación, las tareas previamente almacenadas en Room se emiten inmediatamente al `TaskListScreen` sin esperar respuestas de red.
2. **Sincronización Paginada en Segundo Plano**:
   - `DefaultTaskRepository` invoca `fetchAndSyncRemoteTasksPaged()`.
   - `TaskPagingSource` solicita la Página 1 (`_page=1&_limit=10`) a JSONPlaceholder.
   - Cada página se mapea y guarda atómicamente en Room (`tareas` y `remote_task_links`).
   - Room emite la lista acumulada a la UI.
3. **Modo Sin Conexión**: Si se desactiva la conexión Wi-Fi/Datos móviles o si ocurre un fallo de red:
   - Las tareas guardadas en Room siguen mostrándose y estando disponibles para consulta en detalle, cambio de estado, edición de descripción y eliminación local.

---

## 2. Paginación de Paging 3 (`TaskPagingSourceTest`)

Resultados de las pruebas unitarias automatizadas de `TaskPagingSource`:
* **Página 1 (`LoadParams.Refresh`, key = 1)**: Retorna `LoadResult.Page` con 10 elementos y `nextKey = 2`.
* **Página 2 (`LoadParams.Append`, key = 2)**: Retorna `LoadResult.Page` con 10 elementos, `prevKey = 1` y `nextKey = 3`.
* **Fin de Paginación (Página > Límite)**: Retorna `LoadResult.Page` vacía con `nextKey = null`.

---

## 3. Aislamiento Estricto de Capas
* **Capa `domain/`**: Cero importaciones de Paging 3, Retrofit, Room o DTOs.
* **Capa `ui/`**: Observa únicamente `Flow<List<Task>>` de `TaskUseCase`.
* **39 de 39 pruebas pasadas al 100%**.
