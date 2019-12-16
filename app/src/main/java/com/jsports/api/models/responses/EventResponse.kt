package com.jsports.api.models.responses

class EventResponse(
    val id: Long,
    val comment: String?,
    val dateTime: String,
    val result: CyclicResult
)

class CyclicResult(val distance: Float, val time: Float)