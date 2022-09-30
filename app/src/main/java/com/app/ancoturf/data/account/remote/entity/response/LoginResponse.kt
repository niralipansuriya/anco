package com.app.ancoturf.data.account.remote.entity.response


import com.google.gson.annotations.SerializedName

data class LoginResponse(
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
    @SerializedName("privilege")
    var privilege: Privilege = Privilege(),
    @SerializedName("status")
    var status: String = "",
    @SerializedName("token")
    var token: String = ""
) {
    data class Privilege(
        @SerializedName("created_at")
        var createdAt: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("is_superadmin")
        var isSuperadmin: String = "",
        @SerializedName("name")
        var name: String = "",
        @SerializedName("theme_color")
        var themeColor: String = "",
        @SerializedName("updated_at")
        var updatedAt: String = ""
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
        var disclaimer: String = "",
        @SerializedName("email")
        var email: String = "",
        @SerializedName("gst")
        var gst: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("logo_url")
        var logoUrl: String = "",
        @SerializedName("mobile_number")
        var mobileNumber: String = "",
        @SerializedName("payment_terms")
        var paymentTerms: String = "",
        @SerializedName("phone_number")
        var phoneNumber: String = "",
        @SerializedName("registered_for_gst")
        var registeredForGst: String = "",
        @SerializedName("user_id")
        var userId: String = "",
        @SerializedName("web_url")
        var webUrl: String = ""
    )
}