package com.josue.taskflow.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.josue.taskflow.ui.screen.home.view.HomeRoute
import com.josue.taskflow.ui.screen.taskdetail.view.TaskDetailRoute
import com.josue.taskflow.ui.screen.tasklist.view.TaskListRoute

/**
 * Host de navegación principal de la aplicación TaskFlow.
 */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestination.Home.route
    ) {
        composable(route = AppDestination.Home.route) {
            HomeRoute(
                onNavigateToTasks = {
                    navController.navigate(AppDestination.TaskList.route)
                }
            )
        }

        composable(route = AppDestination.TaskList.route) {
            TaskListRoute(
                onOpenDetail = { taskId ->
                    navController.navigate(AppDestination.TaskDetail.createRoute(taskId))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = AppDestination.TaskDetail.route,
            arguments = listOf(
                navArgument(AppDestination.TaskDetail.ARG_TASK_ID) {
                    type = NavType.IntType
                }
            )
        ) {
            TaskDetailRoute(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
