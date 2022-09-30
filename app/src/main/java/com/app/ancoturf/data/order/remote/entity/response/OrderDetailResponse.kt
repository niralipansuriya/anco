package com.app.ancoturf.data.order.remote.entity.response

import com.google.gson.annotations.SerializedName

class OrderDetailResponse(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("products")
    var products: List<OrderDataResponse.Product> = listOf(),
    @SerializedName("reference_number")
    var referenceNumber: String = "",
    @SerializedName("order_status")
    var orderStatus: OrderDataResponse.OrderStatus,
    @SerializedName("delivery_status")
    var deliveryStatus: OrderDataResponse.OrderStatus,
    @SerializedName("user")
    var user: OrderDataResponse.Users,
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
    var shippingFirstName: String = "",
    @SerializedName("shipping_address_line_1")
    var shippingAddressLine1: String = "",
    @SerializedName("shipping_address_line_2")
    var shippingAddressLine2: String = "",
    @SerializedName("shipping_country")
    var shippingCountry: String = "",
    @SerializedName("shipping_state")
    var shippingState: String = "",
    @SerializedName("shipping_city")
    var shippingCity: String = "",
    @SerializedName("shipping_post_code")
    var shippingPostCode: String = "",
    @SerializedName("shipping_phone")
    var shippingPhone: String = "",
    @SerializedName("credit_discount")
    var creditDescount: String = "",
    @SerializedName("sub_total_price")
    var subTotalPrice: String = "",
    @SerializedName("total_cart_price")
    var totalCartPrice: String = "",
    @SerializedName("payment_method")
    var paymentMethod: String = "",
    @SerializedName("payment_transaction_id")
    var paymentTransactionId: String = "",
    @SerializedName("payment_details")
    var paymentDetail: String = "",
    @SerializedName("payment_transaction_completed")
    var paymentTransactionCompleted: String = "",
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("shipping_type")
    var shippingType: String = "",
    @SerializedName("invoice_date")
    var invoiceDate: String = "",
    @SerializedName("payment_date")
    var paymentDate: String = "",

    @SerializedName("instructions_notes")
    var instructionsNotes: String = "",

    var checked: Boolean = false,

    @SerializedName("driver")
    var driver: DriverModel? = null



)