package com.app.ancoturf.data.quote.remote.entity


import com.google.gson.annotations.SerializedName

data class QuoteProducts(
    @SerializedName("descriptions")
    var descriptions: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("image_url")
    var imageUrl: String? = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("price")
    var price: String = "",
    @SerializedName("user_id")
    var userId: Int = 0,
    @SerializedName("feature_image_url")
    var featureImageUrl: String = "",
    @SerializedName("product_category_id")
    var productCategoryId: Int = 0,
    @SerializedName("product_unit")
    var productUnit: String = "",
    @SerializedName("product_unit_id")
    var productUnitId: Int = 0,
    var unit: String = "",
    var qty: Int? = 0,
    var ancoProduct: Boolean = false,
    var margin: Int = 0
)
