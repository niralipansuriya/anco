package com.app.ancoturf.domain.afterpay.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.afterpay.remote.entity.request.OrderRequest
import com.app.ancoturf.data.afterpay.remote.entity.response.OrderResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.account.AccountRepository
import com.app.ancoturf.domain.afterpay.AfterpayRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class OrdersUseCase @Inject constructor(private val afterpayRepository: AfterpayRepository) :  BaseUseCase<OrderResponse>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<OrderResponse> {

        var merchant = OrderRequest.Merchant(redirectConfirmUrl = data!![UseCaseConstants.REDIRECT_CONFIRM_URL] as String , redirectCancelUrl = data!![UseCaseConstants.REDIRECT_CANCEL_URL] as String)
        var consumer = OrderRequest.Consumer(email = data!![UseCaseConstants.EMAIL] as String , phoneNumber = data!![UseCaseConstants.PHONE_NUMBER] as String? , givenNames = data!![UseCaseConstants.GIVEN_NAMES] as String , surname = data!![UseCaseConstants.SURNAME] as String)
        var totalAmount = OrderRequest.TotalAmount(amount = data!![UseCaseConstants.AMOUNT] as String , currency = data!![UseCaseConstants.CURRENCY] as String)
        var orderRequest = OrderRequest(merchant = merchant , consumer = consumer , totalAmount = totalAmount)

        return afterpayRepository.order(orderRequest = orderRequest)
    }
}