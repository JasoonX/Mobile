package com.jsports.api.responses

class BooleanResponse(result: Boolean) {
    private val result:Boolean = result

    fun isResult():Boolean{
        return this.result
    }
}