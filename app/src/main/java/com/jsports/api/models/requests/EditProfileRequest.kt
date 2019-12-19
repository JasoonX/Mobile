package com.jsports.api.models.requests

class EditProfileRequest(
    val fullname: String?,
    val gender: String?,
    val username: String?,
    val height:Float?,
    val weight:Float?,
    val born:String?,
    val country:String?,
    val sports:List<String>?
)