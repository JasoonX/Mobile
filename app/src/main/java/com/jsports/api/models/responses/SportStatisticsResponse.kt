package com.jsports.api.models.responses

class SportStatisticsResponse(
    val discipline: String,
    val people: Int,
    val males: Int,
    val females: Int,
    val eventsCount: Int,
    val userEventsCount:Int,
    val userEventsPercent:Double
)