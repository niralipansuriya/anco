package com.app.ancoturf.domain.order.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.order.remote.entity.response.OrderDataResponse
import com.app.ancoturf.data.order.remote.entity.response.OrderDetailRequest
import com.app.ancoturf.data.order.remote.entity.response.OrderDetailResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.order.OrderRepository
import io.reactivex.Single
import javax.inject.Inject

class OrderDetailsUseCase @Inject constructor(private val orderRepository: OrderRepository) :
    BaseUseCase<BaseResponse<OrderDetailResponse>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<OrderDetailResponse>> {
        return orderRepository.getOrderDetails(reference_number = data?.get(UseCaseConstants.REFERENCE_NO) as String)
    }

}