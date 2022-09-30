package com.app.ancoturf.domain.deliverydate

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.data.cart.remote.entity.*
import com.app.ancoturf.data.offer.remote.OfferDataResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import io.reactivex.Maybe
import io.reactivex.Single

interface DeliveryDateValidationRepository {

    fun deliveryDateValidation(deliveryDate: String): Single<DeliveryDateValidation>

}