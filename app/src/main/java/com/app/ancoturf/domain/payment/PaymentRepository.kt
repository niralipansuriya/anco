package com.app.ancoturf.domain.payment

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.cart.remote.entity.OrderDetails
import com.app.ancoturf.data.payment.remote.entity.BillHistoryResponse
import com.app.ancoturf.data.payment.remote.entity.PendingPaymentResponse
import com.app.ancoturf.data.portfolio.remote.entity.*
import io.reactivex.Single

interface PaymentRepository {

    fun getPendingPayments(): Single<BaseResponse<PendingPaymentResponse>>

    fun getBillHistory(page : String): Single<BaseResponse<BillHistoryResponse>>

    fun updatePayment(orderIds : String,payment_method : String, payment_details: String): Single<BaseResponse<OrderDetails>>

}