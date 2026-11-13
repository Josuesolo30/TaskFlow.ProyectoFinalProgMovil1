# Reporte de Corrección del Laboratorio 3 — TaskFlow

## 1. Archivos Creados y Modificados

- `domain/usecase/task/TaskUseCase.kt`:
  - Implementado `supervisorScope` en `calculateStatistics()` para aislar el cálculo de conteos principales respecto al resumen secundario de tareas.
  - Si el resumen falla, los conteos se calculan y entregan sin bloquear la aplicación.
  - Manejo explícito que relanza `CancellationException`.
- `core/audio/TaskAudioPlayer.kt`:
  - Relanza `CancellationException`.
  - Registra inicio, cancelación, liberación y errores en Logcat bajo la etiqueta `"TaskAudioPlayer"` **omitiendo títulos y descripciones de las tareas por privacidad**.
- `ui/screen/taskdetail/view/TaskDetailScreen.kt`:
  - `DisposableEffect` ejecuta `audioPlayer.release()` al salir de la pantalla para cancelar la lectura y liberar los recursos del motor de voz TTS.

---

## 2. Explicación para Presentar al Profesor

> *"Profesor, en el Laboratorio 3 aplicamos supervisión de corrutinas y la gestión limpia del reproductor de voz:*
> 
> 1. ***Supervisión en Caso de Uso (`supervisorScope`)***:
>    - *En `TaskUseCase.calculateStatistics()`, la generación del resumen secundario corre aislada de la métrica principal.*
>    - *Un fallo secundario no cancela el cálculo de conteos de tareas.*
> 2. ***Cancelación y Privacidad en Logcat***:
>    - *En `TaskAudioPlayer`, los eventos de inicio, cancelación, liberación y error se registran con la etiqueta `TaskAudioPlayer` sin exponer el contenido de las tareas.*
>    - *`CancellationException` se relanza para respetar la cancelación estructurada.*
> 3. ***Liberación en Detalle***:
>    - *Al salir de `TaskDetailScreen`, `audioPlayer.release()` detiene el TTS y libera el motor de voz."*
