package com.josue.taskflow.domain.model.sync

/**
 * Representa el estado de sincronización remota con la API REST.
 */
sealed interface SyncStatus {
    data object Idle : SyncStatus
    data object Loading : SyncStatus
    data object Success : SyncStatus
    data class Error(val message: String) : SyncStatus
}
