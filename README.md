# TaskFlow - Laboratorios y Proyecto Integrador

Aplicación Android para la gestión de tareas personales, desarrollada con Kotlin, Jetpack Compose y Material 3. Integra las competencias trabajadas en los **Laboratorios 0, 1+2, 3, 4, 5 y 6** del curso.

---

## 📌 Contenido de los Laboratorios Implementados

### 🟢 Lab 0: Nivelación - Scaffold, Navigation e Intent
* **Estructura con Scaffold**: Uso de `topBar`, `floatingActionButton` (para agregar tareas mediante un `AlertDialog`) y `content` adaptado a `PaddingValues`.
* **Navegación Robusta**: Implementación de `NavHost` + `NavController`, pasando argumentos dinámicos entre pantallas (`itemId`), usando `navigate()` y `popBackStack()`.
* **Intent Implícito y Selector**: Botón **"Compartir"** en la pantalla de detalle que dispara un `Intent` implícito (`ACTION_SEND`) con `Intent.createChooser(...)`.
* **Documentación en código**: Comentario pedagógico explicando la diferencia conceptual entre un **Intent Explícito** y un **Intent Implícito**.

---

### 🔵 Lab 1+2: Librería Genérica con Estilo Funcional
* **Librería Genérica (`ContenedorProcesable<T>`)**: Ubicada en `com.josue.taskflow.util`.
* **Varianza**: Firma de métodos con proyecciones de tipo `Array<out T>` (covariante) y `MutableList<in T>` (contravariante).
* **Type Bounds**: Funciones de comparación y ordenamiento (`obtenerMaximo()`, `obtenerMinimo()`, `ordenar()`) restringidas con `where T : Comparable<T>`.
* **Filtrado Inline Reified**: `inline fun <reified R : T> filtrarPorTipo(): List<R>` para inspección de tipos en tiempo de ejecución.
* **Operadores Funcionales**: Uso de `map` (`transformar`), `filter` (`filtrar`), `fold` (`acumular`) y `reduce` (`reducir`).
* **Scope Functions**: Aplicación de `let`, `run`, `also`, `apply` y `with`, con comentarios explicativos en el código.
* **Pruebas Unitarias**: Suite de 12 pruebas unitarias automatizadas (`ContenedorProcesableTest.kt`) pasando al 100%.

---

### 🟠 Lab 3: Concurrencia y Corrutinas
* **Migración de Callback a Suspend**: Adaptación de APIs asíncronas heredadas basadas en callbacks a funciones suspendidas utilizando `suspendCancellableCoroutine`.
* **Módulo de Sincronización (`MatchSyncService`)**: Gestión de tareas en segundo plano usando `Dispatchers.Main.immediate` y control de ciclo de vida con `Job`.
* **Cancelación Cooperativa**: Implementación de sincronización periódica (*polling*) que responde de forma inmediata a `cancelAndJoin()`.
* **Evidencia en Logs**: Verificación en Logcat (`LAB3`) de que la cancelación se ejecuta limpiamente (`activeCalls=0`).

---

### 🔴 Lab 4: Flow y Programación Reactiva
Se implementan dos fuentes simuladas: una emite listas de tareas y otra emite filtros en momentos diferentes.

* **Pipeline de Operadores Flow**:
  - `filter`: Descarta emisiones con identificadores duplicados.
  - `map`: Limpia títulos y ordena las tareas.
  - `combine`: Combina las tareas con el filtro más reciente.
  - `transform`: Convierte el resultado en contenido o lista vacía.
  - `retry`: Realiza hasta dos reintentos ante `IOException`.
  - `catch`: Transforma el error final en un estado visible.
  - `stateIn`: Expone el resultado como `StateFlow`.
* **Estados en la Interfaz**: Manejo reactivo de estados **Cargando**, **Contenido**, **Vacía** y **Error**.
* **Ruta Independiente**: Las tareas simuladas del Lab 4 cuentan con una pantalla y flujo reactivo propio.

---

### 🟣 Lab 5: MVVM + Clean Architecture (Presentación, Dominio y Datos)
* **Capa de Dominio (`com.josue.taskflow.dominio`)**:
  - **`model.Tarea`**: Modelo de datos de Kotlin puro sin dependencias de Android, Room o Retrofit.
  - **`repository.TareaRepository`**: Interfaz abstracta con funciones `suspend` y `Flow` (Inversión de Dependencias).
  - **Casos de Uso (`usecase`)**: `ObtenerTareasUseCase`, `AgregarTareaUseCase`, `CambiarEstadoTareaUseCase` e `ActualizarDescripcionUseCase` implementando `operator fun invoke()`.
* **Capa de Datos (`com.josue.taskflow.datos`)**:
  - **`repository.TareaRepositoryImpl`**: Implementación de la interfaz de dominio conectada a la fuente de datos.
* **Capa de Presentación (`com.josue.taskflow.presentacion`)**:
  - **`viewmodel.TareaViewModel`**: Expone `UiState` mediante `StateFlow` y depende exclusivamente de los Casos de Uso.
  - **`state.TareaUiState`**: Estado inmutable para la UI siguiendo el patrón Unidirectional Data Flow (**UDF**).
  - **Composables**: Desacoplados de la lógica de negocio aplicando *"State Down, Events Up"*.

---

### 🟤 Lab 6: Persistencia con Room y Consultas Reactivas con Flow
* **Componentes de Room (`com.josue.taskflow.datos.db`)**:
  - **`TareaEntity`**: Entidad de la tabla `tareas`, con los campos `id`, `titulo`, `descripcion` y `completada`. Incluye mappers `toDomain()` y `toEntity()`.
  - **`TareaDao`**: Expone consultas mediante `Flow<List<TareaEntity>>` e inserciones/actualizaciones con funciones `suspend`.
  - **`TaskFlowDatabase`**: Singleton de `RoomDatabase`. Inserta tareas iniciales al crear la base de datos.
* **Repositorio con Room**:
  - `TareaRepositoryImpl` implementa `TareaRepository` utilizando `TareaDao`.
  - Permite crear tareas con título y descripción, consultar tareas, cambiar su estado y actualizar su descripción de forma persistente.
* **Separación de Responsabilidades**:
  - El dominio conserva su independencia de Room.
  - `TareaViewModelFactory` configura el repositorio de Room y lo conecta con los casos de uso.
* **Confirmaciones con Channel**:
  - `TareaRepositoryImpl` emite mensajes mediante `Channel<String>` expuestos como `Flow`.
  - `AppNavigation` observa el flujo del ViewModel y muestra confirmaciones flotantes al crear una tarea, cambiar su estado o guardar su descripción.
* **Verificación de Persistencia y Base de Datos**:
  - Persistencia comprobada tras cerrar/abrir la app y rotación de pantalla.
  - Captura de la tabla `tareas` verificada en Database Inspector.

---

## 📸 Evidencias y Capturas de Pantalla

### 📱 Flujo Principal de la Aplicación
| Pantalla de Inicio | Pantalla de Lista | Pantalla de Detalle |
| :---: | :---: | :---: |
| ![Inicio](screenshots/inicio.png) | ![Lista](screenshots/lista.png) | ![Detalle](screenshots/detalle.png) |

---

### 🟠 Lab 3: Concurrencia y Corrutinas
Muestra del log en Android Studio confirmando la invocación de `cancelAndJoin()`, la cancelación del polling (`CANCELLED T-TIGRES`) y la liberación completa de recursos (`activeCalls=0`):

![Evidencia Cancelación Lab 3](screenshots/lab3_cancellation.png)

---

### 🔴 Lab 4: Programación Reactiva con Flow

#### Diagrama de Emisiones y Combinación (Marble Diagram)
![Diagrama Flow Lab 4](screenshots/lab4_marbles.png)

#### Estados de la Interfaz en Lab 4
| Estado Contenido (Filtro) | Estado Vacío | Estado Error |
| :---: | :---: | :---: |
| ![Contenido Lab 4](screenshots/lab4_contenido.png) | ![Vacío Lab 4](screenshots/lab4_vacia.png) | ![Error Lab 4](screenshots/lab4_error.png) |

#### Evidencia de Operadores (`retry` y `catch`) en Logcat
![Logs Logcat Lab 4](screenshots/lab4_logs.png)

---

### 🟣 Lab 5: Arquitectura Limpia (Clean Architecture)
Estructura del proyecto organizada en capas independientes (**datos**, **dominio** y **presentación**):

![Estructura Clean Architecture Lab 5](screenshots/lab5_capas.png)

---

### 🟤 Lab 6: Persistencia con Room y Confirmaciones con Channel

#### Persistencia y Confirmación en la UI
| Tareas Persistidas en Room | Mensaje de Confirmación (Channel) | Detalle y Edición de Tarea |
| :---: | :---: | :---: |
| ![Lista Room](screenshots/lab6_despues.png) | ![Confirmación Channel](screenshots/lab6_confirmacion.png) | ![Detalle Edición](screenshots/lab6_antes.png) |

#### Inspección de la Base de Datos SQLite (Database Inspector)
![Database Inspector Room](screenshots/lab6_room.png)

---

## 🛠️ Tecnologías Utilizadas

* **Kotlin 2.x** (Genéricos, Varianza, Reified, Scope Functions, Corrutinas)
* **Room Database 2.6.1** & **KSP**
* **Arquitectura MVVM + Clean Architecture**
* **Unidirectional Data Flow (UDF)** con `StateFlow` y `ViewModel`
* **Jetpack Compose** & **Material 3**
* **Navigation Compose 2.9.8**
* **KotlinX Coroutines & Channels** (`suspendCancellableCoroutine`, `cancelAndJoin`, `Channel.receiveAsFlow()`)
* **JUnit 4** para pruebas unitarias de la librería genérica

---

## 🚀 Cómo Ejecutar el Proyecto

1. Clonar este repositorio:
   ```bash
   git clone https://github.com/Josuesolo30/TaskFlow.ProyectoFinalProgMovil1.git
   ```
2. Abrir el proyecto en **Android Studio**.
3. Sincronizar los archivos de Gradle.
4. Para ejecutar las pruebas unitarias del Lab 1+2:
   ```bash
   ./gradlew test
   ```
5. Para ejecutar la aplicación en el emulador: seleccionar el módulo `app` y presionar **Run** (Shift + F10).

---

## 👤 Autor

**Josue Rafael Piña Fermin**  
Estudiante de UNICDA  
Asignatura: Desarrollo Android con Kotlin
