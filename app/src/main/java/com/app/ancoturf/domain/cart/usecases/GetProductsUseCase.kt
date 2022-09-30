package com.app.ancoturf.domain.cart.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.data.offer.remote.OfferDataResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.account.AccountRepository
import com.app.ancoturf.domain.cart.CartRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(private val cartRepository: CartRepository) :  BaseUseCase<List<ProductDB>>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<List<ProductDB>> {
        return cartRepository.getAllProducts()
    }
}