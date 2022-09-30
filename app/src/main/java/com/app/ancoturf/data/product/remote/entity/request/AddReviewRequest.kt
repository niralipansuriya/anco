package com.app.ancoturf.data.product.remote.entity.request


import com.google.gson.annotations.SerializedName

data class AddReviewRequest(
    @SerializedName("email")
    var email: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("product_id")
    var productId: Int = 0,
    @SerializedName("rating")
    var rating: Int = 0,
    @SerializedName("review_text")
    var reviewText: String = "",
    @SerializedName("user_id")
    var userId: Int = 0
)