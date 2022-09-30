package com.app.ancoturf.domain.quote.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.quote.remote.entity.response.CustomProductResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.quote.QuoteRepository
import io.reactivex.Single
import javax.inject.Inject

class AddCustomProductsUseCase @Inject constructor(private val quoteRepository: QuoteRepository) :  BaseUseCase<BaseResponse<CustomProductResponse>>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<CustomProductResponse>> {

        return quoteRepository.addCustomProducts(customProductId = data?.get(UseCaseConstants.CUSTOM_PRODUCT_ID) as  Int ,
            name = data?.get(UseCaseConstants.NAME) as  String , descriptions = data?.get(UseCaseConstants.DESCRIPTIONS) as  String,
            price = data?.get(UseCaseConstants.PRICE) as  String , imagePath = data?.get(UseCaseConstants.IMAGE) as  String?)
    }


}