package fulk.evilcorp.dailyreflection.data

import fulk.evilcorp.dailyreflection.model.ReflectionData
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface ReflectionApi {
    @GET("reflection/{version}")
    suspend fun getReflection(@Path("version") version: String): ReflectionData

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
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
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