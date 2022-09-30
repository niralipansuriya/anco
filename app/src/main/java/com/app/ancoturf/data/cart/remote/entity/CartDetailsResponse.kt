package com.app.ancoturf.data.cart.remote.entity


import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.data.product.remote.entity.response.RelatedProductsResponse
import com.google.gson.annotations.SerializedName

data class CartDetailsResponse(
    @SerializedName("credit_discount")
    var creditDiscount: Float = 0f,
    @SerializedName("products")
    var products: List<ProductDB> = listOf(),
    @SerializedName("coupons")
    var coupons: List<CouponDB> = listOf(),
    @SerializedName("related_products")
    var relatedProducts: List<RelatedProductsResponse.Data> = listOf(),
    @SerializedName("user")
    var user: UserInfo = UserInfo(),
    @SerializedName("shipping_price")
    var shippingPrice: Float = 0f,
    @SerializedName("sub_total_price")
    var subTotalPrice: Float = 0f,
    @SerializedName("total_cart_price")
    var totalCartPrice: Float = 0f,
    @SerializedName("total_discount")
    var totalDiscount: Float = 0f
) {
    data class RelatedProduct(
        @SerializedName("feature_image_url")
        var featureImageUrl: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("in_stock")
        var inStock: String = "",
        @SerializedName("minimum_quantity")
        var minimumQuantity: String = "",
        @SerializedName("price")
        var price: String = "",
        @SerializedName("product_category_id")
        var productCategoryId: String = "",
        @SerializedName("product_name")
        var productName: String = "",
        @SerializedName("product_unit")
        var productUnit: String = "",
        @SerializedName("product_unit_id")
        var productUnitId: String = ""
    )

    data class Product(
        @SerializedName("base_total_price")
        var baseTotalPrice: Int = 0,
        @SerializedName("dimension_height")
        var dimensionHeight: String = "",
        @SerializedName("dimension_length")
        var dimensionLength: String = "",
        @SerializedName("dimension_width")
        var dimensionWidth: String = "",
        @SerializedName("feature_image_url")
        var featureImageUrl: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("is_turf")
        var isTurf: String = "",
        @SerializedName("price")
        var price: String = "",
        @SerializedName("product_category_id")
        var productCategoryId: String = "",
        @SerializedName("product_name")
        var productName: String = "",
        @SerializedName("product_unit")
        var productUnit: String = "",
        @SerializedName("product_unit_id")
        var productUnitId: String = "",
        @SerializedName("qty")
        var qty: Int = 0,
        @SerializedName("quantity")
        var quantity: Int = 0,
        @SerializedName("total_price")
        var totalPrice: Int = 0,
        @SerializedName("weight")
        var weight: String = ""
    )
    data class ProductForFree(
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
        var free_products: FreeProducts = FreeProducts()
    )

    data class FreeProducts(
        var qty: Int = 0,
        var id: Int = 0,
        var minimum_qty: Int = 0,
        var feature_image_url: String = "",
        var price: Float = 0f,
        @SerializedName("product_name")
        var productName: String = "",
        @SerializedName("product_category_id")
        var productCategoryId: Int = 0
    )

}