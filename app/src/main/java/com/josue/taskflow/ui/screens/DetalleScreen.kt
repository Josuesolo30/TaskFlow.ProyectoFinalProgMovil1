package com.josue.taskflow.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.josue.taskflow.data.Tarea

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleScreen(
    tarea: Tarea?,
    onCambiarEstado: () -> Unit,
    onVolver: () -> Unit,
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
                .padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            if (tarea == null) {
                Text(
                    text = "No se encontró la tarea.",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = { onVolver() }) {
                    Text("Regresar")
                }
            } else {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = tarea.titulo,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = tarea.descripcion,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = if (tarea.completada) {
                                "Estado: Completada"
                            } else {
                                "Estado: Pendiente"
                            },
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

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

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        /*
                         * EXPLICACIÓN: DIFERENCIA ENTRE INTENT IMPLÍCITO Y EXPLÍCITO
                         * ---------------------------------------------------------
                         * - INTENT EXPLÍCITO: Especifica exactamente la clase o componente de destino
                         *   que debe ejecutarse dentro de la propia aplicación (o paquete específico).
                         *   Ejemplo: Intent(context, MainActivity::class.java). Se usa principalmente
                         *   para la navegación interna entre actividades.
                         * 
                         * - INTENT IMPLÍCITO: No especifica una clase de destino concreta. En su lugar,
                         *   declara una acción genérica que se desea realizar (por ejemplo, ACTION_SEND
                         *   para compartir información o ACTION_VIEW para abrir una URL). El sistema operativo
                         *   Android analiza las aplicaciones capaces de responder a esa acción y le permite
                         *   al usuario elegir mediante el selector de aplicaciones (Intent.createChooser).
                         */
                        val contenidoACompartir = "Tarea: ${tarea.titulo}\nDescripción: ${tarea.descripcion}\nEstado: ${if (tarea.completada) "Completada" else "Pendiente"}"

                        val intentCompartir = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, tarea.titulo)
                            putExtra(Intent.EXTRA_TEXT, contenidoACompartir)
                        }

                        val chooser = Intent.createChooser(intentCompartir, "Compartir tarea con...")
                        context.startActivity(chooser)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartir",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Compartir")
                }
            }
        }
    }
}
