package com.app.ancoturf.data.afterpay.remote.entity.request


import com.google.gson.annotations.SerializedName

data class CaptureRequest(
    @SerializedName("merchantReference")
    var merchantReference: String = "",
    @SerializedName("token")
    var token: String = ""
)