package com.jsports.api.models.requests

import com.jsports.api.models.responses.CyclicResult

class EventRequest(
    val sportsDiscipline: String,
    val comment: String?,
    val result: CyclicResult
)