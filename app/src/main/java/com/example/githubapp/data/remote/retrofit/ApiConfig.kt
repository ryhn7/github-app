package com.example.githubapp.data.remote.retrofit

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.githubapp.Utils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(context: Context): ApiService {

            val authInterceptor = Interceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${Utils.TOKEN}")
                    .build()
                chain.proceed(newRequest)
            }

            val githubClient = OkHttpClient.Builder()
                .addInterceptor(ChuckerInterceptor(context))
                .addInterceptor(authInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(githubClient)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}