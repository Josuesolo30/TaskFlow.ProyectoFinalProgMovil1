package com.josue.taskflow.core.collection

import com.josue.taskflow.domain.model.task.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pruebas unitarias para validar [ProcessableContainer].
 */
class ProcessableContainerTest {

    @Test
    fun `map debe transformar elementos a un nuevo tipo`() {
        val container = ProcessableContainer(listOf(1, 2, 3, 4))
        val result = container.map { "Numero: $it" }

        assertEquals(listOf("Numero: 1", "Numero: 2", "Numero: 3", "Numero: 4"), result)
    }

    @Test
    fun `filter debe retornar elementos que cumplen el predicado`() {
        val container = ProcessableContainer(listOf(1, 2, 3, 4, 5, 6))
        val evens = container.filter { it % 2 == 0 }

        assertEquals(listOf(2, 4, 6), evens.elements)
    }

    @Test
    fun `fold debe realizar acumulacion desde un valor inicial`() {
        val container = ProcessableContainer(listOf(10, 20, 30))
        val total = container.fold(100) { acc, elem -> acc + elem }

        assertEquals(160, total)
    }

    @Test
    fun `reduce debe combinar elementos sin valor inicial`() {
        val container = ProcessableContainer(listOf("A", "B", "C", "D"))
        val concatenated = container.reduce { acc, elem -> "$acc-$elem" }

        assertEquals("A-B-C-D", concatenated)
    }

    @Test
    fun `addFromSource debe aceptar arreglos covariantes out T`() {
        val numberContainer = ProcessableContainer<Number>(listOf(1, 2))
        val source: Array<Int> = arrayOf(3, 4)

        val result = numberContainer.addFromSource(source)

        assertEquals(listOf<Number>(1, 2, 3, 4), result.elements)
    }

    @Test
    fun `copyTo debe volcar elementos en lista contravariante in T`() {
        val intContainer = ProcessableContainer(listOf(10, 20))
        val target: MutableList<Number> = mutableListOf(1.0, 2.0)

        intContainer.copyTo(target)

        assertEquals(4, target.size)
        assertEquals(listOf<Number>(1.0, 2.0, 10, 20), target)
    }

    @Test
    fun `filterByType debe filtrar elementos por tipo en tiempo de ejecucion`() {
        val heterogeneous = ProcessableContainer<Any>(listOf("Kotlin", 42, "Compose", 3.14, "Android", true))

        val strings: List<String> = heterogeneous.filterByType<String>()
        val ints: List<Int> = heterogeneous.filterByType<Int>()

        assertEquals(listOf("Kotlin", "Compose", "Android"), strings)
        assertEquals(listOf(42), ints)
    }

    @Test
    fun `maxOrNull y minOrNull deben retornar extremos de tipos Comparables`() {
        val numbers = ProcessableContainer(listOf(15, 3, 99, 42, 7))

        assertEquals(99, numbers.maxOrNull())
        assertEquals(3, numbers.minOrNull())
    }

    @Test
    fun `sorted debe ordenar elementos`() {
        val unsorted = ProcessableContainer(listOf("Zebra", "Apple", "Mango", "Banana"))
        val sorted = unsorted.sorted()

        assertEquals(listOf("Apple", "Banana", "Mango", "Zebra"), sorted.elements)
    }

    @Test
    fun `maxOrNull y minOrNull con la entidad Task`() {
        val t1 = Task(id = 1, title = "Task A", description = "Desc A")
        val t2 = Task(id = 5, title = "Task E", description = "Desc E")
        val t3 = Task(id = 3, title = "Task C", description = "Desc C")

        val taskContainer = ProcessableContainer(listOf(t1, t2, t3))

        assertEquals(t2, taskContainer.maxOrNull())
        assertEquals(t1, taskContainer.minOrNull())
    }

    @Test
    fun `maxOrNull en contenedor vacio retorna null`() {
        val empty = ProcessableContainer<Int>(emptyList())

        assertNull(empty.maxOrNull())
        assertNull(empty.minOrNull())
    }

    @Test
    fun `executeOperation debe ejecutar lambda con receptor this`() {
        val container = ProcessableContainer(listOf(1, 2, 3, 4, 5))

        val evenCount = container.executeOperation {
            filter { it % 2 == 0 }.elements.size
        }

        assertEquals(2, evenCount)
    }
}
