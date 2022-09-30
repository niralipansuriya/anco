package com.app.ancoturf.domain.offer

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.offer.remote.OfferDataResponse
import io.reactivex.Single

interface OfferRepository {

    fun getOffers( page : String): Single<BaseResponse<OfferDataResponse>>

}