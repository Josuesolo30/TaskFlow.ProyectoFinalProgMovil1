package com.josue.taskflow.presentation.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.josue.taskflow.core.audio.TaskAudioPlayer
import com.josue.taskflow.domain.model.Tarea
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    tarea: Tarea?,
    onCambiarEstado: () -> Unit,
    onGuardarDescripcion: (String) -> Unit,
    onEliminarTarea: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val audioPlayer = remember(context) { TaskAudioPlayer(context) }
    var reproduciendoAudio by remember { mutableStateOf(false) }

    var mostrarConfirmacionEliminar by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(audioPlayer) {
        onDispose {
            audioPlayer.liberar()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Tarea") },
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (tarea == null) {
                Text("No se encontró la tarea seleccionada.")
                Button(onClick = onVolver) {
                    Text("Regresar a la lista")
                }
            } else {
                var descripcionEditada by rememberSaveable(tarea.id, tarea.descripcion) {
                    mutableStateOf(tarea.descripcion)
                }

                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = if (tarea.completada) "Estado: Completada" else "Estado: Pendiente",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge
                )

                OutlinedTextField(
                    value = descripcionEditada,
                    onValueChange = { descripcionEditada = it },
                    label = { Text("Descripción") },
                    minLines = 4,
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { onGuardarDescripcion(descripcionEditada) },
                    enabled = descripcionEditada.trim() != tarea.descripcion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar descripción")
                }

                Button(
                    onClick = onCambiarEstado,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (tarea.completada) "Marcar como pendiente" else "Marcar como completada")
                }

                OutlinedButton(
                    onClick = {
                        if (!reproduciendoAudio) {
                            reproduciendoAudio = true
                            scope.launch {
                                try {
                                    val textoALeer = "Tarea: ${tarea.titulo}. Descripción: ${tarea.descripcion.ifBlank { "Sin descripción" }}"
                                    audioPlayer.reproducirTexto(textoALeer)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Audio cancelado o con error", Toast.LENGTH_SHORT).show()
                                } finally {
                                    reproduciendoAudio = false
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (reproduciendoAudio) "Reproduciendo en voz alta..." else "Escuchar tarea (TTS)")
                }

                OutlinedButton(
                    onClick = {
                        val contenido = """
                            Tarea: ${tarea.titulo}
                            Descripción: ${tarea.descripcion}
                            Estado: ${if (tarea.completada) "Completada" else "Pendiente"}
                        """.trimIndent()

                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, tarea.titulo)
                            putExtra(Intent.EXTRA_TEXT, contenido)
                        }

                        context.startActivity(
                            Intent.createChooser(intent, "Compartir tarea con...")
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Compartir")
                }

                Button(
                    onClick = { mostrarConfirmacionEliminar = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar tarea")
                }
            }
        }

        if (mostrarConfirmacionEliminar) {
            AlertDialog(
                onDismissRequest = { mostrarConfirmacionEliminar = false },
                title = { Text("Eliminar Tarea") },
                text = { Text("¿Deseas eliminar permanentemente esta tarea?") },
                confirmButton = {
                    Button(
                        onClick = {
                            mostrarConfirmacionEliminar = false
                            onEliminarTarea()
                            onVolver()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarConfirmacionEliminar = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}
