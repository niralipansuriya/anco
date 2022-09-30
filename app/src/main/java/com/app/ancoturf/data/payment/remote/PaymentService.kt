package com.app.ancoturf.data.payment.remote

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.cart.remote.entity.OrderDetails
import com.app.ancoturf.data.payment.remote.entity.BillHistoryResponse
import com.app.ancoturf.data.payment.remote.entity.PendingPaymentResponse
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*

interface PaymentService {

    @GET("api/pending_payments")
    fun getPendingPayments(): Single<BaseResponse<PendingPaymentResponse>>

    @GET("api/bill_history")
    fun getBillHistory(@Query("page") page: String): Single<BaseResponse<BillHistoryResponse>>

    @FormUrlEncoded
    @POST("api/update_payment")
    fun updatePayment(
        @Field("order_ids") orderIds: String,
        @Field("payment_method") payment_method: String,
        @Field("payment_details") payment_details: String?,
        @Field("partial_payment") partial_payment:String="1"
    ): Single<BaseResponse<OrderDetails>>


    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("single-use-tokens")
    fun singleUseTokens(
        @Header("Authorization") auth: String, @FieldMap data: Map<String, String>
    ): Call<String>


    @GET("merchants")
    fun merchantCall(@Header("Authorization") auth: String): Call<String>

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("customers")
    fun createCustomer(@Header("Authorization") auth: String, @FieldMap data: HashMap<String, String>): Call<String>

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded;charset=utf-8")
    @POST("transactions")
    fun transactionApi(@Header("Authorization") auth: String, @FieldMap data: HashMap<String, String>): Call<String>


}