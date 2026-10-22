package com.josue.taskflow.core.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Adaptador de lectura de texto a voz (TextToSpeech) para la plataforma Android.
 * Convierte los callbacks de Android TTS en llamadas suspendibles gestionadas con corrutinas.
 */
class TaskAudioPlayer(private val context: Context) {

    private var tts: TextToSpeech? = null

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
