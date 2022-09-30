package com.app.ancoturf.data.afterpay

import com.app.ancoturf.data.afterpay.remote.AfterpayPaymentService
import com.app.ancoturf.data.afterpay.remote.entity.request.CaptureRequest
import com.app.ancoturf.data.afterpay.remote.entity.request.OrderRequest
import com.app.ancoturf.data.afterpay.remote.entity.response.CaptureResponse
import com.app.ancoturf.data.afterpay.remote.entity.response.OrderResponse
import com.app.ancoturf.data.common.network.AfterpayService
import com.app.ancoturf.domain.afterpay.AfterpayRepository
import io.reactivex.Single
import javax.inject.Inject

class AfterpayDataRepository @Inject constructor() : AfterpayRepository, AfterpayService<AfterpayPaymentService>() {

    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = AfterpayPaymentService::class.java

    override fun order(orderRequest: OrderRequest): Single<OrderResponse> {
        return networkService.orders(orderRequest).map {
            it
        }
    }

    override fun capture(captureRequest: CaptureRequest): Single<CaptureResponse> {
        return networkService.capture(captureRequest).map {
            it
        }
    }
}