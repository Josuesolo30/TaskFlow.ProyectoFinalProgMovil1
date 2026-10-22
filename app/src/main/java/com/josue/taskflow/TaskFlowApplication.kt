package com.josue.taskflow

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase Application principal de la app annotada con @HiltAndroidApp para inicializar Hilt.
 */
@HiltAndroidApp
class TaskFlowApplication : Application()
