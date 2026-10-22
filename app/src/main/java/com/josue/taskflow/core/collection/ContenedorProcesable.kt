package com.josue.taskflow.core.collection

/**
 * Contenedor genérico reutilizable para almacenar, procesar y manipular colecciones de elementos de tipo [T].
 *
 * @param elementos Lista inmutable de elementos contenidos.
 */
class ContenedorProcesable<T>(
    val elementos: List<T> = emptyList(),
) {

    fun <R> transformar(transformador: (T) -> R): List<R> {
        return elementos.map(transformador)
    }

    fun filtrar(predicado: (T) -> Boolean): ContenedorProcesable<T> {
        return elementos.filter(predicado).let { listaFiltrada ->
            ContenedorProcesable(listaFiltrada)
        }
    }

    fun <R> acumular(inicial: R, operacion: (acumulado: R, elemento: T) -> R): R {
        return elementos.fold(inicial, operacion)
    }

    fun reducir(operacion: (acumulado: T, elemento: T) -> T): T {
        return elementos.reduce(operacion)
    }

    fun agregarDesdeFuente(fuente: Array<out T>): ContenedorProcesable<T> {
        return run {
            val listaCombinada = elementos + fuente.toList()
            ContenedorProcesable(listaCombinada)
        }
    }

    fun copiarA(destino: MutableList<in T>): ContenedorProcesable<T> {
        return this.also {
            destino.addAll(elementos)
        }
    }

    inline fun <reified R : T> filtrarPorTipo(): List<R> {
        return mutableListOf<R>().apply {
            elementos.forEach { elemento ->
                if (elemento is R) {
                    add(elemento)
                }
            }
        }
    }

    fun <R> ejecutarOperacion(bloque: ContenedorProcesable<T>.() -> R): R {
        return with(this) {
            bloque()
        }
    }
}

fun <T> ContenedorProcesable<T>.obtenerMaximo(): T? where T : Comparable<T> {
    return elementos.maxOrNull()
}

fun <T> ContenedorProcesable<T>.obtenerMinimo(): T? where T : Comparable<T> {
    return elementos.minOrNull()
}

fun <T> ContenedorProcesable<T>.ordenar(): ContenedorProcesable<T> where T : Comparable<T> {
    return ContenedorProcesable(elementos.sorted())
}
