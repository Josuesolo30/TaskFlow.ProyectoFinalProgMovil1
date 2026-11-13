# Evidencia de Sincronización Remota y Reintento en UI — Lab 8

## 1. Funcionamiento del Banner de Reintento y Estado Offline

1. **Sincronización Inicial**:
   - Al iniciar la app o abrir la lista, `DefaultTaskRepository` activa `SyncStatus.Loading`.
   - La pantalla muestra el indicador de sincronización en la parte superior.

2. **Corte de Conexión o Fallo de Red**:
   - Si no hay conexión o la API retorna un error HTTP, `DefaultTaskRepository` captura el error y emite `SyncStatus.Error("Error de conexión...")`.
   - `TaskListScreen` muestra el banner de error con el mensaje y el botón **"Reintentar"**.
   - **Las tareas guardadas en Room permanecen visibles y 100% operativas**.

3. **Recuperación con "Reintentar"**:
   - Al presionar el botón **"Reintentar"**, se invoca `viewModel.retrySync()`.
   - Se ejecuta un nuevo intento de sincronización paginado en `DefaultTaskRepository`.

---

## 2. Pruebas Automatizadas
* **49 de 49 pruebas unitarias e instrumentadas ejecutadas y aprobadas al 100%**.
