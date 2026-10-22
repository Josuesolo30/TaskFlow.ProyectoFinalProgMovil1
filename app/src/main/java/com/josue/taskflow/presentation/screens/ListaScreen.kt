package com.josue.taskflow.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.josue.taskflow.presentation.components.TareaCard
import com.josue.taskflow.presentation.state.EstadoListaTaskFlow
import com.josue.taskflow.presentation.state.FiltroTareas

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    estadoLista: EstadoListaTaskFlow,
    filtroActivo: FiltroTareas,
    onSeleccionarFiltro: (FiltroTareas) -> Unit,
    onAgregarTarea: (String, String) -> Unit,
    onCambiarEstado: (Int) -> Unit,
    onEliminarTarea: (Int) -> Unit,
    onAbrirDetalle: (Int) -> Unit,
    onVolver: () -> Unit
) {
    var mostrarDialogoCrear by rememberSaveable { mutableStateOf(false) }
    var textoNuevaTarea by rememberSaveable { mutableStateOf("") }
    var descripcionNuevaTarea by rememberSaveable { mutableStateOf("") }

    var tareaAEliminar by rememberSaveable { mutableStateOf<Int?>(null) }

    val estadoScroll = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Tareas") },
                navigationIcon = {
                    TextButton(onClick = onVolver) {
                        Text("Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogoCrear = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar tarea"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FiltroTareas.entries.forEach { filtro ->
                    val seleccionado = filtro == filtroActivo
                    FilterChip(
                        selected = seleccionado,
                        onClick = { onSeleccionarFiltro(filtro) },
                        label = {
                            Text(
                                when (filtro) {
                                    FiltroTareas.TODAS -> "Todas"
                                    FiltroTareas.PENDIENTES -> "Pendientes"
                                    FiltroTareas.COMPLETADAS -> "Completadas"
                                }
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (estadoLista) {
                is EstadoListaTaskFlow.Cargando -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is EstadoListaTaskFlow.Vacia -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (estadoLista.filtro) {
                                FiltroTareas.TODAS -> "No tienes tareas guardadas. Presiona '+' para agregar una."
                                FiltroTareas.PENDIENTES -> "No hay tareas pendientes."
                                FiltroTareas.COMPLETADAS -> "No hay tareas completadas."
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                is EstadoListaTaskFlow.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Error: ${estadoLista.mensaje}",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                is EstadoListaTaskFlow.Contenido -> {
                    LazyColumn(
                        state = estadoScroll,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = estadoLista.tareas,
                            key = { tarea -> tarea.id }
                        ) { tarea ->
                            TareaCard(
                                tarea = tarea,
                                onEstadoChange = { onCambiarEstado(tarea.id) },
                                onClick = { onAbrirDetalle(tarea.id) },
                                onEliminar = { tareaAEliminar = tarea.id }
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        if (mostrarDialogoCrear) {
            AlertDialog(
                onDismissRequest = {
                    mostrarDialogoCrear = false
                    textoNuevaTarea = ""
                    descripcionNuevaTarea = ""
                },
                title = { Text("Nueva Tarea") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = textoNuevaTarea,
                            onValueChange = { textoNuevaTarea = it },
                            label = { Text("Título de la tarea") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = descripcionNuevaTarea,
                            onValueChange = { descripcionNuevaTarea = it },
                            label = { Text("Descripción") },
                            minLines = 3,
                            maxLines = 5,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (textoNuevaTarea.isNotBlank()) {
                                onAgregarTarea(textoNuevaTarea, descripcionNuevaTarea)
                                textoNuevaTarea = ""
                                descripcionNuevaTarea = ""
                                mostrarDialogoCrear = false
                            }
                        },
                        enabled = textoNuevaTarea.isNotBlank()
                    ) {
                        Text("Agregar")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            mostrarDialogoCrear = false
                            textoNuevaTarea = ""
                            descripcionNuevaTarea = ""
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }

        tareaAEliminar?.let { id ->
            AlertDialog(
                onDismissRequest = { tareaAEliminar = null },
                title = { Text("Eliminar Tarea") },
                text = { Text("¿Estás seguro de que deseas eliminar esta tarea?") },
                confirmButton = {
                    Button(
                        onClick = {
                            onEliminarTarea(id)
                            tareaAEliminar = null
                        }
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { tareaAEliminar = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
