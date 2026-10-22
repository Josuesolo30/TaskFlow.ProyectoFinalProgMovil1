package com.josue.taskflow.di

import android.content.Context
import com.josue.taskflow.data.local.TareaDao
import com.josue.taskflow.data.local.TaskFlowDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTaskFlowDatabase(
        @ApplicationContext context: Context
    ): TaskFlowDatabase {
        return TaskFlowDatabase.getDatabase(context)
    }

    @Provides
    fun provideTareaDao(database: TaskFlowDatabase): TareaDao {
        return database.tareaDao()
    }
}
