package com.app.ancoturf.domain.cart.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.cart.remote.entity.PaymentMethodsResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.cart.CartRepository
import io.reactivex.Single
import javax.inject.Inject

class GetPaymentMethodsUseCase @Inject constructor(private val cartRepository: CartRepository) :
    BaseUseCase<BaseResponse<PaymentMethodsResponse>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<PaymentMethodsResponse>> {
        return cartRepository.getPaymentMethods()
    }
}