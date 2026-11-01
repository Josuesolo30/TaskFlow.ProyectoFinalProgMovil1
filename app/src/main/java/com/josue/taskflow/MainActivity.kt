package com.josue.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.josue.taskflow.domain.usecase.task.TaskUseCase
import com.josue.taskflow.ui.message.AppMessageHost
import com.josue.taskflow.ui.navigation.AppNavHost
import com.josue.taskflow.ui.theme.TaskFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Activity principal de la aplicación anotada con @AndroidEntryPoint para Hilt.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var taskUseCase: TaskUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TaskFlowTheme {
                AppMessageHost(notificationsStream = taskUseCase.observeNotifications())
                AppNavHost()
            }
        }
    }
}
