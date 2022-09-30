package com.app.ancoturf.data.account.remote.entity.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("password")
    var password: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("device_token")
    var deviceToken: String = "",
    @SerializedName("device_type")
    var deviceType: String = "Android"

)