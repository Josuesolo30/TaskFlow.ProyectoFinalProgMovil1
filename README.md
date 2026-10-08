# TaskFlow - Laboratorios y Proyecto Integrador

Aplicación Android para la gestión de tareas personales, desarrollada con Kotlin, Jetpack Compose y Material 3. Integra las competencias trabajadas en los **Laboratorios 0, 1+2 y 3** del curso.

---

## 📌 Contenido de los Laboratorios Implementados

### 🟢 Lab 0: Nivelación - Scaffold, Navigation e Intent
* **Estructura con Scaffold**: Uso de `topBar`, `floatingActionButton` (para agregar tareas mediante un `AlertDialog`) y `content` adaptado a `PaddingValues`.
* **Navegación Robusta**: Implementación de `NavHost` + `NavController`, pasando argumentos dinámicos entre pantallas (`itemId`), usando `navigate()` y `popBackStack()`.
* **Intent Implícito y Selector**: Botón **"Compartir"** en la pantalla de detalle que dispara un `Intent` implícito (`ACTION_SEND`) con `Intent.createChooser(...)`.
* **Documentación en código**: Comentario pedagógico explicando la diferencia conceptual entre un **Intent Explícito** (ejecución interna de clases) y un **Intent Implícito** (delegación de acciones genéricas al sistema).

---

### 🔵 Lab 1+2: Librería Genérica con Estilo Funcional
* **Librería Genérica (`ContenedorProcesable<T>`)**: Ubicada en `com.josue.taskflow.util`.
* **Varianza**: Firma de métodos con proyecciones de tipo `Array<out T>` (covariante) y `MutableList<in T>` (contravariante).
* **Type Bounds**: Funciones de comparación y ordenamiento (`obtenerMaximo()`, `obtenerMinimo()`, `ordenar()`) restringidas con `where T : Comparable<T>`.
* **Filtrado Inline Reified**: `inline fun <reified R : T> filtrarPorTipo(): List<R>` para inspección de tipos en tiempo de ejecución sin pérdida de tipos (*type erasure*).
* **Operadores Funcionales**: Uso de `map` (`transformar`), `filter` (`filtrar`), `fold` (`acumular`) y `reduce` (`reducir`).
* **Scope Functions**: Aplicación fundamentada de `let`, `run`, `also`, `apply` y `with`, con comentarios explicativos en el código.
* **Pruebas Unitarias**: Suite de 12 pruebas unitarias automatizadas (`ContenedorProcesableTest.kt`) pasando al 100%.

---

### 🟠 Lab 3: Concurrencia y Corrutinas
* **Migración de Callback a Suspend**: Adaptación de APIs asíncronas heredadas basadas en callbacks a funciones suspendidas utilizando `suspendCancellableCoroutine`.
* **Módulo de Sincronización (`MatchSyncService`)**: Gestión de tareas en segundo plano usando `Dispatchers.Main.immediate` y control de ciclo de vida con `Job`.
* **Cancelación Cooperativa**: Implementación de sincronización periódica (*polling*) que responde de forma inmediata a `cancelAndJoin()`.
* **Evidencia en Logs**: Verificación en Logcat (`LAB3`) de que la cancelación se ejecuta limpiamente y los recursos en E/S finalizan correctamente (`activeCalls=0`).

---

## 📸 Evidencias y Capturas de Pantalla

### Capturas del Flujo de Interfaz (Lab 0)

| Pantalla de Inicio | Pantalla de Lista | Pantalla de Detalle |
| :---: | :---: | :---: |
| ![Inicio](screenshots/inicio.png) | ![Lista](screenshots/lista.png) | ![Detalle](screenshots/detalle.png) |

### Evidencia en Logs de Cancelación en Corrutinas (Lab 3)

Muestra del log en Android Studio confirmando la invocación de `cancelAndJoin()`, la cancelación del polling (`CANCELLED T-TIGRES`) y la liberación completa de recursos (`activeCalls=0`):

![Evidencia Cancelación Lab 3](screenshots/lab3_cancellation.png)

---

## 🛠️ Tecnologías Utilizadas

* **Kotlin 2.x** (Genéricos, Varianza, Reified, Scope Functions, Corrutinas)
* **Jetpack Compose** & **Material 3**
* **Navigation Compose 2.9.8**
* **KotlinX Coroutines** (`suspendCancellableCoroutine`, `cancelAndJoin`)
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
