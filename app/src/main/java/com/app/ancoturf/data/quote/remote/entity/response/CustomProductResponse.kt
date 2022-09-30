package com.app.ancoturf.data.quote.remote.entity.response


import com.google.gson.annotations.SerializedName

data class CustomProductResponse(
    @SerializedName("descriptions")
    var descriptions: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("price")
    var price: String = "",
    @SerializedName("image_url")
    var imageUrl: String = "",
    @SerializedName("user_id")
    var userId: Int = 0,
    var qty: Int = 0
)