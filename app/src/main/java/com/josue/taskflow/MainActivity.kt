package com.josue.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.josue.taskflow.ui.navigation.AppNavigation
import com.josue.taskflow.ui.theme.TaskFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContent inicia la interfaz creada con Jetpack Compose.
        setContent {
            TaskFlowTheme {
                AppNavigation()
            }
        }
    }
}
