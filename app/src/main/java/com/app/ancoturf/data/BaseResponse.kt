package com.app.ancoturf.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    @Expose
    @SerializedName("message")
    val message: String? = "",
    @Expose
    @SerializedName("success")
    var success: Boolean = false,
    @Expose
    @SerializedName("data")
    val data: T? = null,
    @SerializedName("order_max_price")
    var order_max_price: String = "",
    @SerializedName("quote_max_price")
    var quote_max_price: String = ""
)