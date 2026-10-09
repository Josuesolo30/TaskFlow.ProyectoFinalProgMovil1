package com.josue.taskflow.lab4

import com.josue.taskflow.dominio.model.Tarea
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Dos fuentes simuladas para demostrar el LAB 4.
 *
 * Las tareas y los filtros cambian en momentos diferentes.
 */
object FuentesTareasSimuladas {

    private fun tareasBase(): List<Tarea> {
        return listOf(
            Tarea(
                id = 1,
                titulo = "  Estudiar Kotlin  ",
                descripcion = "Repasar Flow",
                completada = false
            ),
            Tarea(
                id = 2,
                titulo = "Hacer ejercicio",
                descripcion = "Caminar",
                completada = true
            )
        )
    }

    // Primera fuente: listas de tareas.
    fun tareas(): Flow<List<Tarea>> = flow {

        delay(300)

        // A los 300 ms llegan dos tareas.
        emit(tareasBase())

        delay(900)

        // A los 1200 ms llega una lista inválida.
        // El operador filter debe descartarla.
        emit(
            listOf(
                tareasBase()[0],
                tareasBase()[0]
            )
        )

        delay(300)

        // A los 1500 ms ambas tareas pasan a completadas.
        val completadas = tareasBase().map { tarea ->
            tarea.copy(completada = true)
        }

        emit(completadas)

        delay(1200)

        // A los 2700 ms aparece una tarea nueva pendiente.
        emit(
            completadas + Tarea(
                id = 3,
                titulo = "Leer",
                descripcion = "Leer un capítulo",
                completada = false
            )
        )
    }

    // Segunda fuente: cambios del filtro.
    fun filtro(): Flow<FiltroTareas> = flow {

        emit(FiltroTareas.TODAS)

        delay(800)

        emit(FiltroTareas.PENDIENTES)

        delay(1200)

        emit(FiltroTareas.COMPLETADAS)

        delay(1200)

        emit(FiltroTareas.TODAS)
    }
}

