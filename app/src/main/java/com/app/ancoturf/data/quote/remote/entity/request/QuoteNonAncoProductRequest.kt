package com.app.ancoturf.data.portfolio.remote.entity


import com.google.gson.annotations.SerializedName

data class QuoteNonAncoProductRequest(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("qty")
    var qty: Int? = 0
)