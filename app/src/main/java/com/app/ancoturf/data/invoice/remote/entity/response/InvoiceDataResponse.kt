package com.app.ancoturf.data.invoice.remote.entity.response

import com.app.ancoturf.data.notification.remote.entity.response.NotificationDataResponse
import com.google.gson.annotations.SerializedName

class InvoiceDataResponse {
    @SerializedName("data")
    var `data`: List<NotificationDataResponse.Data> = listOf()

}

data class Data(
    @SerializedName("payment_method_id")
    var payment_method_id: Int = 0

)