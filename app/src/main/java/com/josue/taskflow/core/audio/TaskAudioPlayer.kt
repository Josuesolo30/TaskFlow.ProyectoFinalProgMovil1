package com.josue.taskflow.core.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Adaptador de lectura de texto a voz (TextToSpeech) para Android.
 * Convierte los callbacks de Android TTS en llamadas suspendibles gestionadas con corrutinas.
 * Registra eventos en Logcat con la etiqueta 'TaskAudioPlayer' omitiendo textos privados.
 */
class TaskAudioPlayer(private val context: Context) {

    companion object {
        private const val TAG = "TaskAudioPlayer"
    }

    private var tts: TextToSpeech? = null

    private suspend fun initTts(): TextToSpeech = suspendCancellableCoroutine { continuation ->
        var tempTts: TextToSpeech? = null
        tempTts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tempTts?.language = Locale("es", "ES")
                Log.d(TAG, "TTS inicializado correctamente")
                if (continuation.isActive) {
                    continuation.resume(tempTts!!)
                }
            } else {
                Log.e(TAG, "Error al inicializar TTS status: $status")
                if (continuation.isActive) {
                    continuation.resumeWithException(
                        IllegalStateException("Error al inicializar TextToSpeech (status: $status)")
                    )
                }
            }
        }

        continuation.invokeOnCancellation {
            Log.d(TAG, "Cancelación en la inicialización de TTS")
            tempTts?.stop()
            tempTts?.shutdown()
        }
    }

    suspend fun playText(text: String) {
        try {
            val player = tts ?: initTts().also { tts = it }
            val utteranceId = UUID.randomUUID().toString()

            suspendCancellableCoroutine<Unit> { continuation ->
                player.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        Log.d(TAG, "Inicio de reproducción TTS")
                    }

                    override fun onDone(id: String?) {
                        Log.d(TAG, "Finalización exitosa de reproducción TTS")
                        if (id == utteranceId && continuation.isActive) {
                            continuation.resume(Unit)
                        }
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        Log.e(TAG, "Fallo en reproducción TTS")
                        if (continuation.isActive) {
                            continuation.resumeWithException(
                                RuntimeException("Error al reproducir audio de la tarea")
                            )
                        }
                    }

                    override fun onError(utteranceId: String?, errorCode: Int) {
                        Log.e(TAG, "Fallo de audio TTS codigo: $errorCode")
                        if (continuation.isActive) {
                            continuation.resumeWithException(
                                RuntimeException("Error de audio TTS (código: $errorCode)")
                            )
                        }
                    }
                })

                continuation.invokeOnCancellation {
                    Log.d(TAG, "Cancelación de reproducción TTS")
                    player.stop()
                }

                player.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            }
        } catch (e: CancellationException) {
            Log.d(TAG, "Relanzando CancellationException en playText")
            throw e
        }
    }

    fun release() {
        Log.d(TAG, "Liberación de recursos TTS")
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
