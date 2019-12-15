package com.jsports.api

import com.jsports.api.models.User
import com.jsports.api.models.requests.LoginRequest
import com.jsports.api.models.requests.RegisterRequest
import com.jsports.api.models.responses.BooleanResponse
import com.jsports.api.models.responses.LoginResponse
import com.jsports.api.models.responses.MessageResponse
import com.jsports.api.models.responses.SportStatisticsResponse
import retrofit2.Call
import retrofit2.http.*

const val usersBase = "/users"
const val sportsBase = "/sports/stats"

interface Api {
    @GET("$usersBase/tokenValid")
    fun isTokenValid(@Query("token") token: String): Call<BooleanResponse>


    @POST("$usersBase/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>


    @POST("$usersBase/resetPassword")
    fun resetPassword(
        @Query("email") email:String
    ): Call<MessageResponse>


    @POST("$usersBase/register")
    fun register(
        @Body registerRequest:RegisterRequest
    ): Call<MessageResponse>

    @POST("$usersBase/logout")
    fun logout(): Call<MessageResponse>

    @GET("$sportsBase/all")
    fun getSportStatistics(): Call<List<SportStatisticsResponse>>

    @GET("$usersBase/current")
    fun getCurrentUserProfile(): Call<User>
}