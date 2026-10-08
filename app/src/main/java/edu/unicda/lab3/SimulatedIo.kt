package edu.unicda.lab3

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

/** API clasica de callbacks (estilo Android pre-corrutinas). */
interface Callback<T> {
    fun onSuccess(value: T)
    fun onError(error: Throwable)
}

/** Atajo para crear un [Callback] con dos lambdas. */
fun <T> callback(ok: (T) -> Unit, err: (Throwable) -> Unit): Callback<T> = object : Callback<T> {
    override fun onSuccess(value: T) = ok(value)
    override fun onError(error: Throwable) = err(error)
}

/**
 * Manija para cancelar una operacion en curso.
 * Cancelar interrumpe el hilo de trabajo y garantiza que el callback NO se invoque.
 */
class Cancellable internal constructor() {
    @Volatile
    var isCancelled: Boolean = false
        private set

    private val hooks = CopyOnWriteArrayList<() -> Unit>()

    fun cancel() {
        if (isCancelled) return
        isCancelled = true
        hooks.forEach { it() }
    }

    internal fun onCancel(hook: () -> Unit) {
        hooks.add(hook)
        if (isCancelled) hook()
    }
}

/**
 * Simula I/O de red con Thread.sleep en hilos "io-N".
 * NO MODIFICAR: es parte del modulo entregado por el profesor.
 */
object SimulatedIo {
    /** Multiplicador de latencia. Los tests lo bajan para correr rapido. */
    @Volatile
    var latencyScale: Double = 1.0

    /** Cuantas operaciones tienen un hilo trabajando ahora mismo. */
    val activeCalls = AtomicInteger(0)

    private val threadCounter = AtomicInteger(0)
    private val startNanos = System.nanoTime()

    fun log(tag: String, msg: String) {
        val ms = (System.nanoTime() - startNanos) / 1_000_000
        println(
            "[+%5dms][%-5s] %-10s %s".format(
                ms,
                Thread.currentThread().name,
                tag,
                msg
            )
        )
    }

    fun <T> async(
        tag: String,
        baseMs: Long,
        work: () -> T,
        cb: Callback<T>
    ): Cancellable {
        val handle = Cancellable()

        val worker = Thread({
            activeCalls.incrementAndGet()

            var result: T? = null
            var error: Throwable? = null
            var interrupted = false

            try {
                Thread.sleep((baseMs * latencyScale).toLong())
                result = work()
            } catch (e: InterruptedException) {
                interrupted = true
            } catch (e: Throwable) {
                error = e
            } finally {
                activeCalls.decrementAndGet()
            }

            // Fuera del try para no capturar excepciones del consumidor.
            when {
                interrupted || handle.isCancelled -> {
                    log(tag, "CANCELADO (hilo interrumpido, sin callback)")
                }

                error != null -> {
                    log(tag, "error: ${error.message}")
                    cb.onError(error)
                }

                else -> {
                    log(tag, "ok")
                    @Suppress("UNCHECKED_CAST")
                    cb.onSuccess(result as T)
                }
            }
        }, "io-${threadCounter.incrementAndGet()}")

        worker.isDaemon = true
        handle.onCancel { worker.interrupt() }

        log(tag, "inicia (~${(baseMs * latencyScale).toLong()}ms)")
        worker.start()

        return handle
    }

    /** Espera y luego ejecuta action en un hilo io-N. */
    fun delayed(ms: Long, action: () -> Unit): Cancellable =
        async("wait", ms, { }, callback<Unit>({ action() }, { }))
}
