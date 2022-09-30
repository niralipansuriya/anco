package com.app.ancoturf.data.order.remote

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.order.remote.entity.response.OrderDataResponse
import com.app.ancoturf.data.order.remote.entity.response.OrderDetailRequest
import com.app.ancoturf.data.order.remote.entity.response.OrderDetailResponse
import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.*

interface OrderService {

    @GET("api/orders")
    fun getOrderByCategories(
        @Query("order_status_id") orderStatus: String,
        @Query("delivery_status_id") deliveryStatus : String,
        @Query("price_min") minPrice: String,
        @Query("price_max") maxPrice: String,
        @Query("address") address: String,
        @Query("sort_by") sortBy: String,
        @Query("delivery_date_from") deliveryDateFrom: String,
        @Query("page") page: String
    ): Single<BaseResponse<OrderDataResponse>>

    @GET("api/orders")
    fun getAllOrders(): Single<BaseResponse<OrderDataResponse>>

    @POST("api/order/detail")
    fun getOrderDetails(@Body order_detail_request: JsonObject): Single<BaseResponse<OrderDetailResponse>>

    @FormUrlEncoded
    @POST("api/order/update_delivery_date")
    fun rescheduleOrder(@Field("order_id") orderId: Int,
                        @Field("new_delivery_date") newDeliveryDate: String): Single<BaseResponse<OrderDetailResponse>>

    @FormUrlEncoded
    @POST("api/order/cancel")
    fun cancelOrder(@Field("order_id") orderId: Int): Single<BaseResponse<OrderDetailResponse>>

}