package com.josue.taskflow.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.josue.taskflow.dominio.model.Tarea

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    tarea: Tarea?,
    onCambiarEstado: () -> Unit,
    onVolver: () -> Unit,
    onGuardarDescripcion: (String) -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de tarea") },
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
                Text("No se encontró la tarea.")

                Button(onClick = onVolver) {
                    Text("Regresar")
                }
            } else {
                var descripcionEditada by rememberSaveable(
                    tarea.id,
                    tarea.descripcion
                ) {
                    mutableStateOf(tarea.descripcion)
                }

                Text(
                    text = tarea.titulo,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = if (tarea.completada) {
                        "Estado: Completada"
                    } else {
                        "Estado: Pendiente"
                    }
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
                    onClick = {
                        onGuardarDescripcion(descripcionEditada)
                    },
                    enabled = descripcionEditada.trim() != tarea.descripcion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar descripción")
                }

                Button(
                    onClick = onCambiarEstado,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (tarea.completada) {
                            "Marcar como pendiente"
                        } else {
                            "Marcar como completada"
                        }
                    )
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
                            Intent.createChooser(
                                intent,
                                "Compartir tarea con..."
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Compartir")
                }

                Text(
                    text = "Compartir utiliza la descripción guardada.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}