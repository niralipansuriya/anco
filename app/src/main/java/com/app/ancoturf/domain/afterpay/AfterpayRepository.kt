package com.app.ancoturf.domain.afterpay

import com.app.ancoturf.data.afterpay.remote.entity.request.CaptureRequest
import com.app.ancoturf.data.afterpay.remote.entity.request.OrderRequest
import com.app.ancoturf.data.afterpay.remote.entity.response.CaptureResponse
import com.app.ancoturf.data.afterpay.remote.entity.response.OrderResponse
import io.reactivex.Single

interface AfterpayRepository {

    fun order(orderRequest: OrderRequest): Single<OrderResponse>

    fun capture(captureRequest: CaptureRequest): Single<CaptureResponse>

}