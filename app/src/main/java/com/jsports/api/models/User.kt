package com.jsports.api.models

import java.io.Serializable

class User(
    val fullname: String,
    val gender: String,
    val username: String,
    val email: String,
    val height:Float?,
    val weight:Float?,
    val born:String?,
    val roles:List<String>,
    val country:String?,
    val sports:List<Sport>
) : Serializable