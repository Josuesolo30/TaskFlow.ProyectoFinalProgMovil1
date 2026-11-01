package com.josue.taskflow.ui.message

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.josue.taskflow.R
import kotlinx.coroutines.flow.Flow

/**
 * Consumidor único de notificaciones de confirmación a nivel de raíz de UI.
 * Mapea mensajes o códigos de confirmación en inglés a cadenas en español desde strings.xml.
 */
@Composable
fun AppMessageHost(
    notificationsStream: Flow<String>
) {
    val context = LocalContext.current

    LaunchedEffect(notificationsStream) {
        notificationsStream.collect { rawCode ->
            val message = resolveNotificationMessage(context, rawCode)
            if (message.isNotBlank()) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun resolveNotificationMessage(context: Context, rawCode: String): String {
    return when {
        rawCode.startsWith("msg_task_saved:") -> {
            val title = rawCode.substringAfter("msg_task_saved:")
            context.getString(R.string.msg_task_saved, title)
        }
        rawCode == "msg_task_completed" -> {
            context.getString(R.string.msg_task_completed)
        }
        rawCode == "msg_task_pending" -> {
            context.getString(R.string.msg_task_pending)
        }
        rawCode == "msg_description_saved" -> {
            context.getString(R.string.msg_description_saved)
        }
        rawCode.startsWith("msg_task_deleted:") -> {
            val title = rawCode.substringAfter("msg_task_deleted:")
            context.getString(R.string.msg_task_deleted, title)
        }
        else -> rawCode
    }
}
