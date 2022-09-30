package com.app.ancoturf.domain.cart.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.cart.remote.entity.OrderDetails
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.cart.CartRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class UpdateOrderUseCase @Inject constructor(private val cartRepository: CartRepository) :
    BaseUseCase<BaseResponse<OrderDetails>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<OrderDetails>> {

        return cartRepository.updateOrder(
            data?.get(UseCaseConstants.ORDER_ID) as Int,
            data?.get(UseCaseConstants.PAYMENT_METHOD) as String,
            data?.get(UseCaseConstants.PAYMENT_DETAILS) as String?,
            data?.get(UseCaseConstants.PAYMENT_TRANSACTION_COMPLETED) as String?,
            data?.get(UseCaseConstants.QUOTEID) as String?
        )
    }
}