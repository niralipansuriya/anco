package com.app.ancoturf.presentation.payment

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.cart.remote.entity.OrderDetails
import com.app.ancoturf.data.common.exceptions.AncoHttpException
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.payment.remote.entity.BillHistoryResponse
import com.app.ancoturf.data.payment.remote.entity.PendingPaymentResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.payment.usecases.BillHistoryUseCase
import com.app.ancoturf.domain.payment.usecases.PendingPaymentUseCase
import com.app.ancoturf.domain.payment.usecases.UpdatePaymentUseCase
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import javax.inject.Inject

class PaymentViewModel @Inject constructor(
    val app: Application
    , val pendingPaymentUseCase: PendingPaymentUseCase,
    val billHistoryUseCase: BillHistoryUseCase,
    val updatePaymentUseCase: UpdatePaymentUseCase
) : BaseObservableViewModel(app) {

    val _pendingPaymentLiveData = MutableLiveData<PendingPaymentResponse>()
    val pendingPaymentLiveData: LiveData<PendingPaymentResponse> get() = _pendingPaymentLiveData

    val _updatePaymentLiveData = MutableLiveData<BaseResponse<OrderDetails>>()
    val updatePaymentLiveData: LiveData<BaseResponse<OrderDetails>> get() = _updatePaymentLiveData

    val _billHistoryLiveData = MutableLiveData<BillHistoryResponse>()
    val billHistoryLiveData: LiveData<BillHistoryResponse> get() = _billHistoryLiveData

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    fun callGetPendingPayment() {
        pendingPaymentUseCase.execute().customSubscribe({
            Utility.cancelProgress()
            if (it.success && it.data != null)
                _pendingPaymentLiveData.value = it.data
        }, {
            Utility.cancelProgress()
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        }).collect()
    }

    fun updatePayment(orderIds : String,paymentMethod : String ,paymentDetails : String)
    {
        val data = mapOf(UseCaseConstants.ORDER_ID to orderIds,UseCaseConstants.PAYMENT_METHOD to paymentMethod,UseCaseConstants.PAYMENT_DETAILS to paymentDetails)

        updatePaymentUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            if (it.success)
                _updatePaymentLiveData.value = it
        }, {
            Utility.cancelProgress()
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        }).collect()
    }

    fun callGetBillHistory(page : String) {
        val data = mapOf(UseCaseConstants.PER_PAGE to page)

        billHistoryUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            if (it.success && it.data?.data != null)
            {
                _billHistoryLiveData.value = it.data
                _isNextPageUrl.value = it.data.nextPageUrl != null
            }
        }, {
            Utility.cancelProgress()
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        }).collect()
    }
}
