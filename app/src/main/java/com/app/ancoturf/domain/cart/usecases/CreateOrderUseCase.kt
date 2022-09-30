package com.app.ancoturf.domain.cart.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.cart.FreeProductAPI
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.cart.remote.entity.CartDetailsResponse
import com.app.ancoturf.data.cart.remote.entity.CountryResponse
import com.app.ancoturf.data.cart.remote.entity.OrderDetails
import com.app.ancoturf.data.offer.remote.OfferDataResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.account.AccountRepository
import com.app.ancoturf.domain.cart.CartRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(private val cartRepository: CartRepository) :  BaseUseCase<BaseResponse<OrderDetails>>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<OrderDetails>> {
        return cartRepository.createOrder(data?.get(UseCaseConstants.EMAIL) as String , data?.get(UseCaseConstants.PURCHASE_FIRST_NAME) as String? , data?.get(UseCaseConstants.PURCHASE_LAST_NAME) as String? , data?.get(UseCaseConstants.SHIPPING_FIRST_NAME) as String? , data?.get(UseCaseConstants.SHIPPING_LAST_NAME) as String? , data?.get(UseCaseConstants.SHIPPING_ADDRESS_LINE_1) as String? , data?.get(UseCaseConstants.SHIPPING_ADDRESS_LINE_2) as String? , data?.get(UseCaseConstants.SHIPPING_COUNTRY_CODE) as String?, data?.get(UseCaseConstants.SHIPPING_STATE) as String?, data?.get(UseCaseConstants.SHIPPING_CITY) as String?, data?.get(UseCaseConstants.SHIPPING_PHONE) as String?, data?.get(UseCaseConstants.SHIPPING_POSTAL_CODE) as Int, data?.get(UseCaseConstants.SHIPPING_TYPE) as String?, data?.get(UseCaseConstants.PRODUCTS) as String, data?.get(UseCaseConstants.DELIVERY_DATE) as String, data?.get(UseCaseConstants.CREDIT) as String, data?.get(UseCaseConstants.SUB_TOTAL_PRICE) as String, data?.get(UseCaseConstants.TOTAL_DISCOUNT) as String, data?.get(UseCaseConstants.CREDIT_DISCOUNT) as String, data?.get(UseCaseConstants.SHIPPING_PRICE) as String, data?.get(UseCaseConstants.TOTAL_CART_PRICE) as String, data?.get(UseCaseConstants.COUPON_CODE) as Array<String>, data?.get(UseCaseConstants.instructions_notes) as String, data?.get(UseCaseConstants.IS_CHECKED) as Int, data?.get(UseCaseConstants.ADMIN_FEE) as String,data?.get(UseCaseConstants.FREE_PRODUCTS) as String)
    }
}