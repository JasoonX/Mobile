package com.jsports.api

import com.jsports.api.requests.LoginRequest
import com.jsports.api.responses.BooleanResponse
import com.jsports.api.responses.LoginResponse
import retrofit2.Call
import retrofit2.http.*

const val usersBase = "/users"

interface Api {
    @GET("$usersBase/tokenValid")
    fun isTokenValid(@Query("token") token: String): Call<BooleanResponse>

    @Headers("Content-Type: application/json")
    @POST("$usersBase/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>
}