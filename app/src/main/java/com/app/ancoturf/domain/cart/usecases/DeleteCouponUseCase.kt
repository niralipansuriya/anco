package com.app.ancoturf.domain.cart.usecases

import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.cart.CartRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class DeleteCouponUseCase @Inject constructor(private val cartRepository: CartRepository) : BaseUseCase<Boolean>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<Boolean> {

        var couponDb: CouponDB? = null
        if (data != null)
            couponDb = data?.get(UseCaseConstants.COUPON) as CouponDB

        return if (couponDb != null)
            cartRepository.deleteCoupon(couponDb)
        else
            cartRepository.deleteAllCoupons()
    }
}