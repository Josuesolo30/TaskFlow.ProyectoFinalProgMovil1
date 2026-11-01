package com.josue.taskflow.ui.message

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.josue.taskflow.R
import kotlinx.coroutines.flow.Flow

/**
 * Consumidor único de notificaciones a nivel de raíz de UI con repeatOnLifecycle(State.STARTED).
 */
@Composable
fun AppMessageHost(
    notificationsStream: Flow<String>
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(notificationsStream, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            notificationsStream.collect { rawCode ->
                val message = resolveNotificationMessage(context, rawCode)
                if (message.isNotBlank()) {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
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
