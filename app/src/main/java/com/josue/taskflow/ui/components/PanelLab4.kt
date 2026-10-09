package com.josue.taskflow.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.josue.taskflow.lab4.EstadoListaLab4
import com.josue.taskflow.lab4.FiltroTareas

/**
 * Muestra el estado que recibe del ViewModel.
 * Los botones envían eventos hacia arriba.
 */
@Composable
fun PanelLab4(
    estado: EstadoListaLab4,
    iniciarDemo: () -> Unit,
    seleccionarFiltro: (FiltroTareas) -> Unit,
    falloTemporal: () -> Unit,
    errorPersistente: () -> Unit,
    recargar: () -> Unit,
    onCambiarEstadoSimulado: (Int) -> Unit,
    onAbrirDetalleSimulado: (Int) -> Unit
) {

    Column {

        Text("LAB 4: tareas de ejemplo")

        Button(
            onClick = iniciarDemo
        ) {
            Text("Iniciar demo")
        }

        Row {

            FiltroTareas.entries.forEach { filtro ->

                TextButton(
                    onClick = {
                        seleccionarFiltro(filtro)
                    }
                ) {

                    Text(
                        when (filtro) {

                            FiltroTareas.TODAS ->
                                "Todas"

                            FiltroTareas.PENDIENTES ->
                                "Pendientes"

                            FiltroTareas.COMPLETADAS ->
                                "Hechas"
                        }
                    )
                }
            }
        }

        when (estado) {

            EstadoListaLab4.Cargando -> {
                Text("Cargando...")
            }

            is EstadoListaLab4.Contenido -> {

                Text("Filtro: ${estado.filtro}")

                estado.tareas.forEach { tarea ->

                    TareaCard(
                        tarea = tarea,
                        onEstadoChange = {
                            onCambiarEstadoSimulado(tarea.id)
                        },
                        onClick = {
                            onAbrirDetalleSimulado(tarea.id)
                        }
                    )
                }
            }

            is EstadoListaLab4.Vacia -> {
                Text("Sin tareas para ${estado.filtro}")
            }

            is EstadoListaLab4.Error -> {
                Text("Error: ${estado.mensaje}")
            }
        }

        Row {

            TextButton(
                onClick = falloTemporal
            ) {
                Text("Fallo temporal")
            }

            TextButton(
                onClick = errorPersistente
            ) {
                Text("Error")
            }

            TextButton(
                onClick = recargar
            ) {
                Text("Recargar")
            }
        }

        Text("Mis tareas guardadas en Room:")
    }
}
