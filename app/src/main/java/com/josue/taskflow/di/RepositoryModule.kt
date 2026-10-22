package com.josue.taskflow.di

import com.josue.taskflow.data.DefaultSettingsRepository
import com.josue.taskflow.data.DefaultTareaRepository
import com.josue.taskflow.domain.repository.SettingsRepository
import com.josue.taskflow.domain.repository.TareaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTareaRepository(
        defaultTareaRepository: DefaultTareaRepository
    ): TareaRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        defaultSettingsRepository: DefaultSettingsRepository
    ): SettingsRepository
}
