package com.app.ancoturf.data.cart.remote.entity


import com.google.gson.annotations.SerializedName

data class ExpressCheckoutResponse(
    @SerializedName("create_time")
    var createTime: String = "",
    @SerializedName("id")
    var id: String = "",
    @SerializedName("intent")
    var intent: String = "",
    @SerializedName("links")
    var links: List<Link> = listOf(),
    @SerializedName("payer")
    var payer: Payer = Payer(),
    @SerializedName("purchase_units")
    var purchaseUnits: List<PurchaseUnit> = listOf(),
    @SerializedName("status")
    var status: String = "",
    @SerializedName("update_time")
    var updateTime: String = ""
) {
    data class Link(
        @SerializedName("href")
        var href: String = "",
        @SerializedName("method")
        var method: String = "",
        @SerializedName("rel")
        var rel: String = "",
        @SerializedName("title")
        var title: String = ""
    )

    data class PurchaseUnit(
        @SerializedName("amount")
        var amount: Amount = Amount(),
        @SerializedName("payee")
        var payee: Payee = Payee(),
        @SerializedName("payments")
        var payments: Payments = Payments(),
        @SerializedName("reference_id")
        var referenceId: String = "",
        @SerializedName("shipping")
        var shipping: Shipping = Shipping()
    ) {
        data class Payments(
            @SerializedName("captures")
            var captures: List<Capture> = listOf()
        ) {
            data class Capture(
                @SerializedName("amount")
                var amount: Amount = Amount(),
                @SerializedName("create_time")
                var createTime: String = "",
                @SerializedName("final_capture")
                var finalCapture: Boolean = false,
                @SerializedName("id")
                var id: String = "",
                @SerializedName("links")
                var links: List<Link> = listOf(),
                @SerializedName("seller_protection")
                var sellerProtection: SellerProtection = SellerProtection(),
                @SerializedName("status")
                var status: String = "",
                @SerializedName("update_time")
                var updateTime: String = ""
            ) {
                data class SellerProtection(
                    @SerializedName("dispute_categories")
                    var disputeCategories: List<String> = listOf(),
                    @SerializedName("status")
                    var status: String = ""
                )

                data class Link(
                    @SerializedName("href")
                    var href: String = "",
                    @SerializedName("method")
                    var method: String = "",
                    @SerializedName("rel")
                    var rel: String = "",
                    @SerializedName("title")
                    var title: String = ""
                )

                data class Amount(
                    @SerializedName("currency_code")
                    var currencyCode: String = "",
                    @SerializedName("value")
                    var value: String = ""
                )
            }
        }

        data class Amount(
            @SerializedName("currency_code")
            var currencyCode: String = "",
            @SerializedName("value")
            var value: String = ""
        )

        data class Shipping(
            @SerializedName("address")
            var address: Address = Address(),
            @SerializedName("name")
            var name: Name = Name()
        ) {
            data class Name(
                @SerializedName("full_name")
                var fullName: String = ""
            )

            data class Address(
                @SerializedName("address_line_1")
                var addressLine1: String = "",
                @SerializedName("admin_area_1")
                var adminArea1: String = "",
                @SerializedName("admin_area_2")
                var adminArea2: String = "",
                @SerializedName("country_code")
                var countryCode: String = "",
                @SerializedName("postal_code")
                var postalCode: String = ""
            )
        }

        data class Payee(
            @SerializedName("email_address")
            var emailAddress: String = "",
            @SerializedName("merchant_id")
            var merchantId: String = ""
        )
    }

    data class Payer(
        @SerializedName("address")
        var address: Address = Address(),
        @SerializedName("email_address")
        var emailAddress: String = "",
        @SerializedName("name")
        var name: Name = Name(),
        @SerializedName("payer_id")
        var payerId: String = ""
    ) {
        data class Name(
            @SerializedName("given_name")
            var givenName: String = "",
            @SerializedName("surname")
            var surname: String = ""
        )

        data class Address(
            @SerializedName("country_code")
            var countryCode: String = ""
        )
    }
}