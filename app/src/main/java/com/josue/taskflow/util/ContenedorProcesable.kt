package com.josue.taskflow.util

/**
 * Contenedor genérico reutilizable para almacenar, procesar y manipular colecciones de elementos de tipo [T].
 * 
 * Cumple con los requerimientos de las Unidades I y II:
 * - Programación funcional con operadores de orden superior (map, filter, fold, reduce).
 * - Uso semántico de Scope Functions (let, apply, also, run, with) con comentarios explicativos.
 * - Manejo de varianza (proyecciones in / out) en firmas de métodos.
 * - Type bounds (where T : Comparable<T>) para operaciones de comparación y ordenamiento.
 * - Funciones inline con parámetros reified para filtrado de tipos en tiempo de ejecución.
 *
 * @param elementos Lista inmutable de elementos contenidos.
 */
class ContenedorProcesable<T>(
    val elementos: List<T> = emptyList(),
) {

    // =========================================================================
    // 1. OPERADORES FUNCIONALES Y SCOPE FUNCTIONS (let, fold, reduce, map)
    // =========================================================================

    /**
     * Transforma cada elemento del contenedor a un nuevo tipo [R] utilizando [map].
     */
    fun <R> transformar(transformador: (T) -> R): List<R> {
        return elementos.map(transformador)
    }

    /**
     * Filtra los elementos según un [predicado] dado usando [filter].
     * 
     * SELECCIÓN DE SCOPE FUNCTION ('let'):
     * Elegimos 'let' para transformar la lista filtrada resultante en un nuevo objeto
     * [ContenedorProcesable] de manera concisa y funcional sin crear variables locales temporales.
     */
    @Suppress("RedundantLet")
    fun filtrar(predicado: (T) -> Boolean): ContenedorProcesable<T> {
        return elementos.filter(predicado).let { listaFiltrada ->
            ContenedorProcesable(listaFiltrada)
        }
    }

    /**
     * Combina todos los elementos de la colección a partir de un valor [inicial] usando [fold].
     */
    fun <R> acumular(inicial: R, operacion: (acumulado: R, elemento: T) -> R): R {
        return elementos.fold(inicial, operacion)
    }

    /**
     * Reduce la colección acumulando sus elementos con un operador binario usando [reduce].
     * Lanza [NoSuchElementException] si la colección está vacía.
     */
    fun reducir(operacion: (acumulado: T, elemento: T) -> T): T {
        return elementos.reduce(operacion)
    }

    // =========================================================================
    // 2. VARIANZA (in / out) Y SCOPE FUNCTIONS (run, also)
    // =========================================================================

    /**
     * Agrega elementos desde un arreglo con proyección covariante ([Array<out T>]).
     * La proyección 'out T' permite recibir arreglos de T o subtipos de T de forma segura (lectura).
     *
     * SELECCIÓN DE SCOPE FUNCTION ('run'):
     * Elegimos 'run' para delimitar la lógica de combinación de la lista existente con el nuevo arreglo
     * y retornar el nuevo [ContenedorProcesable] construido.
     */
    fun agregarDesdeFuente(fuente: Array<out T>): ContenedorProcesable<T> {
        return run {
            val listaCombinada = elementos + fuente.toList()
            ContenedorProcesable(listaCombinada)
        }
    }

    /**
     * Copia los elementos contenidos hacia una lista con proyección contravariante ([MutableList<in T>]).
     * La proyección 'in T' permite volcar los elementos en una lista capaz de aceptar T o supertipos de T.
     *
     * SELECCIÓN DE SCOPE FUNCTION ('also'):
     * Elegimos 'also' para ejecutar esta acción secundaria de volcado (efecto secundario)
     * manteniendo la referencia al contenedor original `this` para posibilitar encadenamiento de llamadas.
     */
    fun copiarA(destino: MutableList<in T>): ContenedorProcesable<T> {
        return this.also {
            destino.addAll(elementos)
        }
    }

    // =========================================================================
    // 3. FUNCIONALIDADES INLINE Y REIFIED CON SCOPE FUNCTIONS (apply, with)
    // =========================================================================

    /**
     * Filtra los elementos devolviendo únicamente aquellos que sean instancias del tipo [R] en tiempo de ejecución.
     *
     * Gracias a 'inline' y 'reified', la información del tipo [R] se conserva en tiempo de ejecución
     * (evitando la pérdida de tipos / type erasure), lo que permite usar 'is R'.
     *
     * SELECCIÓN DE SCOPE FUNCTION ('apply'):
     * Elegimos 'apply' para instanciar y configurar la MutableList acumuladora sobre su propio contexto 'this',
     * agregando los elementos filtrados antes de devolver el resultado.
     */
    inline fun <reified R : T> filtrarPorTipo(): List<R> {
        return mutableListOf<R>().apply {
            elementos.forEach { elemento ->
                if (elemento is R) {
                    add(elemento)
                }
            }
        }
    }

    /**
     * Ejecuta un bloque de código funcional pasando este contenedor como receptor ('this').
     *
     * SELECCIÓN DE SCOPE FUNCTION ('with'):
     * Elegimos 'with' para operar dentro del contexto explicito del contenedor,
     * permitiendo invocar métodos como [filtrar] o [transformar] sin calificar repetidamente el objeto.
     */
    fun <R> ejecutarOperacion(bloque: ContenedorProcesable<T>.() -> R): R {
        return with(this) {
            bloque()
        }
    }
}

// =============================================================================
// 4. TYPE BOUNDS (where T : Comparable<T>)
// =============================================================================

/**
 * Función de extensión para [ContenedorProcesable] que encuentra el elemento máximo.
 *
 * TYPE BOUND (where T : Comparable<T>):
 * Limita la disponibilidad de esta función únicamente a contenedores cuyo tipo [T] implemente [Comparable].
 */
fun <T> ContenedorProcesable<T>.obtenerMaximo(): T? where T : Comparable<T> {
    return elementos.maxOrNull()
}

/**
 * Función de extensión para [ContenedorProcesable] que encuentra el elemento mínimo.
 *
 * TYPE BOUND (where T : Comparable<T>):
 * Limita la disponibilidad de esta función únicamente a contenedores cuyo tipo [T] implemente [Comparable].
 */
fun <T> ContenedorProcesable<T>.obtenerMinimo(): T? where T : Comparable<T> {
    return elementos.minOrNull()
}

/**
 * Función de extensión para ordenar los elementos del contenedor.
 *
 * TYPE BOUND (where T : Comparable<T>):
 * Garantiza que la ordenación solo sea válida para elementos comparables.
 */
fun <T> ContenedorProcesable<T>.ordenar(): ContenedorProcesable<T> where T : Comparable<T> {
    return ContenedorProcesable(elementos.sorted())
}
