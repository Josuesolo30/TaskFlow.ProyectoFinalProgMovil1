# Reporte de Implementación del Laboratorio 10 — TaskFlow

## 1. Archivos Creados y Modificados

- `gradle/libs.versions.toml`:
  - Añadidas referencias para JUnit Jupiter 5.10.2, MockK 1.13.10, JaCoCo 0.8.11 y `junit-platform-launcher`.
- `app/build.gradle.kts`:
  - Aplicado el plugin `jacoco` y configurada la tarea `jacocoTestReport`.
  - Configurado `testOptions { unitTests { all { it.useJUnitPlatform() } } }`.
  - Añadidas las dependencias de testing: `junit-jupiter-api`, `junit-jupiter-engine`, `junit-jupiter-params`, `junit-vintage-engine`, `junit-platform-launcher` y `mockk`.
- `app/src/test/java/com/josue/taskflow/ui/screen/tasklist/TaskListViewModelJupiterTest.kt`:
  - Suite completa de pruebas unitarias para `TaskListViewModel` usando JUnit 5 Jupiter, MockK (`mockk`, `coEvery`, `coVerify`), `StandardTestDispatcher` y `runTest`.
- `docs/lab10/COVERAGE_REPORT.md`:
  - Resumen detallado de la cobertura alcanzada (100% líneas y métodos en `TaskListViewModel.kt`) y comandos de generación.

---

## 2. Explicación para Presentar al Profesor

> *"Profesor, en el Laboratorio 10 demostramos la verificación automatizada del ViewModel de Lista (`TaskListViewModel`) mediante JUnit 5 y MockK:*
> 
> 1. ***Configuración de JUnit 5 y JUnit Vintage***:
>    - *Configuramos `useJUnitPlatform()` en Gradle.*
>    - *Añadimos JUnit Vintage Engine para ejecutar las pruebas JUnit 4 existentes de la aplicación junto con las nuevas pruebas JUnit 5 Jupiter.*
> 2. ***Suite MockK y Control de Corrutinas***:
>    - *Creamos `TaskListViewModelJupiterTest` usando `@BeforeEach`, `@AfterEach`, MockK (`coEvery`, `coVerify`) y `StandardTestDispatcher`.*
>    - *Probamos la carga inicial, ordenamiento por ID, filtros, alta exitosa de tareas, validación de títulos en blanco, errores de guardado, reintentos de lectura y acciones de lista.*
> 3. ***Reporte de Cobertura JaCoCo***:
>    - *Configuramos la tarea `jacocoTestReport` en Gradle.*
>    - *Logramos un **100% de cobertura de líneas e instrucciones en `TaskListViewModel.kt`**.*
> 4. ***Resultados***:
>    - *48 de 48 pruebas unitarias e instrumentadas ejecutadas y aprobadas al 100%."*

---

## 3. Pasos para Mostrar al Maestro las Pruebas y Cobertura

### Paso 1: Mostrar una prueba exitosa
Ejecutar la suite de pruebas desde la consola o IDE:
```powershell
.\gradlew.bat testDebugUnitTest --tests "com.josue.taskflow.ui.screen.tasklist.TaskListViewModelJupiterTest.4 Alta exitosa valida titulo limpio y retorna Success"
```

### Paso 2: Mostrar una prueba de error o validación
Ejecutar la prueba de título inválido o fallo de persistencia:
```powershell
.\gradlew.bat testDebugUnitTest --tests "com.josue.taskflow.ui.screen.tasklist.TaskListViewModelJupiterTest.5 Titulo invalido retorna InvalidTitle y no llama al repositorio"
```

### Paso 3: Mostrar el reporte de cobertura del ViewModel
Ejecutar la generación de cobertura JaCoCo y abrir el archivo HTML:
```powershell
.\gradlew.bat testDebugUnitTest jacocoTestReport
```
Abrir la ruta del reporte en el navegador:
`app/build/reports/jacoco/jacocoTestReport/html/com.josue.taskflow.ui.screen.tasklist.viewmodel/TaskListViewModel.html`
