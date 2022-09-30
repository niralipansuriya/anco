package com.app.ancoturf.data.product.remote.entity


import com.google.gson.annotations.SerializedName

data class ProductsResponse(
    @SerializedName("data")
    var `data`: List<Data> = listOf(),
    @SerializedName("current_page")
    var currentPage: Int = 0,
    @SerializedName("first_page_url")
    var firstPageUrl: String = "",
    @SerializedName("from")
    var from: Int = 0,
    @SerializedName("next_page_url")
    var nextPageUrl: Any? = Any(),
    @SerializedName("path")
    var path: String = "",
    @SerializedName("per_page")
    var perPage: Int = 0,
    @SerializedName("prev_page_url")
    var prevPageUrl: Any? = Any(),
    @SerializedName("to")
    var to: Int = 0
) {
    data class Data(
        @SerializedName("feature_image_url")
        var featureImageUrl: String = "",
        @SerializedName("in_stock")
        var inStock: Int = 0,
        @SerializedName("minimum_quantity")
        var minimumQuantity: Int = 0,
        @SerializedName("price")
        var price: String = "",
        @SerializedName("product_category_id")
        var productCategoryId: Int = 0,
        @SerializedName("product_id")
        var productId: Int = 0,
        @SerializedName("product_name")
        var productName: String = "",
        @SerializedName("product_unit")
        var productUnit: String = ""
    )
}