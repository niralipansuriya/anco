package com.app.ancoturf.domain.cart

import io.reactivex.Single

interface DeliveryDatesRepository {

    fun getDeliveryDates(user_type : Int) : Single<Object>
}