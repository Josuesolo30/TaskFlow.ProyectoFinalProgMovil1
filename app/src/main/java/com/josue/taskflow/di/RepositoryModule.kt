package com.josue.taskflow.di

import com.josue.taskflow.data.repository.settings.DefaultSettingsRepository
import com.josue.taskflow.data.repository.task.DefaultTaskRepository
import com.josue.taskflow.domain.repository.settings.SettingsRepository
import com.josue.taskflow.domain.repository.task.TaskRepository
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
    abstract fun bindTaskRepository(
        defaultTaskRepository: DefaultTaskRepository
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        defaultSettingsRepository: DefaultSettingsRepository
    ): SettingsRepository
}
