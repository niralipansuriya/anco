package com.app.ancoturf.data.deliverydate.remote

import com.app.ancoturf.data.cart.remote.entity.DeliveryDateValidation
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface DeliveryDateValidationService {

    @GET("webservices/validatedate.php")
    fun deliveryDateValidation(@Query("delivery_date") deliveryDate: String): Single<DeliveryDateValidation>

}