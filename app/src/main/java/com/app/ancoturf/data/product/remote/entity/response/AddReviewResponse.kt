package com.app.ancoturf.data.product.remote.entity.response


import com.google.gson.annotations.SerializedName

data class AddReviewResponse(
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("product_id")
    var productId: String = "",
    @SerializedName("rating")
    var rating: String = "",
    @SerializedName("review_text")
    var reviewText: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("user_id")
    var userId: Int = 0
)