package com.app.ancoturf.data.quote.remote.entity.response


import com.google.gson.annotations.SerializedName

data class CustomersDataResponse(
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
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("customer_address")
        var customerAddress: String = "",
        @SerializedName("customer_email")
        var customerEmail: String = "",
        @SerializedName("customer_mobile")
        var customerMobile: String = "",
        @SerializedName("customer_name")
        var customerName: String = "",
        @SerializedName("customer_phone")
        var customerPhone: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("updated_at")
        var updatedAt: String = "",
        @SerializedName("user_id")
        var userId: String = "",
        var selected: Boolean = false
    )
}