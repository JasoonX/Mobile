package com.jsports.api.models.requests

import com.jsports.api.models.Sport


class RegisterRequest(
    val fullname: String,
    val gender: String,
    val username: String,
    val email: String,
    val password: String,
    val height:Float? = null,
    val weight:Float? = null,
    val born:String? = null,
    val country:String? = null,
    val sports:List<Sport>? = null,
    val roles:List<String> = listOf("ROLE_CLIENT")
)