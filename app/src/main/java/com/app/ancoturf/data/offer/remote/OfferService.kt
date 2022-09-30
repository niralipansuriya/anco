package com.app.ancoturf.data.offer.remote

import com.app.ancoturf.data.BaseResponse
import io.reactivex.Single
import retrofit2.http.*

interface OfferService {

    @GET("api/offers")
    fun getOffers(@Query("page") page: String):Single<BaseResponse<OfferDataResponse>>

}