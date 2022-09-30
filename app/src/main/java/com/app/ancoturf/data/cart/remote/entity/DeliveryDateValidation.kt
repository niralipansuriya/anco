package com.app.ancoturf.data.cart.remote.entity


import com.google.gson.annotations.SerializedName

data class DeliveryDateValidation(
    @SerializedName("Valid")
    var valid: Int = 0
)