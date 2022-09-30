package com.app.ancoturf.data.order

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.data.order.remote.OrderService
import com.app.ancoturf.data.order.remote.entity.response.OrderDataResponse
import com.app.ancoturf.data.order.remote.entity.response.OrderDetailRequest
import com.app.ancoturf.data.order.remote.entity.response.OrderDetailResponse
import com.app.ancoturf.domain.order.OrderRepository
import com.google.gson.JsonObject
import io.reactivex.Single
import javax.inject.Inject

class OrderDataRepository @Inject constructor() : OrderRepository, CommonService<OrderService>() {

    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = OrderService::class.java

    override fun getOrderByCategories(
        orderStatus: String,
        deliveryStatus: String,
        minPrice: String,
        maxPrice: String,
        address: String,
        sortBy: String,
        deliveryDateFrom: String,
        page: String
    ): Single<BaseResponse<OrderDataResponse>> {
        return networkService.getOrderByCategories(
            orderStatus,
            deliveryStatus,
            minPrice,
            maxPrice,
            address,
            sortBy,
            deliveryDateFrom,
            page
        )
    }

    override fun getAllOrders(): Single<BaseResponse<OrderDataResponse>> {
        return networkService.getAllOrders().map {
            it
        }
    }

    override fun getOrderDetails(reference_number: String): Single<BaseResponse<OrderDetailResponse>> {
        val addReviewRequest = OrderDetailRequest(referenceNumber = reference_number)
        val jsonObject: JsonObject = createJsonObject(addReviewRequest)
        return networkService.getOrderDetails(jsonObject).map {
            it
        }
    }

    private fun createJsonObject(order_detail_request: OrderDetailRequest): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("reference_number", order_detail_request.referenceNumber)
        return jsonObject
    }

    override fun rescheduleOrder(
        orderId: Int,
        newDeliveryDate: String
    ): Single<BaseResponse<OrderDetailResponse>> {
        return networkService.rescheduleOrder(orderId, newDeliveryDate).map {
            it
        }
    }

    override fun cancelOrder(orderId: Int): Single<BaseResponse<OrderDetailResponse>> {
        return networkService.cancelOrder(orderId).map {
            it
        }
    }

}