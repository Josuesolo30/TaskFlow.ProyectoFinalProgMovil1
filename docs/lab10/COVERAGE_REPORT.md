# Reporte de Cobertura de Pruebas Unitarias (JaCoCo) — Lab 10

## 1. Resumen de Cobertura de `TaskListViewModel.kt`

| Componente | Cobertura de Líneas | Cobertura de Instrucciones | Cobertura de Métodos |
| :--- | :---: | :---: | :---: |
| **`TaskListViewModel.kt`** | **100% (33 / 33 líneas)** | **100% (167 / 167 instrucciones)** | **100% (9 / 9 métodos)** |

---

## 2. Casos de Prueba Ejecutados en `TaskListViewModelJupiterTest`

1. **Carga y Contenido Inicial**:
   - `1 Carga y contenido inicial emite Content con tareas ordenadas y limpias`
   - Emite estado `Content` ordenando por ID, limpiando títulos y descartando títulos en blanco.
2. **Lista Vacía**:
   - `2 Lista vacia emite Empty con el filtro activo`
   - Sin tareas o sin coincidencias de filtro emite `TaskListUiState.Empty`.
3. **Selección de Filtro**:
   - `3 Seleccion de filtro guarda la preferencia en el repositorio`
   - Invocación de `selectFilter(TaskFilter.COMPLETED)` persiste la preferencia mediante `SettingsRepository`.
4. **Alta Exitosa**:
   - `4 Alta exitosa valida titulo limpio y retorna Success`
   - `addTask` limpia títulos y descripciones, envía datos correctos y retorna `TaskOperationResult.Success`.
5. **Título Inválido**:
   - `5 Titulo invalido retorna InvalidTitle y no llama al repositorio`
   - Título en blanco retorna `TaskOperationResult.InvalidTitle`, establece `operationError` y no realiza llamada a base de datos.
6. **Fallo de Guardado**:
   - `6 Fallo de guardado retorna Failure y permite limpiar el error`
   - `TaskOperationResult.Failure` notifica el error y `clearOperationError()` limpia el mensaje.
7. **Fallo de Lectura y Reintentos**:
   - `7 Fallo al leer y reintentos muestra Error cuando se agotan los reintentos`
   - Flujo con `IOException` reintenta con delay de 300 ms y emite `TaskListUiState.Error` al agotar 2 reintentos.
8. **Acciones de Lista**:
   - `8 Acciones de lista toggleCompletion y deleteTask envian ID correcto`
   - Envía los IDs correctos para cambiar estado y eliminar tareas.
9. **Parámetro Predeterminado de Descripción**:
   - `9 Alta con descripcion predeterminada utiliza cadena vacia`
   - Cobertura de llamadas sin descripción explícita.

---

## 3. Comando Exacto para Regenerar el Reporte en Windows

Para regenerar el reporte de cobertura JaCoCo desde Windows PowerShell / CMD:

```powershell
.\gradlew.bat testDebugUnitTest jacocoTestReport
```

* **Ruta del reporte HTML**: `app/build/reports/jacoco/jacocoTestReport/html/index.html`
* **Ruta del reporte XML**: `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`
