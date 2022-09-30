package com.app.ancoturf.data.common

interface ApiCallback {
    fun onSuccess(type: String, response: String)
    fun onError(type: String, errorMessage: String)
}