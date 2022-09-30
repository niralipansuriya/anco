package com.app.ancoturf.data.cart

import com.app.ancoturf.data.cart.remote.CartService
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.domain.cart.DeliveryDatesRepository
import io.reactivex.Single
import javax.inject.Inject

class DeliveryDatesDataRepository @Inject constructor() :
    DeliveryDatesRepository, CommonService<CartService>() {

    override val baseClass = CartService::class.java

    override fun getDeliveryDates(user_type : Int): Single<Object> {
        return networkService.getDeliveryDates(user_type)
    }
}