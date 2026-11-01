package com.josue.taskflow.ui.screen.taskdetail.view

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.josue.taskflow.R
import com.josue.taskflow.core.audio.TaskAudioPlayer
import com.josue.taskflow.ui.screen.taskdetail.model.TaskDetailUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    uiState: TaskDetailUiState,
    onToggleCompletion: () -> Unit,
    onSaveDescription: (String) -> Unit,
    onDeleteTask: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val audioPlayer = remember(context) { TaskAudioPlayer(context) }
    var isPlayingAudio by remember { mutableStateOf(false) }

    var showDeleteConfirmation by rememberSaveable { mutableStateOf(false) }

    DisposableEffect(audioPlayer) {
        onDispose {
            audioPlayer.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.task_detail_title)) },
                navigationIcon = {
                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    ) {
                        Text(stringResource(R.string.back))
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState) {
                is TaskDetailUiState.Loading, TaskDetailUiState.DeletionSuccess -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is TaskDetailUiState.NotFound -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.task_not_found),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(
                            onClick = onBack,
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .sizeIn(minHeight = 48.dp)
                        ) {
                            Text(stringResource(R.string.return_to_list))
                        }
                    }
                }

                is TaskDetailUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.error_prefix, uiState.message),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                is TaskDetailUiState.Content -> {
                    val task = uiState.task
                    val isSaving = uiState.isSaving
                    val isDeleting = uiState.isDeleting

                    var editedDescription by rememberSaveable(task.id, task.description) {
                        mutableStateOf(task.description)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (uiState.operationError != null) {
                            Text(
                                text = uiState.operationError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.headlineSmall
                        )

                        Text(
                            text = if (task.isCompleted) {
                                stringResource(R.string.status_completed)
                            } else {
                                stringResource(R.string.status_pending)
                            },
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        OutlinedTextField(
                            value = editedDescription,
                            onValueChange = { editedDescription = it },
                            label = { Text(stringResource(R.string.task_description_label)) },
                            minLines = 4,
                            maxLines = 8,
                            enabled = !isSaving && !isDeleting,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { onSaveDescription(editedDescription) },
                            enabled = editedDescription.trim() != task.description && !isSaving && !isDeleting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .sizeIn(minHeight = 48.dp)
                        ) {
                            Text(stringResource(R.string.save_description))
                        }

                        Button(
                            onClick = onToggleCompletion,
                            enabled = !isSaving && !isDeleting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .sizeIn(minHeight = 48.dp)
                        ) {
                            Text(
                                if (task.isCompleted) {
                                    stringResource(R.string.mark_as_pending)
                                } else {
                                    stringResource(R.string.mark_as_completed)
                                }
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                if (!isPlayingAudio) {
                                    isPlayingAudio = true
                                    scope.launch {
                                        try {
                                            val textToRead = "Tarea: ${task.title}. Descripción: ${task.description.ifBlank { "Sin descripción" }}"
                                            audioPlayer.playText(textToRead)
                                        } catch (e: CancellationException) {
                                            throw e
                                        } catch (e: Exception) {
                                            Toast.makeText(context, context.getString(R.string.audio_error), Toast.LENGTH_SHORT).show()
                                        } finally {
                                            isPlayingAudio = false
                                        }
                                    }
                                }
                            },
                            enabled = !isSaving && !isDeleting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .sizeIn(minHeight = 48.dp)
                        ) {
                            Text(
                                if (isPlayingAudio) {
                                    stringResource(R.string.playing_audio_tts)
                                } else {
                                    stringResource(R.string.listen_task_tts)
                                }
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val content = """
                                    Tarea: ${task.title}
                                    Descripción: ${task.description}
                                    Estado: ${if (task.isCompleted) "Completada" else "Pendiente"}
                                """.trimIndent()

                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, task.title)
                                    putExtra(Intent.EXTRA_TEXT, content)
                                }

                                context.startActivity(
                                    Intent.createChooser(intent, context.getString(R.string.share_chooser_title))
                                )
                            },
                            enabled = !isSaving && !isDeleting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .sizeIn(minHeight = 48.dp)
                        ) {
                            Text(stringResource(R.string.share_task))
                        }

                        Button(
                            onClick = { showDeleteConfirmation = true },
                            enabled = !isSaving && !isDeleting,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .sizeIn(minHeight = 48.dp)
                        ) {
                            Text(stringResource(R.string.delete_task))
                        }
                    }
                }
            }
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text(stringResource(R.string.delete_task_dialog_title)) },
                text = { Text(stringResource(R.string.delete_task_permanent_confirmation)) },
                confirmButton = {
                    Button(
                        onClick = {
                            showDeleteConfirmation = false
                            onDeleteTask()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.sizeIn(minHeight = 48.dp)
                    ) {
                        Text(stringResource(R.string.delete_button))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteConfirmation = false },
                        modifier = Modifier.sizeIn(minHeight = 48.dp)
                    ) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }
    }
}
