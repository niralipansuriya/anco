package com.app.ancoturf.data.portfolio.remote.entity


import com.google.gson.annotations.SerializedName

data class NonAncoProductRequest(
    @SerializedName("Description")
    var descriptions: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("price")
    var price: Float = 0f,
    @SerializedName("qty")
    var qty: Int = 0
)