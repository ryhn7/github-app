package com.example.githubapp.data.di

import android.content.Context
import com.example.githubapp.data.remote.retrofit.ApiConfig
import com.example.githubapp.data.remote.retrofit.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    fun provideApiService(@ApplicationContext context: Context): ApiService =
        ApiConfig.getApiService(context)
}