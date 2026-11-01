# TaskFlow - Gestor de Tareas Personales

Aplicación Android para la gestión integral de tareas personales, desarrollada con **Kotlin**, **Jetpack Compose**, **Room Database**, **DataStore Preferences**, **Hilt** y **Material 3 (Teal Theme)**. 

---

## 🏗️ Arquitectura y Estructura del Proyecto

El proyecto está organizado según las recomendaciones de arquitectura de Android y Clean Architecture:

```
app/src/main/java/com/josue/taskflow/
├── TaskFlowApplication.kt            # @HiltAndroidApp
├── MainActivity.kt                   # @AndroidEntryPoint
├── core/                            # Módulos core independientes
│   ├── audio/TaskAudioPlayer.kt     # Adaptador suspendCancellableCoroutine para Android TTS
│   └── collection/ProcessableContainer.kt # Colección genérica, varianza y type bounds
├── data/                            # Capa de Datos (Implementaciones y Mappers)
│   ├── local/
│   │   ├── database/
│   │   │   ├── dao/TaskDao.kt       # Consultas SQL Room
│   │   │   ├── entity/task/TaskEntity.kt # Entidad de la tabla 'tareas'
│   │   │   └── TaskFlowDatabase.kt # Base de datos SQLite Room (v1)
│   │   └── datasource/
│   │       ├── task/TaskLocalDataSource.kt # Fuente local Room
│   │       └── settings/SettingsLocalDataSource.kt # Fuente local DataStore
│   ├── mapper/task/TaskMapper.kt   # Conversión toDomain() / toEntity()
│   └── repository/
│       ├── task/DefaultTaskRepository.kt # Repositorio de tareas
│       └── settings/DefaultSettingsRepository.kt # Repositorio de preferencias
├── di/                              # Módulos de Inyección de Dependencias (Hilt)
│   ├── DatabaseModule.kt            # Proveedor de Room Database y TaskDao
│   ├── DataStoreModule.kt           # Proveedor de DataStore Preferences
│   └── RepositoryModule.kt          # Binding de repositorios
├── domain/                          # Capa de Dominio (Modelos puros y Casos de Uso)
│   ├── model/
│   │   ├── task/Task.kt             # Modelo puro de tarea
│   │   ├── task/TaskFilter.kt       # Enum de filtros con decodificación legado/nuevo
│   │   └── statistics/TaskStatistics.kt # Modelo de estadísticas
│   ├── repository/
│   │   ├── task/TaskRepository.kt   # Contrato de datos de tareas
│   │   └── settings/SettingsRepository.kt # Contrato de preferencias
│   └── usecase/
│       └── task/TaskUseCase.kt      # Caso de uso unificado con las operaciones de tareas
└── ui/                              # Capa de Interfaz de Usuario (Compose UI)
    ├── components/TaskCard.kt       # Componente reutilizable con objetivo >= 48dp
    ├── message/AppMessageHost.kt    # Receptor único de avisos Toast a nivel raíz
    ├── navigation/
    │   ├── AppNavHost.kt            # NavHost principal
    │   └── AppDestination.kt        # Rutas y argumentos de navegación
    ├── screen/
    │   ├── home/                    # Pantalla de Inicio (Route / Screen / ViewModel / State)
    │   │   ├── model/HomeUiState.kt
    │   │   ├── view/HomeRoute.kt
    │   │   ├── view/HomeScreen.kt
    │   │   └── viewmodel/HomeViewModel.kt
    │   ├── tasklist/                # Pantalla de Lista de Tareas
    │   │   ├── model/TaskListUiState.kt
    │   │   ├── view/TaskListRoute.kt
    │   │   ├── view/TaskListScreen.kt
    │   │   └── viewmodel/TaskListViewModel.kt
    │   └── taskdetail/              # Pantalla de Detalle de Tarea
    │       ├── model/TaskDetailUiState.kt
    │       ├── view/TaskDetailRoute.kt
    │       ├── view/TaskDetailScreen.kt
    │       └── viewmodel/TaskDetailViewModel.kt
    └── theme/
        ├── ColorPalette.kt          # Paleta verde azulado Teal Material 3
        ├── TaskFlowTheme.kt         # Temas Claro y Oscuro
        └── Typography.kt            # Tipografía Material 3
```

---

## 📊 Matriz de Cumplimiento y Estado de Prácticas

| Práctica | Archivo que la implementa | Estado | Evidencia o verificación realizada |
| :--- | :--- | :--- | :--- |
| **Vistas en Capa UI** | `ui/screen/`, `ui/components/`, `ui/theme/` | **Completo** | Todas las vistas, Compose y temas organizados bajo `ui/`. |
| **Un Caso de Uso con Operaciones (`TaskUseCase`)** | `domain/usecase/task/TaskUseCase.kt` | **Completo** | Agrupa `observeTasks`, `observeTask`, `observeStatistics`, `observeFilter`, `saveFilter`, `addTask`, `toggleCompletion`, `updateDescription`, `deleteTask`. |
| **Una Pantalla, Un ViewModel** | `HomeViewModel.kt`, `TaskListViewModel.kt`, `TaskDetailViewModel.kt` | **Completo** | Cada pantalla posee su propio ViewModel `@HiltViewModel` e inyectado con `hiltViewModel()`. |
| **Subcarpetas por Pantalla y Capa** | `ui/screen/home/`, `ui/screen/tasklist/`, `ui/screen/taskdetail/`, `domain/model/`, `data/local/` | **Completo** | Subcarpetas `view`, `viewmodel` y `model` organizan cada pantalla; modelos de dominio y persistencia separados. |
| **Nombres en Inglés y Normalización** | Todo el proyecto | **Completo** | Nombres de clases, métodos, variables e identificadores traducidos a inglés. Textos UI visibles conservados en español vía `strings.xml`. |
| **Paleta Teal Material 3 Legible** | `ui/theme/ColorPalette.kt`, `ui/theme/TaskFlowTheme.kt` | **Completo** | Paleta Teal verde azulado con contraste verificado >= 4.5:1 para temas claro y oscuro. |
| **Receptor Único de Mensajes** | `ui/message/AppMessageHost.kt` | **Completo** | `AppMessageHost` en la raíz de UI observa el canal de notificaciones sin competencia entre pantallas. |
| **Persistencia Room + DataStore** | `data/local/database/`, `data/local/datasource/` | **Completo** | Conserva la tabla SQLite `"tareas"` y la clave DataStore `"filtro_tareas_activo"`. |

---

## 📸 Capturas de Pantalla de la Aplicación

| Pantalla de Inicio (`HomeScreen`) | Pantalla de Lista (`TaskListScreen`) | Pantalla de Detalle (`TaskDetailScreen`) |
| :---: | :---: | :---: |
| ![Inicio](screenshots/inicio.png) | ![Lista](screenshots/lista.png) | ![Detalle](screenshots/detalle.png) |

---

## 🛠️ Tecnologías Utilizadas
* **Kotlin 2.x** (Corrutinas, Flow, Genéricos, Varianza, Reified, Scope Functions)
* **Hilt 2.51.1** (Inyección de dependencias)
* **Room Database 2.6.1** & **KSP**
* **DataStore Preferences 1.1.1**
* **Clean Architecture & MVVM** (`Route` / `Screen` / `ViewModel` / `UiState`)
* **Jetpack Compose & Material 3 (Teal Theme)**
* **Navigation Compose 2.9.8**
* **JUnit 4** (20 pruebas unitarias automatizadas)

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

## 📄 Documentación Adicional de Refactorización
Consulta el reporte detallado de refactorización en [`docs/REFACTOR_REPORT.md`](docs/REFACTOR_REPORT.md) y los requerimientos del proyecto en [`REQUIREMENTS.md`](REQUIREMENTS.md).

---

## 👤 Autor

**Josue Rafael Piña Fermin**  
Estudiante de UNICDA  
Asignatura: Desarrollo Android con Kotlin
