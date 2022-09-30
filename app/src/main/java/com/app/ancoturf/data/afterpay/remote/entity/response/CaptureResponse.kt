package com.app.ancoturf.data.afterpay.remote.entity.response


import com.google.gson.annotations.SerializedName

data class CaptureResponse(
    @SerializedName("created")
    var created: String = "",
    @SerializedName("errorCode")
    var errorCode: String = "",
    @SerializedName("errorId")
    var errorId: String = "",
    @SerializedName("httpStatusCode")
    var httpStatusCode: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("merchantReference")
    var merchantReference: String = "",
    @SerializedName("message")
    var message: String = "",
    @SerializedName("orderDetails")
    var orderDetails: OrderDetails = OrderDetails(),
    @SerializedName("status")
    var status: String = "",
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("token")
    var token: String = "",
    @SerializedName("totalAmount")
    var totalAmount: TotalAmount = TotalAmount()
) {
    data class TotalAmount(
        @SerializedName("amount")
        var amount: String = "",
        @SerializedName("currency")
        var currency: String = ""
    )

    data class OrderDetails(
        @SerializedName("billing")
        var billing: Billing = Billing(),
        @SerializedName("consumer")
        var consumer: Consumer = Consumer(),
        @SerializedName("courier")
        var courier: Courier = Courier(),
        @SerializedName("discounts")
        var discounts: List<Any> = listOf(),
        @SerializedName("items")
        var items: List<Any> = listOf(),
        @SerializedName("shipping")
        var shipping: Shipping = Shipping(),
        @SerializedName("shippingAmount")
        var shippingAmount: ShippingAmount = ShippingAmount(),
        @SerializedName("taxAmount")
        var taxAmount: TaxAmount = TaxAmount()
    ) {
        data class TaxAmount(
            @SerializedName("amount")
            var amount: String = "",
            @SerializedName("currency")
            var currency: String = ""
        )

        data class Billing(
            @SerializedName("countryCode")
            var countryCode: String = "",
            @SerializedName("line1")
            var line1: String = "",
            @SerializedName("line2")
            var line2: String = "",
            @SerializedName("name")
            var name: String = "",
            @SerializedName("phoneNumber")
            var phoneNumber: String = "",
            @SerializedName("postcode")
            var postcode: String = "",
            @SerializedName("state")
            var state: String = "",
            @SerializedName("suburb")
            var suburb: String = ""
        )

        data class Courier(
            @SerializedName("countryCode")
            var countryCode: String = "",
            @SerializedName("line1")
            var line1: String = "",
            @SerializedName("line2")
            var line2: String = "",
            @SerializedName("name")
            var name: String = "",
            @SerializedName("phoneNumber")
            var phoneNumber: String = "",
            @SerializedName("postcode")
            var postcode: String = "",
            @SerializedName("state")
            var state: String = "",
            @SerializedName("suburb")
            var suburb: String = ""
        )

        data class ShippingAmount(
            @SerializedName("amount")
            var amount: String = "",
            @SerializedName("currency")
            var currency: String = ""
        )

        data class Consumer(
            @SerializedName("email")
            var email: String = "",
            @SerializedName("givenNames")
            var givenNames: String = "",
            @SerializedName("phoneNumber")
            var phoneNumber: String = "",
            @SerializedName("surname")
            var surname: String = ""
        )

        data class Shipping(
            @SerializedName("countryCode")
            var countryCode: String = "",
            @SerializedName("line1")
            var line1: String = "",
            @SerializedName("line2")
            var line2: String = "",
            @SerializedName("name")
            var name: String = "",
            @SerializedName("phoneNumber")
            var phoneNumber: String = "",
            @SerializedName("postcode")
            var postcode: String = "",
            @SerializedName("state")
            var state: String = "",
            @SerializedName("suburb")
            var suburb: String = ""
        )
    }
}