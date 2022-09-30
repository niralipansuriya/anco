package com.app.ancoturf.domain.payment.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.cart.remote.entity.OrderDetails
import com.app.ancoturf.data.payment.remote.entity.BillHistoryResponse
import com.app.ancoturf.data.payment.remote.entity.PendingPaymentResponse
import com.app.ancoturf.data.product.remote.entity.response.RelatedProductsResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.payment.PaymentRepository
import com.app.ancoturf.domain.product.ProductRepository
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class UpdatePaymentUseCase @Inject constructor(private val updatePaymentRepository: PaymentRepository) :
    BaseUseCase<BaseResponse<OrderDetails>>() {
    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<OrderDetails>> {
            return updatePaymentRepository.updatePayment(data?.get(UseCaseConstants.ORDER_ID) as String,
                data[UseCaseConstants.PAYMENT_METHOD] as String,
                data[UseCaseConstants.PAYMENT_DETAILS] as String)
    }
}