package com.app.ancoturf.domain.payment.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.payment.remote.entity.PendingPaymentResponse
import com.app.ancoturf.data.product.remote.entity.response.RelatedProductsResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.payment.PaymentRepository
import com.app.ancoturf.domain.product.ProductRepository
import io.reactivex.Single
import javax.inject.Inject

class PendingPaymentUseCase @Inject constructor(private val paymentRepository: PaymentRepository) :
    BaseUseCase<BaseResponse<PendingPaymentResponse>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<PendingPaymentResponse>> {
            return paymentRepository.getPendingPayments()
    }
}