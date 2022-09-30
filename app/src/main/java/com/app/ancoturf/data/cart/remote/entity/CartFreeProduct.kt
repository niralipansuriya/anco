package com.app.ancoturf.data.cart.remote.entity

import com.google.gson.annotations.SerializedName
data class CartFreeProduct(
    @SerializedName("id")
    var product_id: Int? = 0,
    @SerializedName("feature_image_url")
    val feature_img_url: String? = null,
    @SerializedName("qty")
    var qty: Int = 0,
    @SerializedName("price")
    val price: Float? = null,
    @SerializedName("product_category_id")
    val product_category_id: Int? = null,
    @SerializedName("product_name")
    val product_name: String? = null,
    @SerializedName("product_unit")
    val product_unit: String? = null,
    @SerializedName("product_unit_id")
    val product_unit_id: Int? = null,
    @SerializedName("base_total_price")
    var base_total_price: Float? = 0f,
    @SerializedName("is_turf")
    var is_turf: Int? = 0,
    @SerializedName("total_price")
    var total_price: Float? = 0f,
    @SerializedName("product_redeemable_against_credit")
    var product_redeemable_against_credit: Boolean = false,
    @SerializedName("free_product")
    var free_product: Int = 0,
    @SerializedName("free_product_qty")
    var free_product_qty: Int = 0,
    @SerializedName("free_products")
    var free_products: FreeProducts = FreeProducts()){
    data class FreeProducts(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("minimum_qty")
        var minimum_qty: Int = 0,
        @SerializedName("feature_image_url")
        var feature_image_url: String = "",
        @SerializedName("price")
        var price: Float = 0f,
        @SerializedName("product_name")
        var productName: String = "",
        @SerializedName("product_category_id")
        var productCategoryId: Int = 0
    )


}