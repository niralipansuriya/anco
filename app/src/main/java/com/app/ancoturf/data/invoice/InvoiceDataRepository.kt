package com.app.ancoturf.data.invoice

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.data.invoice.remote.InvoiceService
import com.app.ancoturf.data.invoice.remote.entity.response.InvoiceDataResponse
import com.app.ancoturf.data.notification.remote.NotificationServices
import com.app.ancoturf.domain.invoice.InvoiceRepository
import com.app.ancoturf.domain.notification.NotificationRepository
import io.reactivex.Single
import javax.inject.Inject

class InvoiceDataRepository  @Inject constructor() : InvoiceRepository,
    CommonService<InvoiceService>() {

    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = InvoiceService::class.java
    override fun getInvoicePDF(invoiceId: String): Single<BaseResponse<InvoiceDataResponse>> {
        return networkService.getInvoicePDF(invoiceId).map {
            it
        }
    }
}