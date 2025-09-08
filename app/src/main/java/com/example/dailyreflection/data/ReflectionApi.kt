package com.example.dailyreflection.data

import com.example.dailyreflection.model.ReflectionData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ReflectionApi {
    @GET("reflection")
    suspend fun getReflection(): ReflectionData

    companion object {
        private const val ACCESS_KEY = "m9Op[3w4IRSgLVzv_}J>T5Y^q]_7?B"

        fun create(): ReflectionApi {
            val headerInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val requestWithHeaders = originalRequest.newBuilder()
                    .addHeader("accesskey", ACCESS_KEY)
                    .build()
                chain.proceed(requestWithHeaders)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl("https://fulk-bible.vercel.app/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ReflectionApi::class.java)
        }
    }
}