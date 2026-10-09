package com.josue.taskflow.ui.screens

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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.josue.taskflow.dominio.model.Tarea
import com.josue.taskflow.lab4.EstadoListaLab4
import com.josue.taskflow.lab4.FiltroTareas
import com.josue.taskflow.ui.components.PanelLab4
import com.josue.taskflow.ui.components.TareaCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    tareas: List<Tarea>,
    estadoLab4: EstadoListaLab4,
    onIniciarDemoLab4: () -> Unit,
    onFiltroLab4: (FiltroTareas) -> Unit,
    onFalloLab4: () -> Unit,
    onErrorLab4: () -> Unit,
    onRecargarLab4: () -> Unit,
    onCambiarEstadoSimuladoLab4: (Int) -> Unit,
    onAbrirDetalleSimuladoLab4: (Int) -> Unit,
    onAgregarTarea: (String) -> Unit,
    onCambiarEstado: (Int) -> Unit,
    onAbrirDetalle: (Int) -> Unit,
    onVolver: () -> Unit,
) {
    var mostrarDialogo by rememberSaveable { mutableStateOf(false) }
    var textoNuevaTarea by rememberSaveable { mutableStateOf("") }
    val estadoLista = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis tareas") },
                navigationIcon = {
                    TextButton(onClick = onVolver) {
                        Text("Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogo = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar tarea"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = estadoLista,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            item {
                PanelLab4(
                    estado = estadoLab4,
                    iniciarDemo = onIniciarDemoLab4,
                    seleccionarFiltro = onFiltroLab4,
                    falloTemporal = onFalloLab4,
                    errorPersistente = onErrorLab4,
                    recargar = onRecargarLab4,
                    onCambiarEstadoSimulado = onCambiarEstadoSimuladoLab4,
                    onAbrirDetalleSimulado = onAbrirDetalleSimuladoLab4
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            if (tareas.isEmpty()) {
                item {
                    Text(
                        text = "Todavía no tienes tareas.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                items(
                    items = tareas,
                    key = { tarea -> tarea.id }
                ) { tarea ->
                    TareaCard(
                        tarea = tarea,
                        onEstadoChange = {
                            onCambiarEstado(tarea.id)
                        },
                        onClick = {
                            onAbrirDetalle(tarea.id)
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }

        if (mostrarDialogo) {
            AlertDialog(
                onDismissRequest = {
                    mostrarDialogo = false
                    textoNuevaTarea = ""
                },
                title = { Text("Nueva tarea") },
                text = {
                    OutlinedTextField(
                        value = textoNuevaTarea,
                        onValueChange = { textoNuevaTarea = it },
                        label = { Text("Título de la tarea") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (textoNuevaTarea.isNotBlank()) {
                                onAgregarTarea(textoNuevaTarea)
                                textoNuevaTarea = ""
                                mostrarDialogo = false
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
                            mostrarDialogo = false
                            textoNuevaTarea = ""
                        }
                    ) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
