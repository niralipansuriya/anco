package com.app.ancoturf.data.payment.remote.entity


import com.google.gson.annotations.SerializedName

data class PendingPaymentResponse(
    @SerializedName("invoices")
    var `data`: ArrayList<Invoices> = ArrayList(),
    @SerializedName("total_pending_amount")
    var total_pending_amount: Float = 0.0F
)
{
    data class Invoices(
        @SerializedName("id")
        var id: Int = 0 ,
        @SerializedName("reference_number")
        var referenceNumber: String = "",
        @SerializedName("invoice_date")
        var invoiceDate: String = "",
        @SerializedName("due_date")
        var dueDate: String = "",
        @SerializedName("invoice_number")
        var invoiceNumber: String = "",
        @SerializedName("total_cart_price")
        var totalCartPrice: String = "",
        @SerializedName("amount_paid")
        var amountPaid: String = "",
        @SerializedName("amount_due")
        var amountDue: String = ""

    )
}