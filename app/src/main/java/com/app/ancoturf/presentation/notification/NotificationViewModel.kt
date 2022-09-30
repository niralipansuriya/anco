package com.app.ancoturf.presentation.notification

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Insert
import com.app.ancoturf.data.common.exceptions.AncoHttpException
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.notification.remote.entity.response.NotificationDataResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.notification.usecases.NotificationUseCase
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import javax.inject.Inject

class NotificationViewModel @Inject constructor(
    val app: Application,
    val notificationUseCase: NotificationUseCase
) :BaseObservableViewModel(app) {
    @Inject
    lateinit var sharedPrefs: SharedPrefs

    /*  val _billHistoryLiveData = MutableLiveData<BillHistoryResponse>()
    val billHistoryLiveData: LiveData<BillHistoryResponse> get() = _billHistoryLiveData*/

   val _notificationLiveData = MutableLiveData<NotificationDataResponse>()
    val notificationLiveData : LiveData<NotificationDataResponse> get() = _notificationLiveData

    fun callGetNotification(page : String){
        val data = mapOf(UseCaseConstants.PER_PAGE to page)
        notificationUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            if (it.success && it.data?.data != null)
            {
                _notificationLiveData.value = it.data
                _isNextPageUrl.value = it.data.nextPageUrl != null
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