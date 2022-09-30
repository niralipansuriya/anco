package com.app.ancoturf.data.quote.remote.entity.response


import com.google.gson.annotations.SerializedName

data class CustomerDataResponse(
    @SerializedName("CustomerID")
    var customerId: Int = 0,
    @SerializedName("customer_name")
    var customerName: String = "",
    @SerializedName("customer_address")
    var customerAddress: String = "",
    @SerializedName("customer_email")
    var customerEmail: String = "",
    @SerializedName("customer_mobile")
    var customerMobile: String = "",
    @SerializedName("customer_phone")
    var customerPhone: String = ""
)