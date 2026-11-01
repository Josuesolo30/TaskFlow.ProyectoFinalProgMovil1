package com.josue.taskflow.data

import com.josue.taskflow.domain.model.task.Task
import com.josue.taskflow.domain.model.task.TaskOperationResult
import com.josue.taskflow.domain.repository.task.TaskRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Repositorio de pruebas (Fake) para verificar la lógica de dominio y ViewModels en tests.
 */
class FakeTaskRepository : TaskRepository {

    private val mutex = Mutex()
    private val notificationChannel = Channel<String>(Channel.BUFFERED)

    private val tasks = MutableStateFlow(
        listOf(
            Task(id = 1, title = "Estudiar navegación", description = "Repasar Inicio, Lista y Detalle."),
            Task(id = 2, title = "Preparar el README", description = "Documentar TaskFlow.", isCompleted = true),
            Task(id = 3, title = "Probar la aplicación", description = "Verificar todos los botones.")
        )
    )

    private var nextId = 4

    var shouldFail = false

    override fun getTasksStream(): Flow<List<Task>> = tasks.asStateFlow()

    override fun getTaskStream(taskId: Int): Flow<Task?> = tasks.map { list -> list.firstOrNull { it.id == taskId } }

    override fun getNotificationsStream(): Flow<String> = notificationChannel.receiveAsFlow()

    override suspend fun addTask(title: String, description: String): TaskOperationResult {
        if (shouldFail) {
            return TaskOperationResult.Failure(RuntimeException("Error en fake repository"))
        }
        val cleanTitle = title.trim()
        if (cleanTitle.isBlank()) {
            return TaskOperationResult.InvalidTitle
        }
        mutex.withLock {
            val newTask = Task(
                id = nextId++,
                title = cleanTitle,
                description = description.trim(),
                isCompleted = false
            )
            tasks.value = tasks.value + newTask
            notificationChannel.send("msg_task_saved:$cleanTitle")
        }
        return TaskOperationResult.Success
    }

    override suspend fun toggleCompletion(taskId: Int): TaskOperationResult {
        if (shouldFail) {
            return TaskOperationResult.Failure(RuntimeException("Error en fake repository"))
        }
        val existing = tasks.value.firstOrNull { it.id == taskId }
            ?: return TaskOperationResult.TaskNotFound

        mutex.withLock {
            tasks.value = tasks.value.map { task ->
                if (task.id == taskId) task.copy(isCompleted = !task.isCompleted) else task
            }
            val msgKey = if (!existing.isCompleted) "msg_task_completed" else "msg_task_pending"
            notificationChannel.send(msgKey)
        }
        return TaskOperationResult.Success
    }

    override suspend fun updateDescription(taskId: Int, description: String): TaskOperationResult {
        if (shouldFail) {
            return TaskOperationResult.Failure(RuntimeException("Error en fake repository"))
        }
        val existing = tasks.value.firstOrNull { it.id == taskId }
            ?: return TaskOperationResult.TaskNotFound

        mutex.withLock {
            tasks.value = tasks.value.map { task ->
                if (task.id == taskId) task.copy(description = description.trim()) else task
            }
            notificationChannel.send("msg_description_saved")
        }
        return TaskOperationResult.Success
    }

    override suspend fun deleteTask(taskId: Int): TaskOperationResult {
        if (shouldFail) {
            return TaskOperationResult.Failure(RuntimeException("Error en fake repository"))
        }
        val existing = tasks.value.firstOrNull { it.id == taskId }
            ?: return TaskOperationResult.TaskNotFound

        mutex.withLock {
            tasks.value = tasks.value.filter { it.id != taskId }
            notificationChannel.send("msg_task_deleted:${existing.title}")
        }
        return TaskOperationResult.Success
    }
}
