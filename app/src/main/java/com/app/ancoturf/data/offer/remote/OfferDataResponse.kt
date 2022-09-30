package com.app.ancoturf.data.offer.remote


import com.google.gson.annotations.SerializedName

data class OfferDataResponse(
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
        @SerializedName("content")
        var content: String = "",
        @SerializedName("coupon")
        var coupon: Coupon = Coupon(),
        @SerializedName("coupon_id")
        var couponId: String = "",
        @SerializedName("end_date")
        var endDate: String = "",
        @SerializedName("footer_image_url")
        var footerImageUrl: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("image_url")
        var imageUrl: String = "",
        @SerializedName("start_date")
        var startDate: String = "",
        @SerializedName("status")
        var status: String = "",
        @SerializedName("title")
        var title: String = ""
    ) {
        data class Coupon(
            @SerializedName("code")
            var code: String = "",
            @SerializedName("discount")
            var discount: String = "",
            @SerializedName("discount_type")
            var discountType: String = "",
            @SerializedName("expiry_date")
            var expiryDate: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("item_ids")
            var itemIds: String = "",
            @SerializedName("item_type")
            var itemType: String = "",
            @SerializedName("name")
            var name: String = "",
            @SerializedName("start_date")
            var startDate: String = "",
            @SerializedName("status")
            var status: String = "",
            @SerializedName("threshold")
            var threshold: String = "",
            @SerializedName("user_role_ids")
            var userRoleIds: String = "",
            @SerializedName("user_type")
            var userType: String = ""
        )
    }
}