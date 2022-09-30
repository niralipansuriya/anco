package com.app.ancoturf.data.payment.remote.entity


import com.google.gson.annotations.SerializedName

data class BillHistoryResponse(
    @SerializedName("data")
    var `data`: List<Data> = listOf(),
    @SerializedName("current_page")
    var currentPage: Int = 0,
    @SerializedName("first_page_url")
    var firstPageUrl: String = "",
    @SerializedName("from")
    var from: Int = 0,
    @SerializedName("next_page_url")
    var nextPageUrl: Any? = Any(),
    @SerializedName("path")
    var path: String = "",
    @SerializedName("per_page")
    var perPage: Int = 0,
    @SerializedName("prev_page_url")
    var prevPageUrl: Any? = Any(),
    @SerializedName("total_pending_amount")
    var total_pending_amount: Float = 0.0F,
    @SerializedName("to")
    var to: Int = 0
)
{
    data class Data(
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
        var amountDue: String = "",
        @SerializedName("pdf_url")
        var pdfUrl:String = "",
        @SerializedName("invoice_id")
        var invoiceId:String = ""
    )
}