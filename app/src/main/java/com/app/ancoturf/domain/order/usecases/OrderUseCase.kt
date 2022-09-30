package com.app.ancoturf.domain.order.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.order.remote.entity.response.OrderDataResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.order.OrderRepository
import io.reactivex.Single
import javax.inject.Inject

class OrderUseCase @Inject constructor(private val orderRepository: OrderRepository) :
    BaseUseCase<BaseResponse<OrderDataResponse>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<OrderDataResponse>> {
        return if (data != null)
            orderRepository.getOrderByCategories(
                orderStatus = data[UseCaseConstants.ORDER_STATUS] as String,
                deliveryStatus = data[UseCaseConstants.DELIVERY_STATUS] as String,
                minPrice = data[UseCaseConstants.PRICE_MIN] as String,
                maxPrice = data[UseCaseConstants.PRICE_MAX] as String,
                address = data[UseCaseConstants.ADDRESS] as String,
                sortBy = data[UseCaseConstants.SORT_BY] as String,
                deliveryDateFrom = data[UseCaseConstants.DELIVERY_DATE_FROM] as String,
                page = data[UseCaseConstants.PER_PAGE] as String
            )
        else
            orderRepository.getAllOrders()
    }

}