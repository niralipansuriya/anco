package com.app.ancoturf.data.portfolio.remote.entity


import com.google.gson.annotations.SerializedName

data class QuoteAncoProductRequest(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("qty")
    var qty: Int? = 0,
    @SerializedName("margin")
    var margin: Int = 0
)