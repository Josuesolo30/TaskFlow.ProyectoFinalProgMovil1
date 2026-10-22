# TaskFlow - Laboratorios y Proyecto Integrador

Aplicación Android para la gestión integral de tareas personales, desarrollada con **Kotlin**, **Jetpack Compose**, **Room Database** y **Material 3**. 

Todos los conceptos de los **Laboratorios 0, 1+2, 3, 4, 5 y 6** han sido integrados al **flujo principal e interactivo de la aplicación real** (creación, consulta, filtrado reactivo, edición, reproducción por voz, eliminación y persistencia de tareas).

---

## 📌 Integración de Laboratorios en el Flujo Principal

### 🟢 Lab 0: Interfaz, Estructura y Navegación
* **Estructura Scaffold**: Pantallas integradas con `TopAppBar`, `FloatingActionButton` (diálogo para agregar tareas) e `innerPadding`.
* **Navegación Robusta (`NavHost` + `NavController`)**: Rutas estructuradas (`inicio`, `lista`, `detalle/{itemId}`) pasando argumentos dinámicos y regresando con `popBackStack()`.
* **Intent Implícito (`ACTION_SEND`)**: Compartir datos de tareas reales guardadas mediante un `Intent.createChooser` en la pantalla de detalle.
* **Formularios con `rememberSaveable`**: Conservan el texto de diálogos y formularios durante rotaciones de pantalla.

---

### 开启 Lab 1+2: Genéricos, Varianza y Programación Funcional
* **Librería Genérica (`ContenedorProcesable<T>`)**: Integrada en la lógica de negocio real de TaskFlow (`ObtenerEstadisticasUseCase` y pipeline de ordenamiento).
* **Varianza (`out T` / `in T`)**: Uso de proyecciones covariantes en `agregarDesdeFuente` y contravariantes en `copiarA`.
* **Type Bounds (`where T : Comparable<T>`)**: Operaciones de ordenamiento (`ordenar()`) y cálculo de extremos (`obtenerMaximo()`, `obtenerMinimo()`) aplicadas a la entidad `Tarea : Comparable<Tarea>`.
* **Operadores Funcionales y Scope Functions**: Uso semántico de `map`, `filter`, `fold` (`acumular`), `reduce` y scope functions (`let`, `apply`, `also`, `run`, `with`).
* **Filtrado Inline Reified**: `inline fun <reified R : T> filtrarPorTipo()` para filtrado en tiempo de ejecución.
* **Pruebas Unitarias**: Suite de 16 pruebas unitarias automatizadas pasando al 100%.

---

### 🟠 Lab 3: Corrutinas, Concurrencia y Migración de Callbacks
* **Ciclo de Vida y Scopes**: Uso estricto de `viewModelScope` y `Dispatchers` apropiados (`Dispatchers.IO` para Room, `Dispatchers.Default` para procesamiento pesado).
* **Concurrencia (`async` / `await`)**: Invocación paralela en `ObtenerEstadisticasUseCase` para calcular métricas y resúmenes de texto con `coroutineScope`.
* **Migración de Callbacks a Suspend (`suspendCancellableCoroutine`)**: Adaptación de la API nativa de Android `TextToSpeech` (`TaskAudioPlayer`) para reproducir en voz alta el título y la descripción de cualquier tarea, manejando éxito, error y cancelación cooperativa.
* **Limpieza de Demostraciones**: Eliminados todos los controles de simulación de partidos y polling deportivo.

---

### 🔴 Lab 4: Programación Reactiva con Flow sobre Tareas Reales
Reemplazo completo de datos simulados por las tareas reales de Room y la selección de filtro del usuario:
* **Primera fuente**: Tareas reales emitidas desde Room mediante `Flow<List<Tarea>>`.
* **Segunda fuente**: Filtro activo (`Todas`, `Pendientes`, `Completadas`) gestionado vía `StateFlow<FiltroTareas>`.
* **Pipeline Operadores (`construirPipelineTaskFlow`)**:
  - `filter`: Inspecciona emisiones y descarta listas corruptas o con IDs duplicados.
  - `map`: Limpia espacios en títulos y ordena mediante `ContenedorProcesable`.
  - `combine`: Combina en tiempo real las tareas de Room con el filtro seleccionado.
  - `transform`: Emite el estado de pantalla unificado (`Contenido` o `Vacia`).
  - `retry`: Reintenta hasta 2 veces ante errores recuperables de E/S.
  - `catch`: Captura excepciones finales y emite `EstadoListaTaskFlow.Error`.
* **Interfaz Única**: La pantalla muestra una única lista de tareas reales conectada a este estado.

---

### 🟣 Lab 5: MVVM y Clean Architecture
* **Capa de Dominio (`com.josue.taskflow.dominio`)**:
  - Modelos puros (`model.Tarea`), contratos (`repository.TareaRepository`) y Casos de Uso (`ObtenerTareasUseCase`, `AgregarTareaUseCase`, `CambiarEstadoTareaUseCase`, `ActualizarDescripcionUseCase`, `EliminarTareaUseCase`, `ObtenerEstadisticasUseCase`).
* **Capa de Datos (`com.josue.taskflow.datos`)**:
  - Repositorio con Room (`TareaRepositoryImpl`), `TareaDao`, `TareaEntity` y base de datos SQLite.
* **Capa de Presentación (`com.josue.taskflow.presentacion`)**:
  - `TareaViewModel` y `TareaUiState` aplicando Unidirectional Data Flow (UDF) y StateDown / EventsUp.
* **Inyección de Dependencias Manual**: `TareaViewModelFactory` desacopla la creación del ViewModel.

---

### 🟤 Lab 6: Persistencia con Room y Eventos por Channel
* **Fuente Única de Verdad**: Base de datos SQLite gestionada con Room (`TaskFlowDatabase`, v1).
* **Escrituras Suspend**: Operaciones de inserción, actualización y eliminación.
* **Confirmaciones con `Channel`**: `TareaRepositoryImpl` emite eventos de un solo uso mediante `Channel<String>` (`receiveAsFlow()`), los cuales son observados en `AppNavigation` para mostrar mensajes Toasts resilientes a rotaciones de pantalla.

---

## 📊 Matriz de Cumplimiento e Integración

| Laboratorio | Requisito | Función real de TaskFlow | Archivos | Verificación realizada o pendiente |
| :--- | :--- | :--- | :--- | :--- |
| **Lab 0** | Navigation e Intent Implícito | Navegación entre Inicio, Lista y Detalle; botón "Compartir" tarea vía `ACTION_SEND`. | `AppNavigation.kt`, `DetalleScreen.kt`, `ListaScreen.kt` | **Realizada** (Navegación probada en AVD/pruebas de compilación). |
| **Lab 1+2** | Genéricos, Varianza y Programación Funcional | Filtrado, acumulación de resúmenes y ordenamiento de tareas reales usando `ContenedorProcesable`. | `ContenedorProcesable.kt`, `ObtenerEstadisticasUseCase.kt`, `PipelineTaskFlow.kt` | **Realizada** (16 pruebas unitarias pasadas al 100%). |
| **Lab 3** | Corrutinas, Async y Callbacks a Suspend | Cálculo de métricas concurrentes (`async`) y lectura por voz de tareas (TTS con `suspendCancellableCoroutine`). | `TaskAudioPlayer.kt`, `ObtenerEstadisticasUseCase.kt`, `DetalleScreen.kt` | **Realizada** (Compilación y pruebas de unidad exitosas). |
| **Lab 4** | Flow reactivo y Operadores | Combinación reactiva de Room + StateFlow de filtro con `filter`, `map`, `combine`, `transform`, `retry` y `catch`. | `PipelineTaskFlow.kt`, `TareaViewModel.kt`, `ListaScreen.kt` | **Realizada** (Pruebas unitarias de Flow pasadas al 100%). |
| **Lab 5** | MVVM y Clean Architecture | Separación en capas `dominio`, `datos`, `presentacion` y `ui`; ViewModel desacoplado por Casos de Uso. | `TareaViewModel.kt`, `TareaViewModelFactory.kt`, paquete `dominio/` y `datos/` | **Realizada** (Verificado mediante inspección y pruebas). |
| **Lab 6** | Persistencia Room y Channels | CRUD completo (crear, consultar, editar, completar, eliminar) en Room con eventos por `Channel`. | `TaskFlowDatabase.kt`, `TareaDao.kt`, `TareaRepositoryImpl.kt`, `AppNavigation.kt` | **Realizada** (Persistencia y canal verificados). |

---

## 📸 Evidencias y Capturas de Pantalla

### 📱 Flujo Principal de la Aplicación
| Pantalla de Inicio | Pantalla de Lista | Pantalla de Detalle |
| :---: | :---: | :---: |
| ![Inicio](screenshots/inicio.png) | ![Lista](screenshots/lista.png) | ![Detalle](screenshots/detalle.png) |

---

### 🔴 Lab 4: Diagrama de Marble Reactivo (Room + Filtro Real)

```
Fuente Room:    ---[Tarea 1, Tarea 2, Tarea 3]------------------------->
Fuente Filtro:  ---(TODAS)--------------->(PENDIENTES)---->(COMPLETADAS)->
                       |                       |                 |
Combine & Map:  -------[3 Tareas]------------->[2 Pendientes]---->[1 Completada]
                       |                       |                 |
Transform:      ---> Estado.Contenido ----> Estado.Contenido ---> Estado.Contenido
```

---

## 🛠️ Tecnologías Utilizadas
* **Kotlin 2.x** (Corrutinas, Flow, Genéricos, Varianza, Reified, Scope Functions)
* **Room Database 2.6.1** & **KSP**
* **Clean Architecture & MVVM**
* **Jetpack Compose & Material 3**
* **Navigation Compose 2.9.8**
* **JUnit 4** (Pruebas unitarias)

---

## 🚀 Cómo Ejecutar el Proyecto

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Josuesolo30/TaskFlow.ProyectoFinalProgMovil1.git
   ```
2. Abrir en **Android Studio**.
3. Sincronizar Gradle.
4. Ejecutar las pruebas unitarias:
   ```bash
   ./gradlew test
   ```
5. Ejecutar la aplicación en el emulador o dispositivo físico.

---

## 🤖 Política de Uso de Inteligencia Artificial (Declaración Explícita)

De acuerdo con las directrices académicas de transparencia, se declara explícitamente el uso de herramientas de **Inteligencia Artificial Generativa** como asistente de desarrollo durante la realización de este proyecto.

### 🛠️ Herramientas Utilizadas
* **Agentes de IA / Asistentes en Android Studio**: Utilizados como herramienta de apoyo y *Pair Programmer*.

### 📋 Alcance y Casos de Uso Específicos
1. **Depuración de Entorno y Compilación**:
   * Diagnóstico de errores de compilación y ajuste de `gradle.properties`.
2. **Documentación y Organización del Entregable**:
   * Redacción, maquetación en Markdown y estructuración de la matriz de cumplimiento.
3. **Revisión de Código y Buenas Prácticas**:
   * Integración de patrones Clean Architecture, corrutinas con `suspendCancellableCoroutine` y Flow reactivo.

### 🛡️ Declaración de Responsabilidad y Autoría
Todo el código generado o sugerido con apoyo de IA fue revisado, probado, comprendido y validado manualmente por el autor (**Josue Rafael Piña Fermin**).

---

## 👤 Autor

**Josue Rafael Piña Fermin**  
Estudiante de UNICDA  
Asignatura: Desarrollo Android con Kotlin
