package com.app.ancoturf.data.order.remote.entity.response

import com.app.ancoturf.data.cart.remote.entity.CartDetailsResponse
import com.google.gson.annotations.SerializedName

class OrderDataResponse(
    @SerializedName("data")
    var `data`: ArrayList<OrderDetailResponse> = ArrayList(),
    @SerializedName("current_page")
    var currentPage: Int = 0,

    @SerializedName("first_page_url")
    var firstPageUrl: String = "",
    @SerializedName("from")
    var from: Int = 0,
    @SerializedName("next_page_url")
    var nextPageUrl: String = "",
    @SerializedName("path")
    var path: String = "",
    @SerializedName("per_page")
    var perPage: Int = 0,
    @SerializedName("prev_page_url")
    var prevPageUrl: String = "",
    @SerializedName("to")
    var to: Int = 0
) {
    data class Data
        (
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("products")
        var products: List<Product> = listOf(),
        @SerializedName("reference_number")
        var referenceNumber: String = "",
        @SerializedName("order_status")
        var orderStatus: OrderStatus,
        @SerializedName("delivery_status")
        var deliveryStatus: OrderStatus,
        @SerializedName("user")
        var user: Users,
        @SerializedName("user_id")
        var userId: String = "",
        @SerializedName("total_discount")
        var totalDiscount: String = "",
        @SerializedName("shipping_cost")
        var shippingCost: String = "",
        @SerializedName("shipping_method")
        var shippingMethod: String = "",
        @SerializedName("delivery_date")
        var deliveryDate: String = "",
        @SerializedName("order_status_id")
        var orderStatusId: String = "",
        @SerializedName("delivery_status_id")
        var deliveryStatusId: String = "",
        @SerializedName("shipping_email")
        var shippingMail: String = "",
        @SerializedName("shipping_first_name")
        var shippingFirstName  : String = "",
        @SerializedName("shipping_address_line_1")
        var shippingAddressLine1  : String = "",
        @SerializedName("shipping_address_line_2")
        var shippingAddressLine2  : String = "",
        @SerializedName("shipping_country")
        var shippingCountry  : String = "",
        @SerializedName("shipping_state")
        var shippingState  : String = "",
        @SerializedName("shipping_city")
        var shippingCity  : String = "",
        @SerializedName("shipping_post_code")
        var shippingPostCode  : String = "",
        @SerializedName("shipping_phone")
        var shippingPhone  : String = "",
        @SerializedName("credit_discount")
        var creditDescount : String = "",
        @SerializedName("sub_total_price")
        var subTotalPrice : String = "",
        @SerializedName("total_cart_price")
        var totalCartPrice : String = "",
        @SerializedName("payment_method")
        var paymentMethod : String = "",
        @SerializedName("payment_transaction_id")
        var paymentTransactionId : String = "",
        @SerializedName("payment_details")
        var paymentDetail : String = "",
        @SerializedName("payment_transaction_completed")
        var paymentTransactionCompleted : String = "",
        @SerializedName("created_at")
        var createdAt : String = ""
    )

    data class Product(

        var isChecked : Boolean = false,

        @SerializedName("average_rating")
        var averageRating: String = "",
        @SerializedName("cms_privilege_id")
        var cmsPrivilegeId: String = "",
        @SerializedName("default_price")
        var defaultPrice: String = "",
        @SerializedName("feature_image_url")
        var featureImageUrl: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("in_stock")
        var inStock: Int = 0,
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
        @SerializedName("product_category_id")
        var productCategoryId: String = "",
        @SerializedName("product_unit")
        var productUnit: String = "",
        @SerializedName("product_unit_id")
        var productUnitId: String = "",
        @SerializedName("purchase_count")
        var purchaseCount: String = "",
        @SerializedName("review_count")
        var reviewCount: String = "",
        @SerializedName("free_product")
        var free_product: Int = 0,
        @SerializedName("free_product_qty")
        var free_product_qty: Int = 0,
        @SerializedName("free_products")
    var free_products: FreeProducts =FreeProducts()
    ) {
        data class Pivot(
            @SerializedName("order_id")
            var orderid: String = "",
            @SerializedName("total_base_price")
            var totalBasePrice:  Float = 0f,
            @SerializedName("product_id")
            var productId: String = "",
            @SerializedName("product_price")
            var productPrice: Float = 0f,
            @SerializedName("quantity")
            var quantity: Int = 0,
            @SerializedName("base_price")
            var basePrice: Float = 0f,
            @SerializedName("credit_used")
            var creditUsed: String = ""
        )
    }

    data class FreeProducts(
        var quantity: Int = 0,
        var feature_image_url: String = "",
        @SerializedName("product_name")
        var productName: String = "",
        @SerializedName("product_id")
        var product_id: Int = 0
    )
    data class OrderStatus(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("slug")
        var slug: String = "",
        @SerializedName("display_name")
        var displayName: String = "",
        @SerializedName("status")
        var status: String = "",
        var position : Int = 0

    )

    data class Users(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("first_name")
        var firstName: String = "",
        @SerializedName("email")
        var email: String = "",
        @SerializedName("status")
        var status: String = "",
        @SerializedName("last_name")
        var lastName: String = "",
        @SerializedName("phone_number")
        var phoneNumber: String = "",
        @SerializedName("last_search_key")
        var lastSearchKey: String = "",
        @SerializedName("image_url")
        var imageUrl: String = "",
        @SerializedName("credit")
        var credit: Credit
    ) {
        data class Credit
            (
            @SerializedName("id")
            var id: Int = 0,
            @SerializedName("user_id")
            var userId: String = "",
            @SerializedName("total_credit")
            var totalCredit: String = "",
            @SerializedName("available_credit")
            var availableCredit: String = "",
            @SerializedName("remaining_qty")
            var remainingQty: String = ""

            )
    }


}
