# Reporte de Implementación del Laboratorio 11 — TaskFlow

## 1. Archivos Creados y Modificados

- `gradle/libs.versions.toml` & `app/build.gradle.kts`:
  - Añadida la librería Turbine (`app.cash.turbine:turbine:1.1.0`) para pruebas reactivas de Flow.
- `app/src/test/java/com/josue/taskflow/ui/screen/tasklist/TaskListViewModelJupiterTest.kt`:
  - Añadida la prueba `10 Ver solo tareas completadas usando Turbine prueba las emisiones reactivas` con `viewModel.uiState.test { ... }`.
- `app/src/main/res/values/strings.xml`:
  - Añadidos recursos de texto para el contador de tareas visibles (`visible_tasks_count`, `visible_tasks_count_one`, `visible_tasks_count_zero`).
- `app/src/main/java/com/josue/taskflow/ui/screen/tasklist/view/TaskListScreen.kt`:
  - Añadida la visualización del contador de tareas visibles derivado de `TaskListUiState.Content.tasks.size` o `Empty`.
- `app/src/androidTest/java/com/josue/taskflow/ui/screen/tasklist/TaskListScreenTest.kt`:
  - Prueba instrumentada UI usando `createComposeRule()` para validar el FAB, apertura de diálogo, deshabilitación de botón sin título, ingreso de texto e invocación del callback.
- `docs/lab11/README.md`:
  - Reporte explicativo del Lab 11 con los comandos de ejecución y rama `feature/lab11-taskflow`.

---

## 2. Explicación para Presentar al Profesor

> *"Profesor, en el Laboratorio 11 implementamos tres componentes clave:*
> 
> 1. ***Pruebas de Flow con Turbine***:
>    - *Añadimos Turbine a `TaskListViewModelJupiterTest` para verificar secuencialmente los estados `Loading`, `Content` inicial, el cambio de filtro a `COMPLETED` y la transición a `Empty`.*
> 2. ***Prueba de Interfaz con ComposeTestRule y Semantics***:
>    - *Creamos `TaskListScreenTest` en `androidTest` usando `createComposeRule()`.*
>    - *Verificamos que al pulsar el FAB se abre el diálogo de 'Nueva Tarea', que el botón 'Agregar' permanece deshabilitado hasta ingresar un título no vacío, y que al confirmar se invoca el callback `onAddTask`.*
> 3. ***Avance del Proyecto Integrador***:
>    - *Agregamos un contador dinámico de tareas visibles en `TaskListScreen` derivado directamente del estado.*
> 4. ***Entrega en Rama***:
>    - *Todo fue integrado en la rama `feature/lab11-taskflow` en GitHub."*
