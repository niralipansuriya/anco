package com.app.ancoturf.domain.offer.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.offer.remote.OfferDataResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.offer.OfferRepository
import io.reactivex.Single
import javax.inject.Inject

class OffersUseCase @Inject constructor(private val offerRepository: OfferRepository) :
    BaseUseCase<BaseResponse<OfferDataResponse>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<OfferDataResponse>> {
        return offerRepository.getOffers( page = data?.get(UseCaseConstants.PER_PAGE) as String)
    }
}