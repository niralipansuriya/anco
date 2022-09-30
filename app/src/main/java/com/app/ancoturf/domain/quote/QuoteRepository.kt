package com.app.ancoturf.domain.quote

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.portfolio.remote.entity.QuoteAncoProductRequest
import com.app.ancoturf.data.portfolio.remote.entity.QuoteNonAncoProductRequest
import com.app.ancoturf.data.quote.remote.entity.response.*
import io.reactivex.Single

interface QuoteRepository {

    fun getQuotesByCategories(status: String , minCost: String , maxCost: String , address: String , sortBy: String,page : String): Single<BaseResponse<QuotesDataResponse>>

    fun getAllQuotes(): Single<BaseResponse<QuotesDataResponse>>

    fun addEditQuotes(quoteId: Int, customerId: Int, customerName: String, customerAddress: String,
                      customerEmail: String, customerMobile: String, customerPhone: String?,
                      deliveryCost: String?, deliveryDate: String?, products: ArrayList<QuoteAncoProductRequest>?, customProducts: ArrayList<QuoteNonAncoProductRequest>?,
                      deletedProductIds: ArrayList<Int>?, deletedCustomProductIds: ArrayList<Int>?, contactName: String, businessName: String,
                      abn: String, address: String, mobileNumber: String, phoneNumber: String?, email: String, webUrl: String?, paymentTerms: String?,
                      disclaimer: String?, registeredForGst: String?, logoUrl: String?, sendQuoteTo: ArrayList<String>?, deepLinkUrl:String, note:String): Single<BaseResponse<AddEditQuoteResponse>>

    fun addEditBusinessDetails(quoteId: Int, contactName: String , businessName: String,
                      abn: String, address: String, mobileNumber: String, phoneNumber: String?, email: String, webUrl: String?, paymentTerms: String?,
                      disclaimer: String?, registeredForGst: String?, logoUrl: String? , products: ArrayList<QuoteAncoProductRequest>?): Single<BaseResponse<AddEditQuoteResponse>>

    fun addEditCustomers(quoteId: Int, customerId: Int, customerName: String, customerAddress: String,
                      customerEmail: String, customerMobile: String, customerPhone: String?, contactName: String , businessName: String,
                         abn: String, address: String, mobileNumber: String, phoneNumber: String?, email: String, webUrl: String?, paymentTerms: String?,
                         disclaimer: String?, registeredForGst: String?): Single<BaseResponse<AddEditQuoteResponse>>

    fun getCustomers(): Single<BaseResponse<CustomersDataResponse>>

    fun getQuoteDetails(quoteId: Int): Single<BaseResponse<QuoteDetailsResponse>>

    fun addCustomProducts(customProductId: Int , name: String , descriptions: String , price: String , imagePath: String?): Single<BaseResponse<CustomProductResponse>>

    fun resendQuote(quoteId: Int, sendQuoteTo: ArrayList<String?>?): Single<BaseResponse<AddEditQuoteResponse>>

    fun duplicateQuote(quoteId: Int): Single<BaseResponse<DuplicateQuoteResponse>>
}
