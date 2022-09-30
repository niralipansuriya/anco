package com.app.ancoturf.data.quote.remote.entity.response


import com.google.gson.annotations.SerializedName

data class DuplicateQuoteResponse(
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("custom_products")
    var customProducts: List<CustomProduct> = listOf(),
    @SerializedName("customer_id")
    var customerId: String = "",
    @SerializedName("delivery_cost")
    var deliveryCost: Any? = Any(),
    @SerializedName("delivery_date")
    var deliveryDate: Any? = Any(),
    @SerializedName("emails")
    var emails: Any? = Any(),
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("products")
    var products: List<Any> = listOf(),
    @SerializedName("reference_number")
    var referenceNumber: String = "",
    @SerializedName("relations")
    var relations: List<Any> = listOf(),
    @SerializedName("sent_date")
    var sentDate: Any? = Any(),
    @SerializedName("status")
    var status: String = "",
    @SerializedName("total_cost")
    var totalCost: String = "",
    @SerializedName("user_id")
    var userId: String = ""
) {
    data class CustomProduct(
        @SerializedName("descriptions")
        var descriptions: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("image_url")
        var imageUrl: String = "",
        @SerializedName("name")
        var name: String = "",
        @SerializedName("pivot")
        var pivot: Pivot = Pivot(),
        @SerializedName("price")
        var price: String = "",
        @SerializedName("user_id")
        var userId: String = ""
    ) {
        data class Pivot(
            @SerializedName("custom_product_id")
            var customProductId: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("product_price")
            var productPrice: String = "",
            @SerializedName("quantity")
            var quantity: String = "",
            @SerializedName("quote_id")
            var quoteId: String = ""
        )
    }
}