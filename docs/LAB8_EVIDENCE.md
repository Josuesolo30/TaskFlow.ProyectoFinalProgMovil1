# Evidencia de Integración de API REST Retrofit — Lab 8

## 1. Petición HTTP Real a JSONPlaceholder

* **Endpoint**: `GET https://jsonplaceholder.typicode.com/todos?userId=1`
* **Método**: `GET`
* **Librerías**: Retrofit 2.11.0 + OkHttp 4.12.0 + converter-kotlinx-serialization
* **Formato de Respuesta JSON**:
  ```json
  [
    {
      "userId": 1,
      "id": 1,
      "title": "delectus aut autem",
      "completed": false
    },
    {
      "userId": 1,
      "id": 2,
      "title": "quis ut nam facilis et officia qui",
      "completed": true
    }
  ]
  ```

---

## 2. Aislamiento de Capas y Constructores de ViewModels

Se confirmó mediante inspección de código que las capas **`domain/`** y **`ui/`** permanecen aisladas de dependencias de red:

* **`HomeViewModel`**: Recibe únicamente `TaskUseCase`.
* **`TaskListViewModel`**: Recibe únicamente `TaskUseCase`.
* **`TaskDetailViewModel`**: Recibe `SavedStateHandle` y `TaskUseCase`.
* **Sin imports de Retrofit / OkHttp**: Ninguna clase de `ui/` o `domain/` importa `retrofit2`, `okhttp3`, `TaskApiService` ni `TaskDto`.

---

## 3. Estrategia 'Room como Fuente de Verdad' e Importación Idempotente

* Las tareas descargadas desde la API REST se mapean a `TaskEntity` mediante `TaskRemoteMapper`.
* Los IDs remotos se asocian a IDs locales en la tabla `remote_task_links`.
* Si una tarea remota ya fue importada, no se duplica.
* Si una tarea importada fue eliminada por el usuario, la marca `is_deleted = 1` en `remote_task_links` evita que la API la vuelva a resucitar.
* Las modificaciones locales (editar descripción, marcar completada) operan localmente en Room sin sobrescribirse por llamadas de red.

---

## 4. Estado de Pruebas Automatizadas
* **36 pruebas unitarias e instrumentadas ejecutadas y aprobadas al 100%**, incluyendo pruebas de Retrofit con `MockWebServer`, `TaskRemoteMapperTest`, `DefaultTaskRepositoryTest` e `UserPreferencesMigrationTest`.
