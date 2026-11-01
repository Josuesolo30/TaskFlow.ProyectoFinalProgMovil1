package com.josue.taskflow.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) para deserializar tareas recibidas de JSONPlaceholder.
 */
@Serializable
data class TaskDto(
    @SerialName("userId")
    val userId: Int,

    @SerialName("id")
    val id: Int,

    @SerialName("title")
    val title: String,

    @SerialName("completed")
    val completed: Boolean
)
