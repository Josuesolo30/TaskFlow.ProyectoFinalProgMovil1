package com.josue.taskflow.domain.usecase.task

import com.josue.taskflow.core.collection.ProcessableContainer
import com.josue.taskflow.core.collection.maxOrNull
import com.josue.taskflow.core.collection.minOrNull
import com.josue.taskflow.domain.model.statistics.TaskStatistics
import com.josue.taskflow.domain.model.task.Task
import com.josue.taskflow.domain.model.task.TaskFilter
import com.josue.taskflow.domain.model.task.TaskOperationResult
import com.josue.taskflow.domain.repository.settings.SettingsRepository
import com.josue.taskflow.domain.repository.task.TaskRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Caso de Uso unificado con operaciones explícitas sobre la funcionalidad de tareas.
 */
class TaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val settingsRepository: SettingsRepository
) {

    fun observeTasks(): Flow<List<Task>> = taskRepository.getTasksStream()

    fun observeTask(taskId: Int): Flow<Task?> = taskRepository.getTaskStream(taskId)

    fun observeFilter(): Flow<TaskFilter> = settingsRepository.getFilterStream()

    fun observeNotifications(): Flow<String> = taskRepository.getNotificationsStream()

    suspend fun saveFilter(filter: TaskFilter): TaskOperationResult {
        return try {
            settingsRepository.saveFilter(filter)
            TaskOperationResult.Success
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            TaskOperationResult.Failure(e)
        }
    }

    suspend fun addTask(title: String, description: String = ""): TaskOperationResult {
        val cleanTitle = title.trim()
        if (cleanTitle.isBlank()) {
            return TaskOperationResult.InvalidTitle
        }
        return taskRepository.addTask(title = cleanTitle, description = description.trim())
    }

    suspend fun toggleCompletion(taskId: Int): TaskOperationResult {
        return taskRepository.toggleCompletion(taskId)
    }

    suspend fun updateDescription(taskId: Int, description: String): TaskOperationResult {
        return taskRepository.updateDescription(taskId, description.trim())
    }

    suspend fun deleteTask(taskId: Int): TaskOperationResult {
        return taskRepository.deleteTask(taskId)
    }

    fun observeStatistics(): Flow<TaskStatistics> {
        return observeTasks().map { tasks ->
            calculateStatistics(tasks)
        }
    }

    suspend fun calculateStatistics(tasks: List<Task>): TaskStatistics = withContext(Dispatchers.Default) {
        if (tasks.isEmpty()) {
            return@withContext TaskStatistics()
        }

        val container = ProcessableContainer(tasks)

        coroutineScope {
            val countDeferred = async {
                val completed = container.filter { it.isCompleted }.elements.size
                val pending = container.filter { !it.isCompleted }.elements.size
                val total = container.elements.size
                val percentage = if (total > 0) (completed * 100) / total else 0
                Triple(completed, pending, percentage)
            }

            val summaryDeferred = async {
                val maxTask = container.maxOrNull()
                val minTask = container.minOrNull()
                val summaryText = container.fold("Resumen de tareas:\n") { acc, task ->
                    val status = if (task.isCompleted) "[✓]" else "[ ]"
                    "$acc- $status ${task.title}\n"
                }
                Triple(maxTask, minTask, summaryText)
            }

            val (completed, pending, percentage) = countDeferred.await()
            val (maxTask, minTask, summaryText) = summaryDeferred.await()

            TaskStatistics(
                totalCount = tasks.size,
                completedCount = completed,
                pendingCount = pending,
                completionPercentage = percentage,
                maxIdTask = maxTask,
                minIdTask = minTask,
                summaryText = summaryText
            )
        }
    }
}
