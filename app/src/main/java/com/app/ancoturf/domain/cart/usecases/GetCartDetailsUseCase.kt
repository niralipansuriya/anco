package com.app.ancoturf.domain.cart.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.cart.remote.entity.CartDetailsResponse
import com.app.ancoturf.data.cart.remote.entity.CountryResponse
import com.app.ancoturf.data.offer.remote.OfferDataResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.account.AccountRepository
import com.app.ancoturf.domain.cart.CartRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class GetCartDetailsUseCase @Inject constructor(private val cartRepository: CartRepository) :  BaseUseCase<BaseResponse<CartDetailsResponse>>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<CartDetailsResponse>> {
        return cartRepository.getCartDetails(data?.get(UseCaseConstants.PRODUCTS) as String ,
            data?.get(UseCaseConstants.COUPON_CODE) as Array<String>? ,
            data?.get(UseCaseConstants.SHIPPING_COUNTRY_CODE) as String?,
            data?.get(UseCaseConstants.SHIPPING_POSTAL_CODE) as String?,
            data?.get(UseCaseConstants.SHIPPING_TYPE) as String?,
            data?.get(UseCaseConstants.CREDIT) as String?)
    }
}