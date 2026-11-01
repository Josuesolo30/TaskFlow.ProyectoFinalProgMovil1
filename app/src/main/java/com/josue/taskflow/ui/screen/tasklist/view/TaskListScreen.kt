package com.josue.taskflow.ui.screen.tasklist.view

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.josue.taskflow.R
import com.josue.taskflow.domain.model.task.TaskFilter
import com.josue.taskflow.domain.model.task.TaskOperationResult
import com.josue.taskflow.ui.components.TaskCard
import com.josue.taskflow.ui.screen.tasklist.model.TaskListUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    uiState: TaskListUiState,
    activeFilter: TaskFilter,
    onSelectFilter: (TaskFilter) -> Unit,
    onAddTask: (String, String, (TaskOperationResult) -> Unit) -> Unit,
    onToggleCompletion: (Int) -> Unit,
    onDeleteTask: (Int) -> Unit,
    onOpenDetail: (Int) -> Unit,
    onClearError: () -> Unit,
    onBack: () -> Unit
) {
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var newTitle by rememberSaveable { mutableStateOf("") }
    var newDescription by rememberSaveable { mutableStateOf("") }

    var taskToDeleteId by rememberSaveable { mutableStateOf<Int?>(null) }

    val listState = rememberLazyListState()

    val isSubmitting = when (uiState) {
        is TaskListUiState.Content -> uiState.isSubmitting
        is TaskListUiState.Empty -> uiState.isSubmitting
        else -> false
    }

    val operationError = when (uiState) {
        is TaskListUiState.Content -> uiState.operationError
        is TaskListUiState.Empty -> uiState.operationError
        else -> null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_tasks_title)) },
                navigationIcon = {
                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                    ) {
                        Text(stringResource(R.string.back))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onClearError()
                    showCreateDialog = true
                },
                modifier = Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_task)
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
            // Fila de filtros con desplazamiento horizontal para pantallas estrechas y fuentes al 200%
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TaskFilter.entries.forEach { filter ->
                    val selected = filter == activeFilter
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectFilter(filter) },
                        modifier = Modifier.sizeIn(minHeight = 48.dp),
                        label = {
                            Text(
                                when (filter) {
                                    TaskFilter.ALL -> stringResource(R.string.filter_all)
                                    TaskFilter.PENDING -> stringResource(R.string.filter_pending)
                                    TaskFilter.COMPLETED -> stringResource(R.string.filter_completed)
                                }
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (uiState) {
                is TaskListUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is TaskListUiState.Empty -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (uiState.activeFilter) {
                                TaskFilter.ALL -> stringResource(R.string.empty_tasks_all)
                                TaskFilter.PENDING -> stringResource(R.string.empty_tasks_pending)
                                TaskFilter.COMPLETED -> stringResource(R.string.empty_tasks_completed)
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                is TaskListUiState.Error -> {
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

                is TaskListUiState.Content -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = uiState.tasks,
                            key = { task -> task.id }
                        ) { task ->
                            TaskCard(
                                task = task,
                                onStatusToggle = { onToggleCompletion(task.id) },
                                onClick = { onOpenDetail(task.id) },
                                onDelete = { taskToDeleteId = task.id }
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = {
                    if (!isSubmitting) {
                        showCreateDialog = false
                        newTitle = ""
                        newDescription = ""
                        onClearError()
                    }
                },
                title = { Text(stringResource(R.string.new_task_dialog_title)) },
                text = {
                    Column {
                        if (operationError != null) {
                            Text(
                                text = operationError,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text(stringResource(R.string.task_title_label)) },
                            singleLine = true,
                            enabled = !isSubmitting,
                            modifier = Modifier
                                .fillMaxWidth()
                                .sizeIn(minHeight = 48.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = newDescription,
                            onValueChange = { newDescription = it },
                            label = { Text(stringResource(R.string.task_description_label)) },
                            minLines = 3,
                            maxLines = 5,
                            enabled = !isSubmitting,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onAddTask(newTitle, newDescription) { result ->
                                if (result is TaskOperationResult.Success) {
                                    newTitle = ""
                                    newDescription = ""
                                    showCreateDialog = false
                                }
                            }
                        },
                        enabled = newTitle.isNotBlank() && !isSubmitting,
                        modifier = Modifier.sizeIn(minHeight = 48.dp)
                    ) {
                        Text(stringResource(R.string.add_button))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            if (!isSubmitting) {
                                showCreateDialog = false
                                newTitle = ""
                                newDescription = ""
                                onClearError()
                            }
                        },
                        enabled = !isSubmitting,
                        modifier = Modifier.sizeIn(minHeight = 48.dp)
                    ) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }

        taskToDeleteId?.let { id ->
            AlertDialog(
                onDismissRequest = { taskToDeleteId = null },
                title = { Text(stringResource(R.string.delete_task_dialog_title)) },
                text = { Text(stringResource(R.string.delete_task_confirmation)) },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteTask(id)
                            taskToDeleteId = null
                        },
                        modifier = Modifier.sizeIn(minHeight = 48.dp)
                    ) {
                        Text(stringResource(R.string.delete_button))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { taskToDeleteId = null },
                        modifier = Modifier.sizeIn(minHeight = 48.dp)
                    ) {
                        Text(stringResource(R.string.cancel_button))
                    }
                }
            )
        }
    }
}
