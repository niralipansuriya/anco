package com.app.ancoturf.data.account.remote.entity.request


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RegisterRequest(

    @Expose
    @SerializedName("abn")
    var abn: String? = null,
    @Expose
    @SerializedName("business_name")
    var businessName: String? = null,
    @Expose
    @SerializedName("email")
    var email: String = "",
    @Expose
    @SerializedName("first_name")
    var firstName: String = "",
    @Expose
    @SerializedName("last_name")
    var lastName: String = "",
    @Expose
    @SerializedName("password")
    var password: String = "",
    @Expose
    @SerializedName("phone_number")
    var phoneNumber: String? = null,
    @SerializedName("device_token")
    var deviceToken: String = "",
    @SerializedName("device_type")
    var deviceType: String = "Android"
)