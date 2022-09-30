package com.app.ancoturf.data.account.remote.entity.response


import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("business")
    var business: Business = Business(),
    @SerializedName("device_token")
    var deviceToken: String = "",
    @SerializedName("device_type")
    var deviceType: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("first_name")
    var firstName: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("order_count")
    var orderCount: Int = 0,
    @SerializedName("quote_count")
    var quoteCount: Int = 0,
    @SerializedName("customer_count")
    var customerCount: Int = 0,
    @SerializedName("id_cms_privileges")
    var idCmsPrivileges: Int = 0,
    @SerializedName("allowed_to_place_order")
    var allowedToPlaceOrder: Int = 0,
    @SerializedName("image_url")
    var imageUrl: String = "",
    @SerializedName("last_name")
    var lastName: String = "",
    @SerializedName("phone_number")
    var phoneNumber: String = "",
    @SerializedName("privilege")
    var privilege: Privilege = Privilege(),
    @SerializedName("last_order")
    var lastOrder: LastOrder = LastOrder(),
    @SerializedName("credit")
    var credit: Credit? = Credit(),
    @SerializedName("status")
    var status: String = "",
    @SerializedName("token")
    var token: String = "",
    @SerializedName("quote_max_price")
    var quote_max_price: String = "",
    @SerializedName("order_max_price")
    var order_max_price: String = ""
) {
    data class Privilege(
        @SerializedName("created_at")
        var createdAt: String? = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("is_superadmin")
        var isSuperadmin: Int = 0,
        @SerializedName("name")
        var name: String = "",
        @SerializedName("theme_color")
        var themeColor: String = "",
        @SerializedName("updated_at")
        var updatedAt: String? = ""
    )

    data class LastOrder(
        @SerializedName("reference_number")
        var referenceNumber: String? = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("invoice_date")
        var invoiceDate: String? = "",
        @SerializedName("due_date")
        var dueDate: String = "",
        @SerializedName("invoice_number")
        var invoiceNumber: String = "",
        @SerializedName("total_cart_price")
        var totalCartPrice: String? = "",
        @SerializedName("amount_paid")
        var amountPaid: String? = ""
    )

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
        var disclaimer: String? = "",
        @SerializedName("email")
        var email: String = "",
        @SerializedName("gst")
        var gst: String? = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("logo_url")
        var logoUrl: String? = "",
        @SerializedName("mobile_number")
        var mobileNumber: String = "",
        @SerializedName("payment_terms")
        var paymentTerms: String? = "",
        @SerializedName("phone_number")
        var phoneNumber: String? = "",
        @SerializedName("registered_for_gst")
        var registeredForGst: String = "",
        @SerializedName("user_id")
        var userId: String = "",
        @SerializedName("web_url")
        var webUrl: String? = ""
    )

    data class Credit(
        @SerializedName("total_credit")
        var total_credit: String = "",
        @SerializedName("available_credit")
        var available_credit: String = "",
        @SerializedName("remaining_qty")
        var remaining_qty: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("user_id")
        var userId: String = ""
    )
}