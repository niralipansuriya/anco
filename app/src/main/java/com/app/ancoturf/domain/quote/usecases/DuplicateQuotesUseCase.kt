package com.app.ancoturf.domain.quote.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.portfolio.remote.entity.QuoteAncoProductRequest
import com.app.ancoturf.data.portfolio.remote.entity.QuoteNonAncoProductRequest
import com.app.ancoturf.data.quote.remote.entity.response.AddEditQuoteResponse
import com.app.ancoturf.data.quote.remote.entity.response.DuplicateQuoteResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.quote.QuoteRepository
import com.app.ancoturf.utils.Utility
import io.reactivex.Single
import javax.inject.Inject

class DuplicateQuotesUseCase @Inject constructor(private val quoteRepository: QuoteRepository) :  BaseUseCase<BaseResponse<DuplicateQuoteResponse>>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<DuplicateQuoteResponse>> {
        return quoteRepository.duplicateQuote(quoteId = data?.get(UseCaseConstants.QUOTE_ID) as  Int)
    }


}