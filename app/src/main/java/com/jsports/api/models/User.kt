package com.jsports.api.models

class User(
    val fullname: String,
    val gender: String,
    val username: String,
    val email: String,
    val roles: List<String>,
    val height:Float?,
    val weight:Float?,
    val born:String?,
    val country:String?,
    val sports:List<Sport>
)