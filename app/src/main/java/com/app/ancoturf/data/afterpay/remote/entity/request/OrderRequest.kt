package com.app.ancoturf.data.afterpay.remote.entity.request


import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("consumer")
    var consumer: Consumer = Consumer(),
    @SerializedName("merchant")
    var merchant: Merchant = Merchant(),
    @SerializedName("totalAmount")
    var totalAmount: TotalAmount = TotalAmount()
) {
    data class Merchant(
        @SerializedName("redirectCancelUrl")
        var redirectCancelUrl: String = "",
        @SerializedName("redirectConfirmUrl")
        var redirectConfirmUrl: String = ""
    )

    data class Consumer(
        @SerializedName("email")
        var email: String = "",
        @SerializedName("givenNames")
        var givenNames: String = "",
        @SerializedName("phoneNumber")
        var phoneNumber: String? = "",
        @SerializedName("surname")
        var surname: String = ""
    )

    data class TotalAmount(
        @SerializedName("amount")
        var amount: String = "",
        @SerializedName("currency")
        var currency: String = ""
    )
}