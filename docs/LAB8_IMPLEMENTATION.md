# Reporte de Implementación del Laboratorio 8 — TaskFlow

## 1. Archivos Creados y Modificados

- `data/remote/dto/TaskDto.kt`:
  - DTO serializable con `@Serializable` para deserializar el JSON de JSONPlaceholder.
- `data/remote/api/TaskApiService.kt`:
  - Interfaz Retrofit declarando `@GET("todos") suspend fun getTodos(@Query("userId") userId: Int = 1): List<TaskDto>`.
- `data/remote/datasource/TaskRemoteDataSource.kt`:
  - Fuente remota inyectada con `TaskApiService`.
- `data/mapper/task/TaskRemoteMapper.kt`:
  - Mappers `TaskDto.toDomain()` y `TaskDto.toEntity()`.
- `data/local/database/entity/task/RemoteTaskLinkEntity.kt` y `TaskFlowDatabase.kt` (v3):
  - Tabla de enlace `remote_task_links` para relacionar IDs remotos y locales sin colisiones ni resucitar tareas eliminadas.
- `di/NetworkModule.kt`:
  - Módulo Hilt que provee `Json`, `OkHttpClient`, `Retrofit` y `TaskApiService`.
- `data/repository/task/DefaultTaskRepository.kt`:
  - Incorpora `fetchAndSyncRemoteTasks()` de forma asíncrona al iniciar la observación de tareas de Room, con control de concurrencia mediante `Mutex`.
- `app/src/test/java/com/josue/taskflow/data/remote/TaskApiServiceTest.kt`:
  - Pruebas unitarias de Retrofit con `MockWebServer`.
- `app/src/test/java/com/josue/taskflow/data/mapper/TaskRemoteMapperTest.kt`:
  - Pruebas unitarias del mapeo de DTOs remotos.

---

## 2. Explicación para Presentar al Profesor

> *"Profesor, en el Laboratorio 8 integramos el consumo de una API REST pública con Retrofit manteniendo Room como la fuente única de verdad:*
> 
> 1. ***Consumo Remoto e Inyección***:
>    - *Configuramos Retrofit con `converter-kotlinx-serialization` e inyectamos `TaskApiService` mediante Hilt en `NetworkModule`.*
>    - *Consumimos el endpoint real `GET https://jsonplaceholder.typicode.com/todos?userId=1`.*
> 2. ***Aislamiento Estricto de Capas***:
>    - *Las capas `domain/` y `ui/` no contienen ninguna dependencia de Retrofit, OkHttp ni DTOs.*
>    - *Los ViewModels siguen recibiendo exclusivamente `TaskUseCase`.*
> 3. ***Importación Idempotente y Preservación de Datos***:
>    - *Creamos la tabla `remote_task_links` en Room (versión 3) para enlazar los IDs remotos con IDs locales autogenerados.*
>    - *Evitamos duplicar tareas importadas o resucitar aquellas que el usuario eliminó localmente.*
>    - *Si la red falla pero existen datos locales en Room, la app muestra los datos locales sin interrumpir la experiencia de usuario.*
> 4. ***Pruebas y Verificación***:
>    - *Implementamos pruebas unitarias con `MockWebServer` para simular respuestas 200, 500 y JSONs de la API.*
>    - *36 pruebas pasadas al 100% y la aplicación compila limpiamente."*
