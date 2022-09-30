package com.app.ancoturf.data.portfolio.remote.entity


import com.google.gson.annotations.SerializedName

data class AddEditPortfolioResponse(
    @SerializedName("address")
    var address: Any? = Any(),
    @SerializedName("budget")
    var budget: String = "",
    @SerializedName("city")
    var city: String = "",
    @SerializedName("custom_products")
    var customProducts: List<CustomProduct> = listOf(),
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("portfolio_images")
    var portfolioImages: List<PortfolioImage> = listOf(),
    @SerializedName("products")
    var products: List<Product> = listOf(),
    @SerializedName("project_description")
    var projectDescription: String = "",
    @SerializedName("project_name")
    var projectName: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("user_id")
    var userId: Int = 0
) {
    data class CustomProduct(
        @SerializedName("description")
        var description: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("image_url")
        var imageUrl: Any? = Any(),
        @SerializedName("name")
        var name: String = "",
        @SerializedName("pivot")
        var pivot: Pivot = Pivot(),
        @SerializedName("price")
        var price: String = "",
        @SerializedName("user_id")
        var userId: Int = 0
    ) {
        data class Pivot(
            @SerializedName("custom_product_id")
            var customProductId: Int = 0,
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("portfolio_id")
            var portfolioId: Int = 0,
            @SerializedName("quantity")
            var quantity: Int = 0
        )
    }

    data class Product(
        @SerializedName("average_rating")
        var averageRating: String = "",
        @SerializedName("cms_privilege_id")
        var cmsPrivilegeId: Int = 0,
        @SerializedName("default_price")
        var defaultPrice: String = "",
        @SerializedName("feature_image_url")
        var featureImageUrl: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("in_stock")
        var inStock: Int = 0,
        @SerializedName("minimum_quantity")
        var minimumQuantity: Int = 0,
        @SerializedName("name")
        var name: String = "",
        @SerializedName("pivot")
        var pivot: Pivot = Pivot(),
        @SerializedName("price")
        var price: String = "",
        @SerializedName("product_benefit_title")
        var productBenefitTitle: Any? = Any(),
        @SerializedName("product_category_id")
        var productCategoryId: Int = 0,
        @SerializedName("product_unit")
        var productUnit: String = "",
        @SerializedName("product_unit_id")
        var productUnitId: Int = 0,
        @SerializedName("purchase_count")
        var purchaseCount: Int = 0,
        @SerializedName("review_count")
        var reviewCount: Int = 0
    ) {
        data class Pivot(
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("portfolio_id")
            var portfolioId: Int = 0,
            @SerializedName("product_id")
            var productId: Int = 0,
            @SerializedName("quantity")
            var quantity: Int = 0
        )
    }

    data class PortfolioImage(
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