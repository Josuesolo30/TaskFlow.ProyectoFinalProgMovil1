# Reporte de Refactorización y Arquitectura — TaskFlow

## 1. Resumen de Cambios y Justificación

Se realizó la refactorización completa del proyecto **TaskFlow** para adoptar las directrices de arquitectura de Android y los requerimientos del profesor:

1. **Unificación de la Capa de Interfaz bajo `ui/`**:
   - Todas las vistas, pantallas Compose, diálogos, componentes y configuraciones de tema se ubicaron dentro del paquete `com.josue.taskflow.ui/`.
2. **Separación "Una Pantalla, un ViewModel"**:
   - Se dividió el antiguo `TareaViewModel` compartido en tres ViewModels especializados: `HomeViewModel`, `TaskListViewModel` y `TaskDetailViewModel`, cada uno vinculado a su pantalla vía Hilt (`@HiltViewModel`).
   - Se implementó el patrón `Route/Screen` (`HomeRoute`/`HomeScreen`, `TaskListRoute`/`TaskListScreen`, `TaskDetailRoute`/`TaskDetailScreen`) para separar la recolección de estado y llamadas de navegación de la representación visual.
3. **Consolidación en `TaskUseCase`**:
   - Se agruparon las ocho clases pequeñas de casos de uso en una única clase de dominio `TaskUseCase` inyectada con `TaskRepository` y `SettingsRepository`.
4. **Normalización y Traducción a Inglés**:
   - Se renombraron todos los paquetes, archivos, clases, métodos y propiedades a inglés, manteniendo intactos los nombres SQL persistidos (`"tareas"`, `"titulo"`, `"descripcion"`, `"completada"`) y los textos visibles para el usuario en español vía `strings.xml`.
5. **Nueva Paleta Material 3 Teal**:
   - Se aplicó una paleta basada en tonos verde azulado (*Teal*) con soporte completo para Tema Claro y Tema Oscuro, garantizando legibilidad y contrastes superiores a 4.5:1.
6. **Receptor Único de Confirmaciones**:
   - Se colocó `AppMessageHost` en la raíz de la UI (`MainActivity`) para consumir el flujo de notificaciones del repositorio a través de `TaskUseCase.observeNotifications()`, evitando receptores duplicados o competencia entre pantallas.

---

## 2. Mapeo de Nombres y Rutas (Anterior ➔ Nuevo)

| Nombre / Ruta Anterior | Nombre / Ruta Nueva | Descripción del Cambio |
| :--- | :--- | :--- |
| `TaskFlowApplication.kt` | `TaskFlowApplication.kt` | Mantenido con `@HiltAndroidApp`. |
| `MainActivity.kt` | `MainActivity.kt` | Mantenido con `@AndroidEntryPoint` e inyección de `TaskUseCase`. |
| `ui/navigation/AppNavigation.kt` | `ui/navigation/AppNavHost.kt` | Reorganizado como NavHost principal. |
| `Pantalla` | `ui/navigation/AppDestination.kt` | Destinos de navegación en inglés. |
| *(Nuevo)* | `ui/message/AppMessageHost.kt` | Consumidor único a nivel de raíz para mensajes Toast. |
| `ui/components/TareaCard.kt` | `ui/components/TaskCard.kt` | Componente visual en inglés con tamaño táctil >= 48dp. |
| `ui/theme/Color.kt` | `ui/theme/ColorPalette.kt` | Nueva paleta Teal Material 3. |
| `ui/theme/Theme.kt` | `ui/theme/TaskFlowTheme.kt` | Esquemas lightColorScheme y darkColorScheme. |
| `ui/theme/Type.kt` | `ui/theme/Typography.kt` | Definición de tipografía Material 3. |
| `ui/screens/InicioScreen.kt` | `ui/screen/home/view/HomeScreen.kt` & `HomeRoute.kt` | Pantalla y Ruta de Inicio. |
| *(Parte de TareaViewModel)* | `ui/screen/home/viewmodel/HomeViewModel.kt` | ViewModel especializado para la pantalla de inicio. |
| *(Parte de TareaUiState)* | `ui/screen/home/model/HomeUiState.kt` | Estado de interfaz para Inicio. |
| `ui/screens/ListaScreen.kt` | `ui/screen/tasklist/view/TaskListScreen.kt` & `TaskListRoute.kt` | Pantalla y Ruta de Lista. |
| *(Parte de TareaViewModel)* | `ui/screen/tasklist/viewmodel/TaskListViewModel.kt` | ViewModel especializado para la lista de tareas. |
| `presentacion/state/EstadoListaTaskFlow.kt` | `ui/screen/tasklist/model/TaskListUiState.kt` | Estados `Loading`, `Content`, `Empty`, `Error`. |
| `ui/screens/DetalleScreen.kt` | `ui/screen/taskdetail/view/TaskDetailScreen.kt` & `TaskDetailRoute.kt` | Pantalla y Ruta de Detalle. |
| *(Parte de TareaViewModel)* | `ui/screen/taskdetail/viewmodel/TaskDetailViewModel.kt` | ViewModel especializado para Detalle con `SavedStateHandle`. |
| *(Nuevo)* | `ui/screen/taskdetail/model/TaskDetailUiState.kt` | Estados `Loading`, `Content`, `NotFound`, `Error`. |
| `dominio/model/Tarea.kt` | `domain/model/task/Task.kt` | Modelo puro de dominio. |
| *(Nuevo)* | `domain/model/task/TaskFilter.kt` | Enum de filtro con soporte de decodificación de claves antiguas/nuevas. |
| `dominio/usecase/ObtenerEstadisticasUseCase.kt` | `domain/model/statistics/TaskStatistics.kt` | Modelo puro de estadísticas fuera del archivo de casos de uso. |
| `dominio/repository/TareaRepository.kt` | `domain/repository/task/TaskRepository.kt` | Contrato de datos de tareas. |
| `domain/repository/SettingsRepository.kt` | `domain/repository/settings/SettingsRepository.kt` | Contrato de preferencias DataStore. |
| 8 clases de Casos de Uso independientes | `domain/usecase/task/TaskUseCase.kt` | Caso de uso unificado con todas las operaciones de tareas. |
| `datos/db/TareaEntity.kt` | `data/local/database/entity/task/TaskEntity.kt` | Entidad Room con `@Entity(tableName = "tareas")`. |
| `datos/db/TareaDao.kt` | `data/local/database/dao/TaskDao.kt` | DAO con consultas SQL reales. |
| `datos/db/TaskFlowDatabase.kt` | `data/local/database/TaskFlowDatabase.kt` | Base de datos SQLite Room. |
| `datos/repository/TareaRepositoryImpl.kt` | `data/repository/task/DefaultTaskRepository.kt` | Repositorio predeterminado de tareas. |
| `data/DefaultSettingsRepository.kt` | `data/repository/settings/DefaultSettingsRepository.kt` | Repositorio predeterminado de preferencias. |
| `datos/repository/TareaRepositoryMemoria.kt` | `app/src/test/.../data/FakeTaskRepository.kt` | Movido a la fuente de pruebas. |
| *(Nuevo)* | `data/mapper/task/TaskMapper.kt` | Mappers de conversión `toDomain()` y `toEntity()`. |
| `util/ContenedorProcesable.kt` | `core/collection/ProcessableContainer.kt` | Colección genérica en inglés. |
| `util/TaskAudioPlayer.kt` | `core/audio/TaskAudioPlayer.kt` | Adaptador TTS en `core/audio`. |

---

## 3. Mapa de Capas y Pantallas

```
Capa Presentation (UI)
┌───────────────────────┐   ┌───────────────────────┐   ┌───────────────────────┐
│       HomeRoute       │   │     TaskListRoute     │   │    TaskDetailRoute    │
│      HomeScreen       │   │    TaskListScreen     │   │   TaskDetailScreen    │
└───────────┬───────────┘   └───────────┬───────────┘   └───────────┬───────────┘
            │                           │                           │
            ▼                           ▼                           ▼
┌───────────────────────┐   ┌───────────────────────┐   ┌───────────────────────┐
│     HomeViewModel     │   │   TaskListViewModel   │   │  TaskDetailViewModel  │
└───────────┬───────────┘   └───────────┬───────────┘   └───────────┬───────────┘
            │                           │                           │
            └───────────────────┬───────┴───────────────────────────┘
                                ▼
Capa Domain
┌───────────────────────────────────────────────────────────────────────────────┐
│                                 TaskUseCase                                   │
│  - observeTasks() / observeTask(id) / observeStatistics() / observeFilter()   │
│  - saveFilter() / addTask() / toggleCompletion() / updateDescription() / del  │
└───────────────────────────────┬───────────────────────────────┬───────────────┘
                                │                               │
                                ▼                               ▼
┌───────────────────────────────────────────────┐ ┌─────────────────────────────┐
│          TaskRepository (Contract)            │ │ SettingsRepository (Contract│
└───────────────────────┬───────────────────────┘ └──────────────┬──────────────┘
                        │                                        │
Capa Data               ▼                                        ▼
┌───────────────────────────────────────────────┐ ┌─────────────────────────────┐
│             DefaultTaskRepository             │ │  DefaultSettingsRepository  │
└───────────────────────┬───────────────────────┘ └──────────────┬──────────────┘
                        │                                        │
                        ▼                                        ▼
┌───────────────────────────────────────────────┐ ┌─────────────────────────────┐
│              TaskLocalDataSource              │ │   SettingsLocalDataSource   │
│                   (Room)                      │ │    (DataStore Preferences)  │
└───────────────────────────────────────────────┘ └─────────────────────────────┘
```

---

## 4. Responsabilidades de `TaskUseCase`

`TaskUseCase` agrupa de forma limpia todas las reglas de negocio de las tareas:
* `observeTasks(): Flow<List<Task>>`: Emisión continua de la lista de tareas desde Room.
* `observeTask(taskId: Int): Flow<Task?>`: Emisión reactiva del detalle de una tarea específica.
* `observeStatistics(): Flow<TaskStatistics>`: Emisión procesada de estadísticas (totales, pendientes, completadas, porcentaje, tarea máx/mín ID y resumen acumulado) utilizando `ProcessableContainer` y corrutinas concurrentes en `Dispatchers.Default`.
* `observeFilter(): Flow<TaskFilter>`: Emisión continua del filtro activo guardado en DataStore.
* `saveFilter(filter: TaskFilter)`: Guarda la preferencia de filtro.
* `addTask(title: String, description: String)`: Valida título no vacío y guarda la tarea.
* `toggleCompletion(taskId: Int)`: Cambia el estado completada/pendiente.
* `updateDescription(taskId: Int, description: String)`: Actualiza la descripción.
* `deleteTask(taskId: Int)`: Elimina la tarea.
* `observeNotifications(): Flow<String>`: Canal de eventos de confirmación para ser consumido por `AppMessageHost`.

---

## 5. Medición de Contraste de la Paleta Teal Material 3

Se aplicó la paleta verde azulado (*Teal*) garantizando el cumplimiento de las guías W3C / WCAG 2.2 de contraste mínimo (al menos 4.5:1 para texto normal):

* **Tema Claro**:
  * `onPrimary` (`#FFFFFF`) sobre `primary` (`#006A60`): **Contraste 6.50:1** (Pasa AAA).
  * `onSurface` (`#171D1A`) sobre `surface` (`#FFFFFF`): **Contraste 17.11:1** (Pasa AAA).
  * `onSurfaceVariant` (`#3F4944`) sobre `surfaceVariant` (`#DBE5DF`): **Contraste 7.24:1** (Pasa AAA).
* **Tema Oscuro**:
  * `onPrimary` (`#00382F`) sobre `primary` (`#7ED7C8`): **Contraste 7.74:1** (Pasa AAA).
  * `onSurface` (`#DEE4DF`) sobre `surface` (`#151C18`): **Contraste 13.43:1** (Pasa AAA).
  * `onSurfaceVariant` (`#BFC9C2`) sobre `surfaceVariant` (`#3F4944`): **Contraste 5.49:1** (Pasa AA).

---

## 6. Compatibilidad y Conservación de Datos

* **Room Database**: Se conservó la tabla `"tareas"`, la clave primaria `"id"`, las columnas `"titulo"`, `"descripcion"`, `"completada"` y el nombre del archivo SQLite `"taskflow_database"`.
* **DataStore Preferences**: Se mantuvo la clave `"filtro_tareas_activo"`. `TaskFilter.fromStorageKey()` decodifica tanto los valores legados (`"TODAS"`, `"PENDIENTES"`, `"COMPLETADAS"`) como los nuevos (`"all"`, `"pending"`, `"completed"`), evitando resetear las preferencias del usuario.

---

## 7. Resultados de Compilación y Pruebas

* **Compilación Gradle (`:app:assembleDebug`)**: **BUILD SUCCESSFUL**.
* **Pruebas Unitarias (`:app:testDebugUnitTest`)**: **20 pruebas ejecutadas y pasadas al 100%**:
  * `ProcessableContainerTest`: 12 pruebas del contenedor genérico.
  * `TaskUseCaseTest`: 4 pruebas de negocio y estadísticas.
  * `DefaultTaskRepositoryTest`: 3 pruebas de operaciones de repositorio y notificaciones.
  * `TaskFilterTest`: 1 prueba de compatibilidad de almacenamiento.
