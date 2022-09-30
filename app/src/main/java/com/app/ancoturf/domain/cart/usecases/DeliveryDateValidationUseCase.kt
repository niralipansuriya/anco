package com.app.ancoturf.domain.cart.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.cart.remote.entity.CountryResponse
import com.app.ancoturf.data.cart.remote.entity.DeliveryDateValidation
import com.app.ancoturf.data.offer.remote.OfferDataResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.account.AccountRepository
import com.app.ancoturf.domain.cart.CartRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.deliverydate.DeliveryDateValidationRepository
import io.reactivex.Single
import javax.inject.Inject

class DeliveryDateValidationUseCase @Inject constructor(private val deliveryDateValidationRepository: DeliveryDateValidationRepository) :  BaseUseCase<DeliveryDateValidation>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<DeliveryDateValidation> {
        return deliveryDateValidationRepository.deliveryDateValidation(data?.get(UseCaseConstants.DELIVERY_DATE) as String)
    }
}