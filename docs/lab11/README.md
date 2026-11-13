# Reporte de Pruebas con Turbine, ComposeTestRule y Avance Integrador — Lab 11

## 1. Archivos de Prueba y Verificaciones

### A. Pruebas Unitarias de ViewModel con Turbine (`TaskListViewModelJupiterTest.kt`)
* **Ubicación**: `app/src/test/java/com/josue/taskflow/ui/screen/tasklist/TaskListViewModelJupiterTest.kt`
* **Verificación**: Prueba la prueba reactiva con Turbine (`viewModel.uiState.test { ... }`) sobre la selección del filtro `TaskFilter.COMPLETED`:
  - Recibe el estado inicial `Loading`.
  - Recibe el estado `Content` con 2 tareas.
  - Solicita el filtro `COMPLETED` y recibe el estado `Content` filtrado conteniendo solo la tarea completada (ID 2).
  - Simula emisión de lista sin completadas y recibe el estado `TaskListUiState.Empty` con filtro `COMPLETED`.

### B. Pruebas Instrumentadas de UI con ComposeTestRule (`TaskListScreenTest.kt`)
* **Ubicación**: `app/src/androidTest/java/com/josue/taskflow/ui/screen/tasklist/TaskListScreenTest.kt`
* **Verificación**: Prueba la interacción completa del diálogo "Nueva Tarea":
  - Localiza el FAB mediante `hasContentDescription(R.string.add_task)` y realiza clic.
  - Verifica la apertura del diálogo con título `R.string.new_task_dialog_title`.
  - Comprueba que el botón "Agregar" inicia **deshabilitado** cuando el título está en blanco.
  - Escribe en los campos de título y descripción y verifica que el botón "Agregar" se **habilita**.
  - Pulsa "Agregar", verifica la invocación del callback `onAddTask` y comprueba que el diálogo se cierra tras `TaskOperationResult.Success`.

---

## 2. Avance del Proyecto Integrador: Contador de Tareas Visibles

Se añadió a la pantalla `TaskListScreen` un **contador de tareas visibles** que se actualiza dinámicamente según el filtro activo y la lista actual:
* **`Content` con 2 tareas visibles**: Muestra `"Mostrando 2 tareas"`.
* **`Content` con 1 tarea visible**: Muestra `"Mostrando 1 tarea"`.
* **`Empty` (0 tareas visibles)**: Muestra `"0 tareas visibles"`.
* Derivado directamente de `TaskListUiState.Content.tasks.size` sin consultas extra a la base de datos.

---

## 3. Comandos de Ejecución en Windows PowerShell / CMD

### Pruebas Unitarias JVM:
```powershell
.\gradlew.bat :app:testDebugUnitTest
```
*(Resultado: 49 pruebas unitarias ejecutadas y aprobadas al 100%)*

### Compilación de Pruebas Instrumentadas UI:
```powershell
.\gradlew.bat :app:assembleDebugAndroidTest
```

### Ejecución de Pruebas Instrumentadas en Emulador Conectado:
```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

---

## 4. Rama Git para la Entrega
* **Nombre de la Rama**: `feature/lab11-taskflow`
* **Repositorio**: [Josuesolo30/TaskFlow.ProyectoFinalProgMovil1](https://github.com/Josuesolo30/TaskFlow.ProyectoFinalProgMovil1.git)
