package com.josue.taskflow.domain.usecase

import com.josue.taskflow.domain.repository.SettingsRepository
import javax.inject.Inject

class GuardarFiltroUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(filtroNombre: String) {
        settingsRepository.guardarFiltro(filtroNombre)
    }
}
