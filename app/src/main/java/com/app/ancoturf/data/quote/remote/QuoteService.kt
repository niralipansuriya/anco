package com.app.ancoturf.data.quote.remote

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.quote.remote.entity.response.*
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface QuoteService {

    @GET("api/quotes")
    fun getQuotesByCategories(
        @Query("status") status: String,
        @Query("cost_min") minCost: String,
        @Query("cost_max") maxCost: String,
        @Query("address") address: String,
        @Query("sort_by") sortBy: String,
        @Query("page") page: String
    ): Single<BaseResponse<QuotesDataResponse>>

    @GET("api/quotes")
    fun getAllQuotes(): Single<BaseResponse<QuotesDataResponse>>

    @Multipart
    @POST("api/quote")
    fun addEditQuotes(
        @Part("quote_id") quoteId: Int,
        @Part("customer_id") customerId: Int,
        @Part("customer_name") customerName: RequestBody,
        @Part("customer_address") customerAddress: RequestBody,
        @Part("customer_email") customerEmail: RequestBody,
        @Part("customer_mobile") customerMobile: RequestBody,
        @Part("customer_phone") customerPhone: RequestBody,
        @Part("delivery_cost") deliveryCost: RequestBody,
        @Part("delivery_date") deliveryDate: RequestBody,
        @Part("products") products: RequestBody,
        @Part("custom_products") customProducts: RequestBody,
        @Part("deleted_product_ids[]") deletedProductIds: Array<Int?>,
        @Part("deleted_custom_product_ids[]") deletedCustomProductIds: Array<Int?>,
        @Part("contact_name") contactName: RequestBody,
        @Part("business_name") businessName: RequestBody,
        @Part("abn") abn: RequestBody,
        @Part("address") address: RequestBody,
        @Part("mobile_number") mobileNumber: RequestBody,
        @Part("phone_number") phoneNumber: RequestBody,
        @Part("email") email: RequestBody,
        @Part("web_url") webUrl: RequestBody,
        @Part("payment_terms") paymentTerms: RequestBody,
        @Part("disclaimer") disclaimer: RequestBody,
        @Part("registered_for_gst") registeredForGst: RequestBody,
        @Part logoUrl: MultipartBody.Part?,
        @Part("send_quote_to[]") sendQuoteTo: Array<RequestBody?>,
        @Part("deeplink_url") deepLinkUrl:RequestBody,
        @Part("note") note: RequestBody
    ): Single<BaseResponse<AddEditQuoteResponse>>


    @Multipart
    @POST("api/quote")
    fun addEditBusinessDetails(
        @Part("quote_id") quoteId: Int,
        @Part("contact_name") contactName: RequestBody,
        @Part("business_name") businessName: RequestBody,
        @Part("abn") abn: RequestBody,
        @Part("address") address: RequestBody,
        @Part("mobile_number") mobileNumber: RequestBody,
        @Part("phone_number") phoneNumber: RequestBody,
        @Part("email") email: RequestBody,
        @Part("web_url") webUrl: RequestBody,
        @Part("payment_terms") paymentTerms: RequestBody,
        @Part("disclaimer") disclaimer: RequestBody,
        @Part("registered_for_gst") registeredForGst: RequestBody,
        @Part("products") products: RequestBody,
        @Part logoUrl: MultipartBody.Part?
    ): Single<BaseResponse<AddEditQuoteResponse>>


    @GET("api/customers")
    fun getCustomers(): Single<BaseResponse<CustomersDataResponse>>


    @Multipart
    @POST("api/quote")
    fun addEditCustomer(
        @Part("quote_id") quoteId: Int,
        @Part("customer_id") customerId: Int,
        @Part("customer_name") customerName: RequestBody,
        @Part("customer_address") customerAddress: RequestBody,
        @Part("customer_email") customerEmail: RequestBody,
        @Part("customer_mobile") customerMobile: RequestBody,
        @Part("customer_phone") customerPhone: RequestBody,
        @Part("contact_name") contactName: RequestBody,
        @Part("business_name") businessName: RequestBody,
        @Part("abn") abn: RequestBody,
        @Part("address") address: RequestBody,
        @Part("mobile_number") mobileNumber: RequestBody,
        @Part("phone_number") phoneNumber: RequestBody,
        @Part("email") email: RequestBody,
        @Part("web_url") webUrl: RequestBody,
        @Part("payment_terms") paymentTerms: RequestBody,
        @Part("disclaimer") disclaimer: RequestBody,
        @Part("registered_for_gst") registeredForGst: RequestBody
    ): Single<BaseResponse<AddEditQuoteResponse>>

    @GET("api/quote/{quoteId}")
    fun getQuoteDetail(@Path("quoteId") quoteId: Int): Single<BaseResponse<QuoteDetailsResponse>>

    @GET("api/custom_product")
    fun addCustomProduct(@Path("quoteId") quoteId: Int): Single<BaseResponse<QuoteDetailsResponse>>


    @Multipart
    @POST("api/custom_product")
    fun addCustomProduct(
        @Part("custom_product_id") customProductId: Int,
        @Part("name") name: RequestBody,
        @Part("price") price: RequestBody,
        @Part("descriptions") descriptions: RequestBody,
        @Part image: MultipartBody.Part?
    ): Single<BaseResponse<CustomProductResponse>>

    @Multipart
    @POST("api/resend_quote")
    fun resendQuote(
        @Part("quote_id") quoteId: Int,
        @Part("send_quote_to[]") sendQuoteTo: Array<RequestBody?>
    ): Single<BaseResponse<AddEditQuoteResponse>>

    @FormUrlEncoded
    @POST("api/duplicate_quote")
    fun duplicateQuote(
        @Field("quote_id") quoteId: Int
    ): Single<BaseResponse<DuplicateQuoteResponse>>
}