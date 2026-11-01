package com.josue.taskflow.di

import android.content.Context
import com.josue.taskflow.data.local.database.TaskFlowDatabase
import com.josue.taskflow.data.local.database.dao.TaskDao
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
    fun provideTaskDao(database: TaskFlowDatabase): TaskDao {
        return database.taskDao()
    }
}
