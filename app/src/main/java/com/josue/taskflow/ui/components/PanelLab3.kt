package com.josue.taskflow.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import edu.unicda.lab3.SimulatedIo
import edu.unicda.lab3.coroutines.MatchSyncService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

@Composable
fun PanelLab3() {
    val scope = rememberCoroutineScope()

    val service = remember(scope) {
        MatchSyncService(
            dispatcher = Dispatchers.Main.immediate,
            parentJob = scope.coroutineContext[Job],
            logger = { tag, message ->
                Log.d("LAB3", "$tag: $message")
            }
        )
    }

    var pollingJob by remember {
        mutableStateOf<Job?>(null)
    }

    var cancelling by remember {
        mutableStateOf(false)
    }

    // Cierra el servicio cuando este panel sale de la composición.
    DisposableEffect(service) {
        onDispose {
            service.close()
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            enabled = !cancelling,
            onClick = {
                if (pollingJob?.isActive != true) {
                    pollingJob = service.startPolling(
                        username = "anyelo",
                        teamId = "T-TIGRES",
                        polls = 50,
                        intervalMs = 400
                    ) { stats ->
                        Log.d(
                            "LAB3",
                            "UPDATE snapshot=${stats.snapshot} " +
                                    "points=${stats.points}"
                        )
                    }
                }
            }
        ) {
            Text("Iniciar LAB 3")
        }

        Button(
            enabled = !cancelling,
            onClick = {
                val current = pollingJob

                if (current != null) {
                    cancelling = true

                    scope.launch {
                        try {
                            Log.d(
                                "LAB3",
                                ">>> cancelAndJoin() invocado"
                            )

                            current.cancelAndJoin()

                            val idle = withTimeoutOrNull(3_000) {
                                while (
                                    SimulatedIo.activeCalls.get() != 0
                                ) {
                                    delay(10)
                                }

                                true
                            } ?: false

                            Log.d(
                                "LAB3",
                                "cancelAndJoin termino; " +
                                        "activeCalls=" +
                                        SimulatedIo.activeCalls.get()
                            )

                            if (!idle) {
                                Log.w(
                                    "LAB3",
                                    "El I/O no quedo inactivo " +
                                            "dentro del limite"
                                )
                            }
                        } finally {
                            cancelling = false
                        }
                    }
                }
            }
        ) {
            Text("Cancelar")
        }
    }
}
