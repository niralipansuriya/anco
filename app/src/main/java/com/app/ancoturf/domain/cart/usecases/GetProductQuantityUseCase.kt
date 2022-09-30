package com.app.ancoturf.domain.cart.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.cart.remote.entity.CartDetailsResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.cart.CartRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class GetProductQuantityUseCase  @Inject constructor(private val cartRepository: CartRepository) :  BaseUseCase<BaseResponse<Any>>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<Any>> {
        return cartRepository.getAvailableQty(data?.get(UseCaseConstants.PRODUCTS) as String)
    }
}