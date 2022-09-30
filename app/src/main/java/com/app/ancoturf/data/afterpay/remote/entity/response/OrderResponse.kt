package com.app.ancoturf.data.afterpay.remote.entity.response


import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("errorCode")
    var errorCode: String = "",
    @SerializedName("errorId")
    var errorId: String = "",
    @SerializedName("expires")
    var expires: String = "",
    @SerializedName("httpStatusCode")
    var httpStatusCode: String = "",
    @SerializedName("message")
    var message: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("token")
    var token: String = ""
)