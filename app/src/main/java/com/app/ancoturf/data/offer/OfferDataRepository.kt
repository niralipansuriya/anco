package com.app.ancoturf.data.offer

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.data.offer.remote.OfferDataResponse
import com.app.ancoturf.data.offer.remote.OfferService
import com.app.ancoturf.domain.offer.OfferRepository
import io.reactivex.Single
import javax.inject.Inject

class OfferDataRepository @Inject constructor() : OfferRepository, CommonService<OfferService>() {

    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = OfferService::class.java

    override fun getOffers( page : String): Single<BaseResponse<OfferDataResponse>> {
        return networkService.getOffers(page).map {
            it
        }
    }
}