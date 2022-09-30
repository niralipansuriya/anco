package com.app.ancoturf.domain.quote.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.quote.remote.entity.response.AddEditQuoteResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.quote.QuoteRepository
import io.reactivex.Single
import javax.inject.Inject

class AddEditCustomersUseCase @Inject constructor(private val quoteRepository: QuoteRepository) :  BaseUseCase<BaseResponse<AddEditQuoteResponse>>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<AddEditQuoteResponse>> {

        return quoteRepository.addEditCustomers(quoteId = data?.get(UseCaseConstants.QUOTE_ID) as  Int ,
            customerId = data?.get(UseCaseConstants.CUSTOMER_ID) as  Int , customerName = data?.get(UseCaseConstants.CUSTOMER_NAME) as  String,
            customerAddress = data?.get(UseCaseConstants.CUSTOMER_ADDRESS) as  String , customerEmail = data?.get(UseCaseConstants.CUSTOMER_EMAIL) as  String,
            customerMobile = data?.get(UseCaseConstants.CUSTOMER_MOBILE) as  String, customerPhone = data?.get(UseCaseConstants.CUSTOMER_PHONE) as  String?,
            contactName = data?.get(UseCaseConstants.CONTACT_NAME) as  String ,businessName = data?.get(UseCaseConstants.BUSINESS_NAME) as  String , abn = data?.get(UseCaseConstants.ABN) as  String , address = data?.get(UseCaseConstants.ADDRESS) as  String ,
            mobileNumber = data?.get(UseCaseConstants.MOBILE_NUMBER) as  String , phoneNumber = data?.get(UseCaseConstants.PHONE_NUMBER) as  String? , email = data?.get(UseCaseConstants.EMAIL) as  String ,
            webUrl = data?.get(UseCaseConstants.WEB_URL) as  String? , paymentTerms = data?.get(UseCaseConstants.PAYMENT_TERMS) as  String? , disclaimer = data?.get(UseCaseConstants.DISCLAIMER) as  String? ,
            registeredForGst = data?.get(UseCaseConstants.REGISTERED_FOR_GST) as  String?)
    }


}