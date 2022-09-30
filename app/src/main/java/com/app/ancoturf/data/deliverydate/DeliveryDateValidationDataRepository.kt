package com.app.ancoturf.data.deliverydate

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.AccountService
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.data.cart.remote.CartService
import com.app.ancoturf.data.cart.remote.entity.*
import com.app.ancoturf.data.common.local.AncoRoomDatabase
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.data.common.network.DateValidationService
import com.app.ancoturf.data.deliverydate.remote.DeliveryDateValidationService
import com.app.ancoturf.domain.cart.CartRepository
import com.app.ancoturf.domain.deliverydate.DeliveryDateValidationRepository
import io.reactivex.Maybe
import io.reactivex.Single
import javax.inject.Inject


class DeliveryDateValidationDataRepository @Inject constructor(private val ancoRoomDatabase: AncoRoomDatabase) : DeliveryDateValidationRepository , DateValidationService<DeliveryDateValidationService>() {

    override val baseClass = DeliveryDateValidationService::class.java

    override fun deliveryDateValidation(deliveryDate: String): Single<DeliveryDateValidation> {
        return networkService.deliveryDateValidation(deliveryDate).map {
            it
        }
    }
}