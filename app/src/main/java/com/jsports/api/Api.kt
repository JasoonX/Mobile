package com.jsports.api

import com.jsports.api.responses.BooleanResponse
import retrofit2.Call
import retrofit2.http.GET

const val usersBase = "/users"

interface Api {
    @GET("$usersBase/tokenValid")
    fun isTokenValid(): Call<BooleanResponse>
}