package com.josue.taskflow.data.mapper

import com.josue.taskflow.data.mapper.task.toDomain
import com.josue.taskflow.data.mapper.task.toEntity
import com.josue.taskflow.data.remote.dto.TaskDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TaskRemoteMapperTest {

    @Test
    fun `TaskDto toDomain mapea titulo e isCompleted y no inventa descripcion`() {
        val dto = TaskDto(userId = 1, id = 99, title = "  Remote Title  ", completed = false)
        val domainTask = dto.toDomain()

        assertEquals("Remote Title", domainTask.title)
        assertEquals("", domainTask.description)
        assertFalse(domainTask.isCompleted)
        assertEquals(0L, domainTask.createdAt)
    }

    @Test
    fun `TaskDto toEntity mapea titulo e isCompleted`() {
        val dto = TaskDto(userId = 1, id = 99, title = "Remote Title", completed = true)
        val entity = dto.toEntity()

        assertEquals("Remote Title", entity.title)
        assertEquals("", entity.description)
        assertEquals(true, entity.isCompleted)
        assertEquals(0L, entity.createdAt)
    }
}
