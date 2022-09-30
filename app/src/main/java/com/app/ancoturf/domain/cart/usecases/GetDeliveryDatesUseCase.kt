package com.app.ancoturf.domain.cart.usecases

import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.cart.CartRepository
import com.app.ancoturf.domain.cart.DeliveryDatesRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class GetDeliveryDatesUseCase @Inject constructor(
    private val deliveryDatesRepository: DeliveryDatesRepository
) : BaseUseCase<Object>() {
    override fun buildSingle(data: Map<String, Any?>?): Single<Object> {
        return deliveryDatesRepository.getDeliveryDates( data?.get(UseCaseConstants.USER_TYPE) as Int)
    }
}