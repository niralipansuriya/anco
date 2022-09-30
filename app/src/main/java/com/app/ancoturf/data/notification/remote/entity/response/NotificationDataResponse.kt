package com.app.ancoturf.data.notification.remote.entity.response

import com.app.ancoturf.data.payment.remote.entity.BillHistoryResponse
import com.google.gson.annotations.SerializedName

data class NotificationDataResponse(
    @SerializedName("data")
    var `data`: List<Data> = listOf(),
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
    data class Data(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("title")
        var title: String = "",
        @SerializedName("content")
        var content: String = "",
        @SerializedName("device_type")
        var device_type: String = "",
        @SerializedName("created_at")
        var created_at: String = ""
    )
}