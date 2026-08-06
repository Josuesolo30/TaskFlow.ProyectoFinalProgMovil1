package com.josue.taskflow.data

/**
 * Fuente local de datos de ejemplo.
 * No es una base de datos; solo permite iniciar la app con contenido.
 */
object TareaRepository {
    val tareasIniciales = listOf(
        Tarea(
            id = 1,
            titulo = "Estudiar navegación",
            descripcion = "Repasar NavController, NavHost y las rutas de cada pantalla."
        ),
        Tarea(
            id = 2,
            titulo = "Preparar el README",
            descripcion = "Documentar funcionalidades, tecnologías y forma de ejecutar la aplicación.",
            completada = true
        ),
        Tarea(
            id = 3,
            titulo = "Probar en el AVD",
            descripcion = "Ejecutar las tres pantallas y verificar todos los botones."
        )
    )
}
