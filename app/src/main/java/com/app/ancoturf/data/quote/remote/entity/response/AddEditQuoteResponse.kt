package com.app.ancoturf.data.quote.remote.entity.response


import com.google.gson.annotations.SerializedName

data class AddEditQuoteResponse(
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("custom_products")
    var customProducts: List<CustomProduct> = listOf(),
    @SerializedName("customer_id")
    var customerId: Int = 0,
    @SerializedName("delivery_cost")
    var deliveryCost: Any? = Any(),
    @SerializedName("delivery_date")
    var deliveryDate: Any? = Any(),
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("products")
    var products: List<Product> = listOf(),
    @SerializedName("reference_number")
    var referenceNumber: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("total_cost")
    var totalCost: Float = 0f,
    @SerializedName("user_id")
    var userId: Int = 0,
    @SerializedName("users")
    var users: Users = Users()
) {
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
            @SerializedName("margin")
            var margin: Int = 0,
            @SerializedName("product_id")
            var productId: Int = 0,
            @SerializedName("product_price")
            var productPrice: String = "",
            @SerializedName("quantity")
            var quantity: Int = 0,
            @SerializedName("quote_id")
            var quoteId: Int = 0
        )
    }

    data class Users(
        @SerializedName("business")
        var business: Business = Business(),
        @SerializedName("device_token")
        var deviceToken: Any? = Any(),
        @SerializedName("device_type")
        var deviceType: String = "",
        @SerializedName("email")
        var email: String = "",
        @SerializedName("first_name")
        var firstName: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("id_cms_privileges")
        var idCmsPrivileges: String = "",
        @SerializedName("image_url")
        var imageUrl: String = "",
        @SerializedName("last_name")
        var lastName: String = "",
        @SerializedName("phone_number")
        var phoneNumber: Any? = Any(),
        @SerializedName("status")
        var status: String = ""
    ) {
        data class Business(
            @SerializedName("abn")
            var abn: String = "",
            @SerializedName("address")
            var address: String = "",
            @SerializedName("business_name")
            var businessName: String = "",
            @SerializedName("contact_name")
            var contactName: String = "",
            @SerializedName("disclaimer")
            var disclaimer: String = "",
            @SerializedName("email")
            var email: String = "",
            @SerializedName("gst")
            var gst: Any? = Any(),
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("logo_url")
            var logoUrl: String = "",
            @SerializedName("mobile_number")
            var mobileNumber: String = "",
            @SerializedName("payment_terms")
            var paymentTerms: String = "",
            @SerializedName("phone_number")
            var phoneNumber: String? = "",
            @SerializedName("registered_for_gst")
            var registeredForGst: String = "",
            @SerializedName("user_id")
            var userId: String = "",
            @SerializedName("web_url")
            var webUrl: String = ""
        )
    }

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
            @SerializedName("quantity")
            var quantity: Int = 0,
            @SerializedName("quote_id")
            var quoteId: Int = 0
        )
    }
}