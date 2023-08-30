package com.example.githubapp.data.di

import android.content.Context
import androidx.room.Room
import com.example.githubapp.data.local.room.UserDao
import com.example.githubapp.data.local.room.UserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DBModule {

    @Provides
    fun provideUserDao(userDatabase: UserDatabase): UserDao = userDatabase.userDao()

    @Provides
    @Singleton
    fun provideUserDatabase(@ApplicationContext context: Context): UserDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            UserDatabase::class.java,
            "user_database"
        ).build()
    }
}