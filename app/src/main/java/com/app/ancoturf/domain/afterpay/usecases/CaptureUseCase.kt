package com.app.ancoturf.domain.afterpay.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.afterpay.remote.entity.request.CaptureRequest
import com.app.ancoturf.data.afterpay.remote.entity.request.OrderRequest
import com.app.ancoturf.data.afterpay.remote.entity.response.CaptureResponse
import com.app.ancoturf.data.afterpay.remote.entity.response.OrderResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.account.AccountRepository
import com.app.ancoturf.domain.afterpay.AfterpayRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class CaptureUseCase @Inject constructor(private val afterpayRepository: AfterpayRepository) :  BaseUseCase<CaptureResponse>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<CaptureResponse> {

        var captureRequest = CaptureRequest(token = data!![UseCaseConstants.TOKEN] as String , merchantReference = data!![UseCaseConstants.MERCHANT_REFERENCE] as String)

        return afterpayRepository.capture(captureRequest = captureRequest)
    }
}