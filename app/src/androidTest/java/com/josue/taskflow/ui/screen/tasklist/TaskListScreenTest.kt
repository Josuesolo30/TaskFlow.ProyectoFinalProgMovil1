package com.josue.taskflow.ui.screen.tasklist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.josue.taskflow.R
import com.josue.taskflow.domain.model.task.Task
import com.josue.taskflow.domain.model.task.TaskFilter
import com.josue.taskflow.domain.model.task.TaskOperationResult
import com.josue.taskflow.ui.screen.tasklist.model.TaskListUiState
import com.josue.taskflow.ui.screen.tasklist.view.TaskListScreen
import com.josue.taskflow.ui.theme.TaskFlowTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas instrumentadas de interfaz de usuario con ComposeTestRule para TaskListScreen.
 * Verifica la interacción con el FAB y el diálogo 'Nueva Tarea'.
 */
@RunWith(AndroidJUnit4::class)
class TaskListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun dialogNuevaTarea_alHacerClicEnFabMuestraDialogo_deshabilitaBotonConTituloVacio_yCompletaAlta() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val addTaskText = context.getString(R.string.add_task)
        val dialogTitleText = context.getString(R.string.new_task_dialog_title)
        val titleLabelText = context.getString(R.string.task_title_label)
        val descLabelText = context.getString(R.string.task_description_label)
        val addButtonText = context.getString(R.string.add_button)

        var capturedTitle = ""
        var capturedDesc = ""
        var addCallCount = 0

        val initialTasks = listOf(
            Task(id = 1, title = "Tarea Inicial", description = "D1", isCompleted = false)
        )

        composeTestRule.setContent {
            TaskFlowTheme {
                TaskListScreen(
                    uiState = TaskListUiState.Content(tasks = initialTasks, activeFilter = TaskFilter.ALL),
                    activeFilter = TaskFilter.ALL,
                    onSelectFilter = {},
                    onAddTask = { title, desc, callback ->
                        addCallCount++
                        capturedTitle = title
                        capturedDesc = desc
                        callback(TaskOperationResult.Success)
                    },
                    onToggleCompletion = {},
                    onDeleteTask = {},
                    onOpenDetail = {},
                    onClearError = {},
                    onBack = {}
                )
            }
        }

        // 1. Abrir diálogo pulsando FAB
        composeTestRule.onNode(
            hasContentDescription(addTaskText) and hasClickAction()
        ).performClick()

        // Comprobar que aparece el título del diálogo
        composeTestRule.onNode(hasText(dialogTitleText)).assertIsDisplayed()

        // 2. Comprobar que el botón "Agregar" está deshabilitado con título vacío
        composeTestRule.onNode(
            hasText(addButtonText) and hasClickAction()
        ).assertIsNotEnabled()

        // 3. Escribir título y descripción
        composeTestRule.onNode(hasText(titleLabelText)).performTextInput("Título de prueba UI")
        composeTestRule.onNode(hasText(descLabelText)).performTextInput("Descripción de prueba UI")

        // Comprobar que ahora "Agregar" está habilitado
        composeTestRule.onNode(
            hasText(addButtonText) and hasClickAction()
        ).assertIsEnabled()

        // 4. Pulsar "Agregar" y verificar callback
        composeTestRule.onNode(
            hasText(addButtonText) and hasClickAction()
        ).performClick()

        assertEquals(1, addCallCount)
        assertEquals("Título de prueba UI", capturedTitle)
        assertEquals("Descripción de prueba UI", capturedDesc)

        // El diálogo se cierra tras el Success
        composeTestRule.onNode(hasText(dialogTitleText)).assertDoesNotExist()
    }
}
