package com.josue.taskflow.domain.usecase

import com.josue.taskflow.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerFiltroGuardadoUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<String> {
        return settingsRepository.getFiltroGuardadoStream()
    }
}
