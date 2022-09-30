package com.app.ancoturf.domain.quote.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.quote.remote.entity.response.QuoteDetailsResponse
import com.app.ancoturf.data.quote.remote.entity.response.QuotesDataResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.quote.QuoteRepository
import io.reactivex.Single
import javax.inject.Inject

class QuotesUseCase @Inject constructor(private val quoteRepository: QuoteRepository) :
    BaseUseCase<BaseResponse<QuotesDataResponse>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<QuotesDataResponse>> {
        return if (data != null)
            quoteRepository.getQuotesByCategories(
                status = data[UseCaseConstants.STATUS] as String,
                minCost = data[UseCaseConstants.COST_MIN] as String,
                maxCost = data[UseCaseConstants.COST_MAX] as String,
                address = data[UseCaseConstants.ADDRESS] as String,
                sortBy = data[UseCaseConstants.SORT_BY] as String,
                page = data[UseCaseConstants.PER_PAGE] as String
            )
        else
            quoteRepository.getAllQuotes()
    }

}