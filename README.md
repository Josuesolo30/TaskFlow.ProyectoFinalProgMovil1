# TaskFlow - Laboratorios y Proyecto Integrador

Aplicación Android para la gestión integral de tareas personales, desarrollada con **Kotlin**, **Jetpack Compose**, **Room Database**, **DataStore Preferences**, **Hilt** y **Material 3**. 

Todos los conceptos de los **Laboratorios 0, 1+2, 3, 4, 5 y 6** han sido estructurados de acuerdo con las buenas prácticas de **Clean Architecture** y las lecciones del curso.

---

## 🏗️ Arquitectura y Estructura del Proyecto

El proyecto está organizado en paquetes según la capa de responsabilidad:

```
com.josue.taskflow/
├── core/                         # Herramientas utilitarias reutilizables
│   ├── audio/TaskAudioPlayer.kt  # Adaptador suspendCancellableCoroutine para Android TextToSpeech
│   └── collection/ContenedorProcesable.kt # Colección genérica, varianza y type bounds
├── data/                         # Capa de Datos (Implementaciones y Fuentes)
│   ├── local/
│   │   ├── TareaDao.kt           # Consultas SQL para Room
│   │   ├── TareaEntity.kt        # Entidad de la tabla 'tareas'
│   │   ├── TaskFlowDatabase.kt   # Instancia SQLite Room
│   │   ├── TareaLocalDataSource.kt # Operaciones locales de Room
│   │   └── SettingsLocalDataSource.kt # Operaciones de DataStore Preferences
│   ├── DefaultTareaRepository.kt # Repositorio principal de tareas
│   └── DefaultSettingsRepository.kt # Repositorio de preferencias
├── di/                           # Módulos de Inyección de Dependencias (Hilt)
│   ├── DatabaseModule.kt         # Proveedor de Room Database y DAO
│   ├── DataStoreModule.kt        # Proveedor de DataStore Preferences
│   └── RepositoryModule.kt       # Binding de contratos de repositorios
├── domain/                       # Capa de Dominio (Modelos puros y Reglas de Negocio)
│   ├── model/Tarea.kt            # Modelo de dominio independiente de Android/Room
│   ├── repository/
│   │   ├── TareaRepository.kt    # Contrato del repositorio de tareas
│   │   └── SettingsRepository.kt # Contrato del repositorio de preferencias
│   └── usecase/                  # Casos de uso
│       ├── ObtenerTareasUseCase.kt
│       ├── AgregarTareaUseCase.kt
│       ├── CambiarEstadoTareaUseCase.kt
│       ├── ActualizarDescripcionUseCase.kt
│       ├── EliminarTareaUseCase.kt
│       ├── ObtenerEstadisticasUseCase.kt
│       ├── ObtenerFiltroGuardadoUseCase.kt
│       └── GuardarFiltroUseCase.kt
└── presentation/                 # Capa de Presentación (Compose UI y ViewModels)
    ├── components/TareaCard.kt   # Componentes visuales
    ├── navigation/AppNavigation.kt # Rutas y NavHost
    ├── screens/
    │   ├── InicioScreen.kt
    │   ├── ListaScreen.kt
    │   └── DetalleScreen.kt
    ├── state/
    │   ├── EstadoListaTaskFlow.kt # Estados de pantalla (Cargando, Contenido, Vacia, Error)
    │   └── TareaUiState.kt        # Estado UDF
    ├── theme/                    # Tema, Colores y Tipografía
    └── viewmodel/TareaViewModel.kt # ViewModel principal inyectado con @HiltViewModel
```

---

## 📊 Matriz de Cumplimiento y Estado de Prácticas

| Práctica | Archivo que la implementa | Estado | Evidencia o verificación pendiente |
| :--- | :--- | :--- | :--- |
| **Navegación e Intent Implícito (Lab 0)** | `presentation/navigation/AppNavigation.kt`, `presentation/screens/DetalleScreen.kt` | **Completo** | Probado con `NavHost`, argumentos dinámicos y chooser `ACTION_SEND` para compartir tareas. |
| **Genéricos y Funciones de Orden Superior (Lab 1+2)** | `core/collection/ContenedorProcesable.kt`, `domain/usecase/ObtenerEstadisticasUseCase.kt` | **Completo** | 12 pruebas unitarias de `ContenedorProcesable` pasadas al 100%. |
| **Corrutinas y Callbacks a Suspend (Lab 3)** | `core/audio/TaskAudioPlayer.kt`, `domain/usecase/ObtenerEstadisticasUseCase.kt` | **Completo** | `suspendCancellableCoroutine` sobre Android TextToSpeech y `async`/`await` concurrentes probados. |
| **Flow Reactivo y Operadores (Lab 4)** | `presentation/viewmodel/TareaViewModel.kt` | **Completo** | Combinación de Room y DataStore con `combine`, `map`, `retry` y `catch`. Pruebas al 100%. |
| **MVVM y Clean Architecture (Lab 5)** | Capas `presentation/`, `domain/`, `data/`, `di/`, `core/` | **Completo** | Inversión de dependencias estricta. El dominio no depende de Android ni Room. |
| **Persistencia Room Database (Lab 6)** | `data/local/TaskFlowDatabase.kt`, `data/local/TareaDao.kt`, `data/local/TareaLocalDataSource.kt` | **Completo** | CRUD completo (Crear, Leer, Editar, Eliminar) en Room SQLite verificado. |
| **Persistencia DataStore Preferences** | `data/local/SettingsLocalDataSource.kt`, `data/DefaultSettingsRepository.kt` | **Completo** | Guarda y restaura el filtro activo (`TODAS`, `PENDIENTES`, `COMPLETADAS`) automáticamente. |
| **Inyección de Dependencias Hilt** | `TaskFlowApplication.kt`, `di/DatabaseModule.kt`, `di/DataStoreModule.kt`, `di/RepositoryModule.kt` | **Completo** | `@HiltAndroidApp`, `@AndroidEntryPoint` y `@HiltViewModel` compilados y probados. |

---

## 📸 Evidencias y Capturas de Pantalla

### 📱 Flujo Principal de la Aplicación
| Pantalla de Inicio | Pantalla de Lista | Pantalla de Detalle |
| :---: | :---: | :---: |
| ![Inicio](screenshots/inicio.png) | ![Lista](screenshots/lista.png) | ![Detalle](screenshots/detalle.png) |

---

## 🛠️ Tecnologías Utilizadas
* **Kotlin 2.x** (Corrutinas, Flow, Genéricos, Varianza, Reified, Scope Functions)
* **Hilt 2.51.1** (Inyección de dependencias)
* **Room Database 2.6.1** & **KSP**
* **DataStore Preferences 1.1.1**
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
1. **Depuración de Entorno y Reorganización de Arquitectura**:
   * Refactorización de paquetes a Clean Architecture en inglés (`presentation`, `domain`, `data`, `di`, `core`).
   * Configuración de Hilt e inyección de dependencias con DataStore Preferences.
2. **Documentación y Organización del Entregable**:
   * Redacción, maquetación en Markdown y estructuración de las matrices de cumplimiento y prácticas.

### 🛡️ Declaración de Responsabilidad y Autoría
Todo el código generado o sugerido con apoyo de IA fue revisado, probado, comprendido y validado manualmente por el autor (**Josue Rafael Piña Fermin**).

---

## 👤 Autor

**Josue Rafael Piña Fermin**  
Estudiante de UNICDA  
Asignatura: Desarrollo Android con Kotlin
