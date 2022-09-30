package com.app.ancoturf.data.search.remote.entity.response


import com.google.gson.annotations.SerializedName

data class RequestProductResponse(
    @SerializedName("name")
    var name: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("email")
    var email: String = "",
    @SerializedName("product_description")
    var productDescription: String = "",
    @SerializedName("product_url")
    var productUrl: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("user_id")
    var userId: Int = 0
)