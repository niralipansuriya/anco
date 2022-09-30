package com.app.ancoturf.domain.search.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.quote.remote.entity.response.CustomProductResponse
import com.app.ancoturf.data.search.remote.entity.response.RequestProductResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.quote.QuoteRepository
import com.app.ancoturf.domain.search.SearchRepository
import io.reactivex.Single
import javax.inject.Inject

class AddRequestProductsUseCase @Inject constructor(private val searchRepository: SearchRepository) :  BaseUseCase<BaseResponse<RequestProductResponse>>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<RequestProductResponse>> {

        return searchRepository.requestProduct(name = data?.get(UseCaseConstants.NAME) as  String ,
            email = data[UseCaseConstants.EMAIL] as  String , productDescription = data[UseCaseConstants.DESCRIPTIONS] as  String,
            productUrl = data[UseCaseConstants.PRODUCT_URL] as  String)
    }


}