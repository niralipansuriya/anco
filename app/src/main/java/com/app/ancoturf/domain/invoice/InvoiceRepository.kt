package com.app.ancoturf.domain.invoice

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.invoice.remote.entity.response.InvoiceDataResponse
import io.reactivex.Single

interface InvoiceRepository {
    fun getInvoicePDF(invoiceId: String): Single<BaseResponse<InvoiceDataResponse>>

}