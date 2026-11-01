package com.josue.taskflow.core.collection

/**
 * Contenedor genérico procesable reutilizable para manipular colecciones de tipo [T].
 * Conserva varianza, funciones de orden superior, type bounds y scope functions.
 *
 * @param elements Lista inmutable de elementos contenidos.
 */
class ProcessableContainer<T>(
    val elements: List<T> = emptyList()
) {

    fun <R> map(transform: (T) -> R): List<R> {
        return elements.map(transform)
    }

    fun filter(predicate: (T) -> Boolean): ProcessableContainer<T> {
        return elements.filter(predicate).let { filteredList ->
            ProcessableContainer(filteredList)
        }
    }

    fun <R> fold(initial: R, operation: (acc: R, element: T) -> R): R {
        return elements.fold(initial, operation)
    }

    fun reduce(operation: (acc: T, element: T) -> T): T {
        return elements.reduce(operation)
    }

    fun addFromSource(source: Array<out T>): ProcessableContainer<T> {
        return run {
            val combined = elements + source.toList()
            ProcessableContainer(combined)
        }
    }

    fun copyTo(destination: MutableList<in T>): ProcessableContainer<T> {
        return this.also {
            destination.addAll(elements)
        }
    }

    inline fun <reified R : T> filterByType(): List<R> {
        return mutableListOf<R>().apply {
            elements.forEach { element ->
                if (element is R) {
                    add(element)
                }
            }
        }
    }

    fun <R> executeOperation(block: ProcessableContainer<T>.() -> R): R {
        return with(this) {
            block()
        }
    }
}

fun <T> ProcessableContainer<T>.maxOrNull(): T? where T : Comparable<T> {
    return elements.maxOrNull()
}

fun <T> ProcessableContainer<T>.minOrNull(): T? where T : Comparable<T> {
    return elements.minOrNull()
}

fun <T> ProcessableContainer<T>.sorted(): ProcessableContainer<T> where T : Comparable<T> {
    return ProcessableContainer(elements.sorted())
}
