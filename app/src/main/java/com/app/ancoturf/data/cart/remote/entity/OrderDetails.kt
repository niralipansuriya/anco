package com.app.ancoturf.data.cart.remote.entity


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class OrderDetails(
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("credit_discount")
    var creditDiscount: String = "",
    @SerializedName("delivery_date")
    var deliveryDate: String = "",
    @SerializedName("delivery_status")
    var deliveryStatus: DeliveryStatus = DeliveryStatus(),
    @SerializedName("delivery_status_id")
    var deliveryStatusId: Int = 0,
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("order_status")
    var orderStatus: OrderStatus = OrderStatus(),
    @SerializedName("order_status_id")
    var orderStatusId: Int = 0,
    @SerializedName("products")
    var products: List<Product> = listOf(),
    @SerializedName("reference_number")
    var referenceNumber: String = "",
    @SerializedName("shipping_address_line_1")
    var shippingAddressLine1: String = "",
    @SerializedName("shipping_address_line_2")
    var shippingAddressLine2: Any? = Any(),
    @SerializedName("shipping_city")
    var shippingCity: String = "",
    @SerializedName("shipping_cost")
    var shippingCost: String = "",
    @SerializedName("shipping_country")
    var shippingCountry: String = "",
    @SerializedName("shipping_email")
    var shippingEmail: String = "",
    @SerializedName("shipping_first_name")
    var shippingFirstName: String = "",
    @SerializedName("shipping_last_name")
    var shippingLastName: Any? = Any(),
    @SerializedName("shipping_method")
    var shippingMethod: String = "",
    @SerializedName("shipping_phone")
    var shippingPhone: Any? = Any(),
    @SerializedName("shipping_post_code")
    var shippingPostCode: String = "",
    @SerializedName("shipping_state")
    var shippingState: String = "",
    @SerializedName("shipping_type")
    var shippingType: String = "",
    @SerializedName("sub_total_price")
    var subTotalPrice: String = "",
    @SerializedName("total_cart_price")
    var totalCartPrice: String = "",
    @SerializedName("total_discount")
    var totalDiscount: String = "",
    @SerializedName("user")
    var user: User = User(),
    @SerializedName("user_id")
    var userId: Int = 0
) :Serializable{
    data class OrderStatus(
        @SerializedName("display_name")
        var displayName: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("slug")
        var slug: String = "",
        @SerializedName("status")
        var status: String = ""
    )

    data class DeliveryStatus(
        @SerializedName("display_name")
        var displayName: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("slug")
        var slug: String = "",
        @SerializedName("status")
        var status: String = ""
    )

    data class User(
        @SerializedName("credit")
        var credit: Credit = Credit(),
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
        @SerializedName("last_search_key")
        var lastSearchKey: Any? = Any(),
        @SerializedName("phone_number")
        var phoneNumber: Any? = Any(),
        @SerializedName("status")
        var status: String = ""
    ) {
        data class Credit(
            @SerializedName("available_credit")
            var availableCredit: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("remaining_qty")
            var remainingQty: String = "",
            @SerializedName("total_credit")
            var totalCredit: String = "",
            @SerializedName("user_id")
            var userId: String = ""
        )
    }

    data class Product(
        @SerializedName("average_rating")
        var averageRating: String = "",
        @SerializedName("cms_privilege_id")
        var cmsPrivilegeId: String = "",
        @SerializedName("default_price")
        var defaultPrice: String = "",
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
        @SerializedName("in_stock")
        var inStock: String = "",
        @SerializedName("is_turf")
        var isTurf: String = "",
        @SerializedName("minimum_quantity")
        var minimumQuantity: String = "",
        @SerializedName("name")
        var name: String = "",
        @SerializedName("pivot")
        var pivot: Pivot = Pivot(),
        @SerializedName("price")
        var price: String = "",
        @SerializedName("product_benefit_title")
        var productBenefitTitle: String = "",
        @SerializedName("product_category")
        var productCategory: String = "",
        @SerializedName("product_category_id")
        var productCategoryId: String = "",
        @SerializedName("product_redeemable_against_credit")
        var productRedeemableAgainstCredit: Boolean = false,
        @SerializedName("product_unit")
        var productUnit: String = "",
        @SerializedName("product_unit_id")
        var productUnitId: String = "",
        @SerializedName("purchase_count")
        var purchaseCount: String = "",
        @SerializedName("review_count")
        var reviewCount: String = "",
        @SerializedName("weight")
        var weight: String = ""
    ) {
        data class Pivot(
            @SerializedName("base_price")
            var basePrice: String = "",
            @SerializedName("credit_used")
            var creditUsed: String = "",
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("order_id")
            var orderId: String = "",
            @SerializedName("product_id")
            var productId: String = "",
            @SerializedName("quantity")
            var quantity: String = "",
            @SerializedName("total_base_price")
            var totalBasePrice: String = ""
        )
    }
}