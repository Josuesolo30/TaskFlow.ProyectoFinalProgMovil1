package com.josue.taskflow.ui.navigation

/**
 * Destinos de navegación con tipos e identificadores de ruta.
 */
sealed class AppDestination(val route: String) {
    object Home : AppDestination("home")

    object TaskList : AppDestination("task_list")

    object TaskDetail : AppDestination("task_detail/{taskId}") {
        const val ARG_TASK_ID = "taskId"
        fun createRoute(taskId: Int): String = "task_detail/$taskId"
    }
}
