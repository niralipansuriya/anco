package com.app.ancoturf.domain.quote.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.portfolio.remote.entity.QuoteAncoProductRequest
import com.app.ancoturf.data.portfolio.remote.entity.QuoteNonAncoProductRequest
import com.app.ancoturf.data.quote.remote.entity.response.AddEditQuoteResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.quote.QuoteRepository
import com.app.ancoturf.utils.Utility
import io.reactivex.Single
import javax.inject.Inject

class AddEditQuotesUseCase @Inject constructor(private val quoteRepository: QuoteRepository) :  BaseUseCase<BaseResponse<AddEditQuoteResponse>>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<AddEditQuoteResponse>> {
        var logoUrl: String? = null
        if(!Utility.isValueNull(data?.get(UseCaseConstants.LOGO_URL) as String?))
            logoUrl = data?.get(UseCaseConstants.LOGO_URL) as String?
        return quoteRepository.addEditQuotes(quoteId = data?.get(UseCaseConstants.QUOTE_ID) as  Int ,
            customerId = data?.get(UseCaseConstants.CUSTOMER_ID) as  Int , customerName = data?.get(UseCaseConstants.CUSTOMER_NAME) as  String,
            customerAddress = data?.get(UseCaseConstants.CUSTOMER_ADDRESS) as  String , customerEmail = data?.get(UseCaseConstants.CUSTOMER_EMAIL) as  String,
            customerMobile = data?.get(UseCaseConstants.CUSTOMER_MOBILE) as  String, customerPhone = data?.get(UseCaseConstants.CUSTOMER_PHONE) as  String ,
            deliveryCost = data?.get(UseCaseConstants.DELIVERY_COST) as  String , deliveryDate = data?.get(UseCaseConstants.DELIVERY_DATE) as  String ,
            products = data?.get(UseCaseConstants.PRODUCTS) as ArrayList<QuoteAncoProductRequest> , customProducts = data?.get(UseCaseConstants.CUSTOM_PRODUCTS) as ArrayList<QuoteNonAncoProductRequest>,
            deletedProductIds = data?.get(UseCaseConstants.DELETED_PRODUCT_IDS) as ArrayList<Int> ,  deletedCustomProductIds = data?.get(UseCaseConstants.DELETED_CUSTOM_PRODUCT_IDS) as ArrayList<Int> ,
            contactName = data?.get(UseCaseConstants.CONTACT_NAME) as  String ,businessName = data?.get(UseCaseConstants.BUSINESS_NAME) as  String , abn = data?.get(UseCaseConstants.ABN) as  String , address = data?.get(UseCaseConstants.ADDRESS) as  String ,
            mobileNumber = data?.get(UseCaseConstants.MOBILE_NUMBER) as  String , phoneNumber = data?.get(UseCaseConstants.PHONE_NUMBER) as  String , email = data?.get(UseCaseConstants.EMAIL) as  String ,
            webUrl = data?.get(UseCaseConstants.WEB_URL) as  String , paymentTerms = data?.get(UseCaseConstants.PAYMENT_TERMS) as  String , disclaimer = data?.get(UseCaseConstants.DISCLAIMER) as  String ,
            registeredForGst = data?.get(UseCaseConstants.REGISTERED_FOR_GST) as  String ,logoUrl = logoUrl , sendQuoteTo = data?.get(UseCaseConstants.SEND_QUOTE_TO) as  ArrayList<String>,
            deepLinkUrl = data?.get(UseCaseConstants.DEEP_LINK_URL) as String,note = data?.get(UseCaseConstants.NOTE) as String)
    }


}