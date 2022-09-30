package com.app.ancoturf.domain.quote.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.quote.remote.entity.response.CustomersDataResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.quote.QuoteRepository
import io.reactivex.Single
import javax.inject.Inject

class CustomersUseCase @Inject constructor(private val quoteRepository: QuoteRepository) :  BaseUseCase<BaseResponse<CustomersDataResponse>>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<CustomersDataResponse>> {
        return quoteRepository.getCustomers()
    }

}