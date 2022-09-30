package com.app.ancoturf.domain.invoice.usecase

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.invoice.remote.entity.response.InvoiceDataResponse
import com.app.ancoturf.data.order.remote.entity.response.OrderDetailResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.invoice.InvoiceRepository
import com.app.ancoturf.domain.order.OrderRepository
import io.reactivex.Single
import javax.inject.Inject

class InvoiceUseCase @Inject constructor(private val invoiceRepository: InvoiceRepository) :
    BaseUseCase<BaseResponse<InvoiceDataResponse>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<InvoiceDataResponse>> {
        return invoiceRepository.getInvoicePDF(invoiceId = data?.get(UseCaseConstants.InvoiceId) as String)
    }

}