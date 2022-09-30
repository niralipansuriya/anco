package com.app.ancoturf.data.payment

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.cart.remote.entity.OrderDetails
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.data.payment.remote.PaymentService
import com.app.ancoturf.data.payment.remote.entity.BillHistoryResponse
import com.app.ancoturf.data.payment.remote.entity.PendingPaymentResponse
import com.app.ancoturf.data.portfolio.remote.PortfolioService
import com.app.ancoturf.data.portfolio.remote.entity.*
import com.app.ancoturf.domain.payment.PaymentRepository
import com.app.ancoturf.domain.portfolio.PortfolioRepository
import com.google.gson.Gson
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject


class PaymentDataRepository @Inject constructor() : PaymentRepository, CommonService<PaymentService>() {

    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = PaymentService::class.java

    override fun getPendingPayments(): Single<BaseResponse<PendingPaymentResponse>> {
        return networkService.getPendingPayments().map {
            it
        }
    }

    override fun getBillHistory(page : String): Single<BaseResponse<BillHistoryResponse>> {
        return networkService.getBillHistory(page).map {
            it
        }
    }

    override fun updatePayment(orderIds : String,payment_method : String, payment_details: String): Single<BaseResponse<OrderDetails>> {
        return networkService.updatePayment(orderIds = orderIds ,payment_method  = payment_method ,payment_details = payment_details ).map {
            it
        }
    }
}