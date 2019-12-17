package com.jsports.api

import com.jsports.api.models.Page
import com.jsports.api.models.User
import com.jsports.api.models.requests.EventRequest
import com.jsports.api.models.requests.LoginRequest
import com.jsports.api.models.requests.RegisterRequest
import com.jsports.api.models.responses.*
import retrofit2.Call
import retrofit2.http.*

const val usersBase = "/users"
const val sportsBase = "/sports/stats"
const val eventsBase = "/events"

interface Api {
    @GET("$usersBase/tokenValid")
    fun isTokenValid(@Query("token") token: String): Call<BooleanResponse>


    @POST("$usersBase/login")
    fun login(
        @Body request: LoginRequest
    ): Call<LoginResponse>


    @POST("$usersBase/resetPassword")
    fun resetPassword(
        @Query("email") email: String
    ): Call<MessageResponse>


    @POST("$usersBase/register")
    fun register(
        @Body registerRequest: RegisterRequest
    ): Call<MessageResponse>

    @POST("$usersBase/logout")
    fun logout(): Call<MessageResponse>

    @GET("$sportsBase/all")
    fun getSportStatistics(): Call<List<SportStatisticsResponse>>

    @GET("$usersBase/current")
    fun getCurrentUserProfile(): Call<User>

    @PUT("$usersBase/current")
    fun updateProfile(@Body request: User): Call<MessageResponse>

    @GET("$eventsBase/page/{page}")
    fun getEvents(
        @Path("page") page: Int,
        @Query("sportsDiscipline") sportsDiscipline: String,
        @Query("orderBy") orderBy:String = "dateTime",
        @Query("direction") direction:String = "DESC"
    ): Call<Page<EventResponse>>

    @DELETE("$eventsBase/")
    fun deleteEvent(@Query("id") id:Long) : Call<MessageResponse>

    @POST("$eventsBase/")
    fun addEvent(@Body request: EventRequest):Call<MessageResponse>
}