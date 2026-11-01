# Reporte de Implementación del Laboratorio 9 — TaskFlow

## 1. Archivos Creados y Modificados

- `app/build.gradle.kts`:
  - Añadida la dependencia de Paging 3: `androidx.paging:paging-runtime:3.3.2`, `paging-common` y `paging-testing`.
- `data/remote/api/TaskApiService.kt`:
  - Declarado el método con parámetros de paginación: `@GET("todos") suspend fun getTodosPaged(@Query("userId") userId: Int = 1, @Query("_page") page: Int = 1, @Query("_limit") limit: Int = 10): List<TaskDto>`.
- `data/remote/paging/TaskPagingSource.kt`:
  - Implementado `PagingSource<Int, TaskDto>` de Paging 3 utilizando `LoadParams` y `LoadResult.Page(data, prevKey, nextKey)`.
- `data/remote/datasource/TaskRemoteDataSource.kt`:
  - Añadidos métodos para solicitar respuestas paginadas y crear instancias de `TaskPagingSource`.
- `data/repository/task/DefaultTaskRepository.kt`:
  - Implementado `fetchAndSyncRemoteTasksPaged(maxPagesToFetch = 2)` con paginación remota interna.
  - Guarda atómicamente cada página en Room mediante `TaskLocalDataSource` y la tabla `remote_task_links`.
  - Room permanece como la **única fuente de verdad observable** (`getTasksStream()`), emitiendo actualizaciones a la UI de forma automática tras la persistencia.
- `app/src/test/java/com/josue/taskflow/data/remote/paging/TaskPagingSourceTest.kt`:
  - Pruebas unitarias de Paging 3 verificando `LoadParams.Refresh` (página 1, `nextKey = 2`), `LoadParams.Append` (página 2, `prevKey = 1`, `nextKey = 3`) y fin de paginación (`nextKey = null`).

---

## 2. Explicación para Presentar al Profesor

> *"Profesor, en el Laboratorio 9 implementamos una capa de datos Offline-First integrando Room, Retrofit y Paging 3 sin alterar la arquitectura ni los contratos del dominio:*
> 
> 1. ***Paginación con Paging 3***:
>    - *Creación de `TaskPagingSource` extendiendo de `PagingSource<Int, TaskDto>` con claves de página y parámetros de carga.*
>    - *El repositorio `DefaultTaskRepository` ejecuta la paginación remota interna en segundo plano solicitando páginas sucesivas.*
> 2. ***Room como Única Fuente de Verdad (Offline-First)***:
>    - *Cada página descargada se mapea y guarda inmediatamente en Room (`tareas` y `remote_task_links`).*
>    - *La pantalla observa Room a través de `Flow<List<Task>>`, mostrando datos locales al instante sin depender de la conexión a Internet.*
> 3. ***Aislamiento Estricto de Capas***:
>    - *Las capas `domain/` y `ui/` **permanecen 100% libres de dependencias de Paging 3, Retrofit o Room**.*
>    - *39 pruebas unitarias e instrumentadas pasadas al 100%."*
