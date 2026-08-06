package com.josue.taskflow.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.josue.taskflow.data.Tarea
import com.josue.taskflow.ui.components.TareaCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaScreen(
    tareas: List<Tarea>,
    onAgregarTarea: (String) -> Unit,
    onCambiarEstado: (Int) -> Unit,
    onAbrirDetalle: (Int) -> Unit,
    onVolver: () -> Unit
) {
    var nuevaTarea by rememberSaveable { mutableStateOf("") }
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = nuevaTarea,
                onValueChange = { nuevaTarea = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nueva tarea") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onAgregarTarea(nuevaTarea)
                    nuevaTarea = ""
                },
                enabled = nuevaTarea.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar tarea")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (tareas.isEmpty()) {
                Text(
                    text = "Todavía no tienes tareas.",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                // LazyColumn crea una lista vertical desplazable.
                LazyColumn(
                    state = estadoLista,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
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
        }
    }
}
