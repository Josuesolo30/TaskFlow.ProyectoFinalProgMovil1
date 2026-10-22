package com.josue.taskflow.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Adaptador de lectura de texto a voz (TextToSpeech) para reproducir en voz alta
 * el título y descripción de una tarea real.
 *
 * Cumple con el requerimiento del Lab 3 de migrar APIs basadas en callbacks
 * a corrutinas con [suspendCancellableCoroutine], controlando el éxito, error y cancelación.
 */
class TaskAudioPlayer(private val context: Context) {

    private var tts: TextToSpeech? = null

    /**
     * Inicializa TextToSpeech suspendiendo hasta que el callback [TextToSpeech.OnInitListener] responde.
     */
    private suspend fun initTts(): TextToSpeech = suspendCancellableCoroutine { continuation ->
        var tempTts: TextToSpeech? = null
        tempTts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tempTts?.language = Locale("es", "ES")
                if (continuation.isActive) {
                    continuation.resume(tempTts!!)
                }
            } else {
                if (continuation.isActive) {
                    continuation.resumeWithException(
                        IllegalStateException("Error al inicializar TextToSpeech (status: $status)")
                    )
                }
            }
        }

        continuation.invokeOnCancellation {
            tempTts?.stop()
            tempTts?.shutdown()
        }
    }

    /**
     * Lee un texto en voz alta y suspende hasta que la reproducción termina (vía UtteranceProgressListener)
     * o hasta que la corrutina sea cancelada.
     */
    suspend fun reproducirTexto(texto: String) {
        val player = tts ?: initTts().also { tts = it }
        val utteranceId = UUID.randomUUID().toString()

        suspendCancellableCoroutine<Unit> { continuation ->
            player.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                override fun onDone(id: String?) {
                    if (id == utteranceId && continuation.isActive) {
                        continuation.resume(Unit)
                    }
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(
                            RuntimeException("Error al reproducir audio de la tarea")
                        )
                    }
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(
                            RuntimeException("Error de audio TTS (código: $errorCode)")
                        )
                    }
                }
            })

            continuation.invokeOnCancellation {
                player.stop()
            }

            player.speak(texto, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    fun liberar() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
