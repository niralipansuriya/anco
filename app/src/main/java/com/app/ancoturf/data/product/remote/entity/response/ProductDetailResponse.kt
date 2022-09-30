package com.app.ancoturf.data.product.remote.entity.response


import com.google.gson.annotations.SerializedName

data class ProductDetailResponse(
    @SerializedName("feature_image_url")
    var featureImageUrl: String = "",
    @SerializedName("suggest_image_url")
    var suggestImageUrl: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("in_stock")
    var inStock: Int = 0,
    @SerializedName("minimum_quantity")
    var minimumQuantity: Int = 0,
    @SerializedName("price")
    var price: Float = 0f,
    @SerializedName("product_benefit_title")
    var productBenefitTitle: String? = "",
    @SerializedName("product_benefits")
    var productBenefits: List<ProductBenefits> = listOf(),
    @SerializedName("product_category")
    var productCategory: ProductCategory = ProductCategory(),
    @SerializedName("free_products")
    var free_products: FreeProducts = FreeProducts(),
    @SerializedName("product_category_id")
    var productCategoryId: Int = 0,
    @SerializedName("product_details")
    var productDetails: List<ProductDetail> = listOf(),
    @SerializedName("product_id")
    var productId: Int = 0,
    @SerializedName("product_images")
    var productImages: List<ProductImage> = listOf(),
    @SerializedName("product_name")
    var productName: String = "",
    @SerializedName("product_reviews")
    var productReviews: List<ProductReview> = listOf(),
    @SerializedName("product_tags")
    var productTags: List<ProductTag> = listOf(),
    @SerializedName("product_unit")
    var productUnit: String = "",
    @SerializedName("product_unit_id")
    var productUnitId: Int = 0,
    @SerializedName("free_product")
    var free_product: Int = 0,
    @SerializedName("free_product_qty")
    var free_product_qty: Int = 0,
    @SerializedName("free_product_description")
    var free_product_description: String = "",
    var qty: Int = 0
) {
    data class ProductDetail(
        @SerializedName("meta_key")
        var metaKey: String = "",
        @SerializedName("meta_value")
        var metaValue: String = "",
        @SerializedName("title")
        var title: String = "",
        @SerializedName("type")
        var type: String = "",
        var open: Boolean = false
    )

    data class ProductBenefits(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("image_url")
        var imageUrl: String = "",
        @SerializedName("display_name")
        var displayName: String = ""
    )

    data class ProductReview(
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("product_id")
        var productId: Int = 0,
        @SerializedName("rating")
        var rating: Int = 0,
        @SerializedName("review_text")
        var reviewText: String = "",
        @SerializedName("status")
        var status: String = "",
        @SerializedName("user")
        var user: User = User(),
        @SerializedName("user_id")
        var userId: Int = 0
    ) {
        data class User(
            @SerializedName("device_token")
            var deviceToken: Any? = Any(),
            @SerializedName("device_type")
            var deviceType: Any? = Any(),
            @SerializedName("email")
            var email: String = "",
            @SerializedName("first_name")
            var firstName: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("id_cms_privileges")
            var idCmsPrivileges: Int = 0,
            @SerializedName("image_url")
            var imageUrl: String = "",
            @SerializedName("last_name")
            var lastName: String = "",
            @SerializedName("phone_number")
            var phoneNumber: String = "",
            @SerializedName("status")
            var status: String = ""
        )
    }

    data class ProductImage(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("image_url")
        var imageUrl: String = "",
        @SerializedName("product_id")
        var productId: Int = 0
    )

    data class ProductTag(
        @SerializedName("display_name")
        var displayName: String = "",
        @SerializedName("slug")
        var slug: String = ""
    )

    data class ProductCategory(
        @SerializedName("display_name")
        var displayName: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("image_url")
        var imageUrl: String = "",
        @SerializedName("slug")
        var slug: String = ""
    )

    data class FreeProducts(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("minimum_qty")
        var minimum_qty: Int = 0,
        @SerializedName("free_product_description")
        var free_product_description: String = "",
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