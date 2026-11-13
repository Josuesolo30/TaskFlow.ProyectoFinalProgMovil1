# Reporte de Corrección del Laboratorio 4 — TaskFlow

## 1. Archivos Creados y Modificados

- `ui/screen/tasklist/viewmodel/TaskListViewModel.kt`:
  - Refactorizado `uiState` utilizando operadores de Flow:
    - **`Flow.filter`**: Descarta emisiones con IDs duplicados conservando listas vacías válidas.
    - **`Flow.map`**: Normaliza títulos (trim), descarta títulos en blanco y ordena por ID mediante `ProcessableContainer`.
    - **`combine`**: Fusiona las tareas procesadas de Room y el filtro de DataStore.
    - **`Flow.transform`**: Emite `TaskListUiState.Content` o `TaskListUiState.Empty` según las tareas visibles.
    - **`retry(2)`**: Reintenta ante excepciones de E/S con espera de 300 ms.
    - **`catch`**: Emite el estado final `TaskListUiState.Error`.

---

## 2. Diagrama de Marbles (Room + DataStore Filter ➔ UI State)

```
Room Tasks Flow:     --[T1(id:1), T2(id:2)]------------------[T1, T2, T3(id:3)]--->
DataStore Filter:    -----------------------[COMPLETED]-------------------------->
filter (sin dups):   --[T1, T2]------------------------------[T1, T2, T3]---------->
map (trim & sort):   --[T1, T2]------------------------------[T1, T2, T3]---------->
combine (con filter):--[T1, T2, ALL]--------[T1, T2, COMP]---[T1, T2, T3, COMP]--->
transform:           --[Content(T1, T2)]----[Content(T2)]----[Content(T2)]------->
```

---

## 3. Explicación para Presentar al Profesor

> *"Profesor, en el Laboratorio 4 estructuramos el flujo reactivo del ViewModel de Lista empleando operadores de Flow:*
> 
> 1. ***Cadena de Operadores***:
>    - *`filter` filtra emisiones que contengan IDs duplicados.*
>    - *`map` realiza el 'trim' de títulos y la ordenación por ID.*
>    - *`combine` integra el flujo de Room con la preferencia del filtro activo de DataStore.*
>    - *`transform` evalúa las tareas visibles y emite `Content` o `Empty`.*
> 2. ***Resiliencia e Invariantes***:
>    - *Reintentos con `retry(2)` ante errores de E/S y captura final con `catch`.*
>    - *El estado conserva el filtro activo, la lista visible y el contador del Lab 11."*
