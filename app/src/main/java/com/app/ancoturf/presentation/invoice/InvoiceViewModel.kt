package com.app.ancoturf.presentation.invoice

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.exceptions.AncoHttpException
import com.app.ancoturf.data.invoice.remote.entity.response.InvoiceDataResponse
import com.app.ancoturf.data.notification.remote.entity.response.NotificationDataResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.invoice.usecase.InvoiceUseCase
import com.app.ancoturf.domain.notification.usecases.NotificationUseCase
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import javax.inject.Inject

class InvoiceViewModel @Inject constructor(
    val app: Application,
    val invoiceUseCase: InvoiceUseCase
) : BaseObservableViewModel(app) {
    val _invoiceLiveData = MutableLiveData<BaseResponse<InvoiceDataResponse>>()
    val invoiceLiveData : LiveData<BaseResponse<InvoiceDataResponse>> get() = _invoiceLiveData

    fun callGetInvoicePDF(invoiceId : String){
        val data = mapOf(UseCaseConstants.InvoiceId to invoiceId)
        invoiceUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            if (it.success)
            {
                _invoiceLiveData.value = it
            }
        },{
            Utility.cancelProgress()
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        }).collect()
    }

}