package com.app.ancoturf.domain.order

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.order.remote.entity.response.OrderDataResponse
import com.app.ancoturf.data.order.remote.entity.response.OrderDetailRequest

import com.app.ancoturf.data.order.remote.entity.response.OrderDetailResponse
import com.app.ancoturf.data.quote.remote.entity.response.QuotesDataResponse
import io.reactivex.Single


interface OrderRepository {
    fun getOrderByCategories(
        orderStatus: String,
        deliveryStatus: String,
        minPrice: String,
        maxPrice: String,
        address: String,
        sortBy: String,
        deliveryDateFrom: String,
        page : String
    ): Single<BaseResponse<OrderDataResponse>>

    fun getAllOrders(): Single<BaseResponse<OrderDataResponse>>

    fun getOrderDetails(reference_number: String): Single<BaseResponse<OrderDetailResponse>>

    fun rescheduleOrder(orderId: Int , newDeliveryDate: String): Single<BaseResponse<OrderDetailResponse>>

    fun cancelOrder(orderId: Int): Single<BaseResponse<OrderDetailResponse>>
}