package com.jsports.helpers

import okhttp3.ResponseBody


interface RetrofitCallback<T> {
    fun onSuccess(value: T)
    fun onServerError(error:ResponseBody)
    fun onError(throwable: Throwable)
}