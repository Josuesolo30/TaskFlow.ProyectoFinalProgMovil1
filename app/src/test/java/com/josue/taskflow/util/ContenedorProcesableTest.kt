package com.josue.taskflow.util

import com.josue.taskflow.dominio.model.Tarea
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pruebas unitarias para validar las capacidades genéricas, funcionales, varianza y type bounds
 * de [ContenedorProcesable].
 */
class ContenedorProcesableTest {

    // -------------------------------------------------------------------------
    // 1. Pruebas de Operadores Funcionales (map, filter, fold, reduce)
    // -------------------------------------------------------------------------

    @Test
    fun `transformar debe mapear elementos a un nuevo tipo`() {
        val contenedor = ContenedorProcesable(listOf(1, 2, 3, 4))
        val resultado = contenedor.transformar { "Numero: $it" }

        assertEquals(listOf("Numero: 1", "Numero: 2", "Numero: 3", "Numero: 4"), resultado)
    }

    @Test
    fun `filtrar debe retornar un nuevo ContenedorProcesable con los elementos que cumplen el predicado`() {
        val contenedor = ContenedorProcesable(listOf(1, 2, 3, 4, 5, 6))
        val pares = contenedor.filtrar { it % 2 == 0 }

        assertEquals(listOf(2, 4, 6), pares.elementos)
    }

    @Test
    fun `acumular debe realizar fold desde un valor inicial`() {
        val contenedor = ContenedorProcesable(listOf(10, 20, 30))
        val sumaTotal = contenedor.acumular(100) { acum, elem -> acum + elem }

        assertEquals(160, sumaTotal)
    }

    @Test
    fun `reducir debe combinar elementos sin valor inicial`() {
        val contenedor = ContenedorProcesable(listOf("A", "B", "C", "D"))
        val concatenado = contenedor.reducir { acum, elem -> "$acum-$elem" }

        assertEquals("A-B-C-D", concatenado)
    }

    // -------------------------------------------------------------------------
    // 2. Pruebas de Varianza (out / in)
    // -------------------------------------------------------------------------

    @Test
    fun `agregarDesdeFuente debe aceptar arreglos covariantes out T`() {
        // Number es supertipo de Int y Double
        val contenedorNumber = ContenedorProcesable<Number>(listOf(1, 2))
        val fuenteSubtipo: Array<Int> = arrayOf(3, 4)

        val resultado = contenedorNumber.agregarDesdeFuente(fuenteSubtipo)

        assertEquals(listOf<Number>(1, 2, 3, 4), resultado.elementos)
    }

    @Test
    fun `copiarA debe volcar elementos en una lista contravariante in T`() {
        val contenedorInt = ContenedorProcesable(listOf(10, 20))
        val destinoSupertipo: MutableList<Number> = mutableListOf(1.0, 2.0)

        contenedorInt.copiarA(destinoSupertipo)

        assertEquals(4, destinoSupertipo.size)
        assertEquals(listOf<Number>(1.0, 2.0, 10, 20), destinoSupertipo)
    }

    // -------------------------------------------------------------------------
    // 3. Pruebas de Inline Reified (filtrarPorTipo)
    // -------------------------------------------------------------------------

    @Test
    fun `filtrarPorTipo debe filtrar elementos en tiempo de ejecucion usando reified`() {
        val heterogeneo = ContenedorProcesable<Any>(listOf("Kotlin", 42, "Compose", 3.14, "Android", true))

        val soloStrings: List<String> = heterogeneo.filtrarPorTipo<String>()
        val soloEnteros: List<Int> = heterogeneo.filtrarPorTipo<Int>()

        assertEquals(listOf("Kotlin", "Compose", "Android"), soloStrings)
        assertEquals(listOf(42), soloEnteros)
    }

    // -------------------------------------------------------------------------
    // 4. Pruebas de Type Bounds (where T Comparable T)
    // -------------------------------------------------------------------------

    @Test
    fun `obtenerMaximo y obtenerMinimo deben retornar el valor extremo para tipos Comparables`() {
        val numeros = ContenedorProcesable(listOf(15, 3, 99, 42, 7))

        assertEquals(99, numeros.obtenerMaximo())
        assertEquals(3, numeros.obtenerMinimo())
    }

    @Test
    fun `ordenar debe retornar un contenedor con elementos ordenados`() {
        val desordenados = ContenedorProcesable(listOf("Zebra", "Apple", "Mango", "Banana"))
        val ordenados = desordenados.ordenar()

        assertEquals(listOf("Apple", "Banana", "Mango", "Zebra"), ordenados.elementos)
    }

    @Test
    fun `obtenerMaximo y obtenerMinimo con la entidad Tarea`() {
        val t1 = Tarea(id = 1, titulo = "Tarea A", descripcion = "Desc A")
        val t2 = Tarea(id = 5, titulo = "Tarea E", descripcion = "Desc E")
        val t3 = Tarea(id = 3, titulo = "Tarea C", descripcion = "Desc C")

        val contenedorTareas = ContenedorProcesable(listOf(t1, t2, t3))

        assertEquals(t2, contenedorTareas.obtenerMaximo()) // ID 5
        assertEquals(t1, contenedorTareas.obtenerMinimo()) // ID 1
    }

    @Test
    fun `obtenerMaximo en contenedor vacio debe retornar null`() {
        val vacio = ContenedorProcesable<Int>(emptyList())

        assertNull(vacio.obtenerMaximo())
        assertNull(vacio.obtenerMinimo())
    }

    // -------------------------------------------------------------------------
    // 5. Pruebas de Receiver / Scope function (with / ejecutarOperacion)
    // -------------------------------------------------------------------------

    @Test
    fun `ejecutarOperacion debe permitir operacion con receptor this`() {
        val contenedor = ContenedorProcesable(listOf(1, 2, 3, 4, 5))

        val conteoPares = contenedor.ejecutarOperacion {
            filtrar { it % 2 == 0 }.elementos.size
        }

        assertEquals(2, conteoPares)
    }
}
