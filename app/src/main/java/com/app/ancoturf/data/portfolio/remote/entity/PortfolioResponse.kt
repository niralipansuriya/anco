package com.app.ancoturf.data.portfolio.remote.entity


import com.google.gson.annotations.SerializedName

data class PortfolioResponse(
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
        @SerializedName("address")
        var address: String = "",
        @SerializedName("budget")
        var budget: String = "",
        @SerializedName("city")
        var city: String = "",
        @SerializedName("featured_image")
        var featuredImage: FeaturedImage = FeaturedImage(),
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("portfolio_product_count")
        var portfolioProductCount: Int = 0,
        @SerializedName("project_description")
        var projectDescription: String = "",
        @SerializedName("project_name")
        var projectName: String = "",
        @SerializedName("status")
        var status: String = "",
        @SerializedName("user_id")
        var userId: Int = 0
     /*   @SerializedName("sharingUrl")
        var sharingUrl: String = ""*/
    ) {
        data class FeaturedImage(
            @SerializedName("featured")
            var featured: Boolean = false,
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("image_url")
            var imageUrl: String = "",
            @SerializedName("portfolio_id")
            var portfolioId: Int = 0
        )
    }
}