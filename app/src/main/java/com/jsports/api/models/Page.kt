package com.jsports.api.models

class Page<T>(
    val content: List<T>,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val size:Int,
    val totalPages:Int
)