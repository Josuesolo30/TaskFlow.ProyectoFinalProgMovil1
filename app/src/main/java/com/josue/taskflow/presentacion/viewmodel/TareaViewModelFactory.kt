package com.josue.taskflow.presentacion.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.josue.taskflow.datos.db.TaskFlowDatabase
import com.josue.taskflow.datos.repository.TareaRepositoryImpl
import com.josue.taskflow.dominio.usecase.ActualizarDescripcionUseCase
import com.josue.taskflow.dominio.usecase.AgregarTareaUseCase
import com.josue.taskflow.dominio.usecase.CambiarEstadoTareaUseCase
import com.josue.taskflow.dominio.usecase.ObtenerTareasUseCase

class TareaViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {

    private val applicationContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TareaViewModel::class.java)) {

            val database = TaskFlowDatabase.getDatabase(
                applicationContext
            )

            val repository = TareaRepositoryImpl(
                database.tareaDao()
            )

            return TareaViewModel(
                obtenerTareasUseCase =
                    ObtenerTareasUseCase(repository),

                agregarTareaUseCase =
                    AgregarTareaUseCase(repository),

                cambiarEstadoTareaUseCase =
                    CambiarEstadoTareaUseCase(repository),

                actualizarDescripcionUseCase =
                    ActualizarDescripcionUseCase(repository),

                confirmacionEventosFlow =
                    repository.confirmacionEventosFlow
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}