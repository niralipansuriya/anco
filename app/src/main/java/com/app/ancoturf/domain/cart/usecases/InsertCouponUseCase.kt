package com.app.ancoturf.domain.cart.usecases

import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.cart.CartRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class InsertCouponUseCase @Inject constructor(private val cartRepository: CartRepository) :  BaseUseCase<Boolean>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<Boolean> {
        var couponDb = CouponDB(data?.get(UseCaseConstants.COUPON_ID) as Int  ,
            data?.get(UseCaseConstants.COUPON_CODE) as String  ,
            data?.get(UseCaseConstants.COUPON_DISCOUNT) as String ,
            data?.get(UseCaseConstants.COUPON_DISCOUNT_TYPE) as String ,
            data?.get(UseCaseConstants.COUPON_EXPIRY_DATE) as String ,
            data?.get(UseCaseConstants.COUPON_ITEM_IDS) as String ,
            data?.get(UseCaseConstants.COUPON_ITEM_TYPE) as String ,
            data?.get(UseCaseConstants.COUPON_NAME) as String ,
            data?.get(UseCaseConstants.COUPON_START_DATE) as String ,
            data?.get(UseCaseConstants.COUPON_STATUS) as String)
        return if (data.get(UseCaseConstants.COUPON_UPDATE) as Boolean)
            cartRepository.updateCoupon(couponDb)
        else
            cartRepository.insertCoupon(couponDb)
    }
}