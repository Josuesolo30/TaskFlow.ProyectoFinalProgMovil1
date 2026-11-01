package com.josue.taskflow.data.local.datasource.task

import com.josue.taskflow.data.local.database.dao.TaskDao
import com.josue.taskflow.data.local.database.entity.task.TaskEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Fuente de datos local que abstrae las operaciones sobre Room Database.
 */
class TaskLocalDataSource @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getTasksStream(): Flow<List<TaskEntity>> {
        return taskDao.getTasksStream()
    }

    fun getTaskStream(taskId: Int): Flow<TaskEntity?> {
        return taskDao.getTaskStream(taskId)
    }

    suspend fun getTaskById(taskId: Int): TaskEntity? {
        return taskDao.getTaskById(taskId)
    }

    suspend fun insertTask(task: TaskEntity): Long {
        return taskDao.insertTask(task)
    }

    suspend fun updateTask(task: TaskEntity): Int {
        return taskDao.updateTask(task)
    }

    suspend fun updateDescription(taskId: Int, description: String): Int {
        return taskDao.updateDescription(taskId, description)
    }

    suspend fun toggleCompletion(taskId: Int): Int {
        return taskDao.toggleCompletion(taskId)
    }

    suspend fun deleteTask(taskId: Int): Int {
        return taskDao.deleteTask(taskId)
    }
}
