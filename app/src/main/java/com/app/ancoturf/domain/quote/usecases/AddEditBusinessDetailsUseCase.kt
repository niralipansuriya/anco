package com.app.ancoturf.domain.quote.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.portfolio.remote.entity.QuoteAncoProductRequest
import com.app.ancoturf.data.quote.remote.entity.response.AddEditQuoteResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.quote.QuoteRepository
import com.app.ancoturf.utils.Utility
import io.reactivex.Single
import javax.inject.Inject

class AddEditBusinessDetailsUseCase @Inject constructor(private val quoteRepository: QuoteRepository) :  BaseUseCase<BaseResponse<AddEditQuoteResponse>>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<AddEditQuoteResponse>> {
        var logoUrl: String? = null
        if(!Utility.isValueNull(data?.get(UseCaseConstants.LOGO_URL) as String?))
            logoUrl = data?.get(UseCaseConstants.LOGO_URL) as String?
        return quoteRepository.addEditBusinessDetails(quoteId = data?.get(UseCaseConstants.QUOTE_ID) as  Int ,
           contactName = data?.get(UseCaseConstants.CONTACT_NAME) as  String ,businessName = data?.get(UseCaseConstants.BUSINESS_NAME) as  String , abn = data?.get(UseCaseConstants.ABN) as  String , address = data?.get(UseCaseConstants.ADDRESS) as  String ,
            mobileNumber = data?.get(UseCaseConstants.MOBILE_NUMBER) as  String , phoneNumber = data?.get(UseCaseConstants.PHONE_NUMBER) as  String , email = data?.get(UseCaseConstants.EMAIL) as  String ,
            webUrl = data?.get(UseCaseConstants.WEB_URL) as  String , paymentTerms = data?.get(UseCaseConstants.PAYMENT_TERMS) as  String , disclaimer = data?.get(UseCaseConstants.DISCLAIMER) as  String ,
            registeredForGst = data?.get(UseCaseConstants.REGISTERED_FOR_GST) as  String ,logoUrl = logoUrl , products = data?.get(UseCaseConstants.PRODUCTS) as ArrayList<QuoteAncoProductRequest>)
    }


}