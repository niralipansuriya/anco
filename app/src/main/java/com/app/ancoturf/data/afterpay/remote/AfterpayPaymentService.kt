package com.app.ancoturf.data.afterpay.remote

import com.app.ancoturf.data.afterpay.remote.entity.request.CaptureRequest
import com.app.ancoturf.data.afterpay.remote.entity.request.OrderRequest
import com.app.ancoturf.data.afterpay.remote.entity.response.CaptureResponse
import com.app.ancoturf.data.afterpay.remote.entity.response.OrderResponse
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface AfterpayPaymentService {

    @POST("orders")
    fun orders(@Body orderRequest: OrderRequest): Single<OrderResponse>

    @POST("payments/capture")
    fun capture(@Body captureRequest: CaptureRequest): Single<CaptureResponse>

}