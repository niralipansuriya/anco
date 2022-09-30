package com.app.ancoturf.data.cart.remote.entity


import com.google.gson.annotations.SerializedName

data class PaymentMethodsResponse(
    @SerializedName("bank_detail")
    var bankDetail: BankDetail = BankDetail(),
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("payment_method")
    var paymentMethod: List<PaymentMethod> = listOf()
) {
    data class BankDetail(
        @SerializedName("bank_acc")
        var bankAcc: String = "",
        @SerializedName("bank_bsb")
        var bankBsb: String = "",
        @SerializedName("bank_details")
        var bankDetails: String = "",
        @SerializedName("bank_name")
        var bankName: String = ""
    )

    data class PaymentMethod(
        @SerializedName("descriptions")
        var descriptions: String = "",
        @SerializedName("display_name")
        var displayName: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("pivot")
        var pivot: Pivot = Pivot(),
        @SerializedName("slug")
        var slug: String = "",
        @SerializedName("status")
        var status: String = "",
        @SerializedName("is_online_payment")
        var isOnlinePayment: Int = 0,
        var checked: Boolean = false
    ) {
        data class Pivot(
            @SerializedName("cms_privilege_id")
            var cmsPrivilegeId: String = "",
            @SerializedName("payment_method_id")
            var paymentMethodId: String = ""
        )
    }
}