# Reporte de Corrección del Laboratorio 8 — TaskFlow

## 1. Archivos Creados y Modificados

- `domain/model/sync/SyncStatus.kt`:
  - Modelo de estado de sincronización remota (`Idle`, `Loading`, `Success`, `Error`).
- `domain/repository/task/TaskRepository.kt` & `TaskUseCase.kt`:
  - Expone `observeSyncStatus(): Flow<SyncStatus>` y `suspend fun syncRemoteTasks()`.
- `data/repository/task/DefaultTaskRepository.kt`:
  - Gestiona la sincronización remota con un único intento activo coordinado mediante `Mutex`.
  - Actualiza `syncStatusState` con `SyncStatus.Loading`, `SyncStatus.Success` o `SyncStatus.Error`.
  - Relanza `CancellationException` y restablece `SyncStatus.Idle`.
- `ui/screen/tasklist/viewmodel/TaskListViewModel.kt`:
  - Expone `val syncStatus: StateFlow<SyncStatus>` y la función `retrySync()`.
- `ui/screen/tasklist/view/TaskListScreen.kt` & `TaskListRoute.kt`:
  - Renderiza el banner de sincronización remota: un indicador de carga durante `SyncStatus.Loading` o un aviso de error con el botón **"Reintentar"** durante `SyncStatus.Error`.
  - Mantiene las tareas locales de Room totalmente visibles y editables en modo sin conexión.

---

## 2. Explicación para Presentar al Profesor

> *"Profesor, en el Laboratorio 8 corregimos el manejo de red para asegurar una experiencia Offline-First con aviso de sincronización:*
> 
> 1. ***Estado de Sincronización Separado (`SyncStatus`)***:
>    - *El repositorio expone un `Flow<SyncStatus>` (`Idle`, `Loading`, `Success`, `Error`).*
>    - *Cualquier fallo de red se informa de forma aislada sin bloquear la lista local de Room.*
> 2. ***Sincronización Única e Idempotente (`Mutex`)***:
>    - *El repositorio utiliza `Mutex` para garantizar una sola petición activa.*
> 3. ***Banner de Reintento en UI (`TaskListScreen`)***:
>    - *Si la conexión falla, se muestra un banner con el botón 'Reintentar'.*
>    - *Las tareas guardadas en Room siguen mostrándose y estando operativas sin conexión."*
