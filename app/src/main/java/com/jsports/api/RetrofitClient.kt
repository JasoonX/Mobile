package com.jsports.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.jsports.storage.SharedPrefManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitClient(mCtx: Context,val lang:String) {
    private val retrofit: Retrofit
    val api: Api
        get() = retrofit.create(Api::class.java)

    init {

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .method(original.method(), original.body())
                val request = requestBuilder
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept-Language",lang)
                    .build()
                chain.proceed(request)
            }.build()

        val json = GsonBuilder()
            .setLenient()
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(json))
            .client(okHttpClient)
            .build()
    }

    companion object {
        private const val BASE_URL = "http://10.0.2.2:8080"
        private var mInstance: RetrofitClient? = null
        @Synchronized
        fun getInstance(mCtx:Context) : RetrofitClient{
            var lang = SharedPrefManager.getInstance(mCtx).getLanguage("en")
            if(lang == "uk"){
                lang = "ua"
            }
            if (mInstance == null || mInstance!!.lang != lang!!) {
                mInstance = RetrofitClient(mCtx,lang!!)
            }
            return mInstance as RetrofitClient
        }
    }
}