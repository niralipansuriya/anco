package com.app.ancoturf.data.cart.remote.entity.request

import com.google.gson.annotations.SerializedName

data class CartProductRequest(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("qty")
    var qty: Int? = 0)