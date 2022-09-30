package com.app.ancoturf.domain.cart.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.data.offer.remote.OfferDataResponse
import com.app.ancoturf.domain.BaseMayBeUseCase
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.account.AccountRepository
import com.app.ancoturf.domain.cart.CartRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(private val cartRepository: CartRepository) :  BaseUseCase<ProductDB>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<ProductDB> {
        return cartRepository.getProductById(data?.get(UseCaseConstants.PRODUCT_ID) as Int)
    }
}