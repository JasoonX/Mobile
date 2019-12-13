package com.jsports.api

import android.util.Base64
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory






class RetrofitClient private constructor() {
    private val retrofit: Retrofit
    val api: Api
        get() = retrofit.create(Api::class.java)

    init {

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .method(original.method(), original.body())
                val request = requestBuilder.build()
                chain.proceed(request)
            }.build()

        val gson = GsonBuilder()
            .setLenient()
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    companion object {
        private const val BASE_URL = "http://localhost:8080"
        private var mInstance: RetrofitClient? = null
        val instance: RetrofitClient
            @Synchronized get() {
                if (mInstance == null) {
                    mInstance = RetrofitClient()
                }
                return mInstance as RetrofitClient
            }
    }
}