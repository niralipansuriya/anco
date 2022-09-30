package com.app.ancoturf.domain.quote.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.quote.remote.entity.response.QuoteDetailsResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.quote.QuoteRepository
import io.reactivex.Single
import javax.inject.Inject

class QuoteDetailsUseCase @Inject constructor(private val quoteRepository: QuoteRepository) :  BaseUseCase<BaseResponse<QuoteDetailsResponse>>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<QuoteDetailsResponse>> {
        return quoteRepository.getQuoteDetails(quoteId = data?.get(UseCaseConstants.QUOTE_ID) as Int)
    }

}