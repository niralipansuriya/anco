package com.app.ancoturf.presentation.manageLawn

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.exceptions.AncoHttpException
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.lawntips.remote.LawnTipsDataResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDetailResponse
import com.app.ancoturf.data.manageLawn.remote.ManageLawnDataResponse
import com.app.ancoturf.data.manageLawn.remote.ManageLawnDetailResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.lawntips.usecases.LawnTipsDetailUseCase
import com.app.ancoturf.domain.lawntips.usecases.LawnTipsUseCase
import com.app.ancoturf.domain.manageLawn.usecases.ManageLawnDetailUseCase
import com.app.ancoturf.domain.manageLawn.usecases.ManageLawnUsecase
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import javax.inject.Inject

class ManageLawnViewModel @Inject constructor(
    val app: Application,
    var manageLawnUsecase: ManageLawnUsecase,
    var manageLawnDetailUseCase : ManageLawnDetailUseCase
) : BaseObservableViewModel(app) {

    val _manageLawnLiveData = MutableLiveData<ManageLawnDataResponse>()
    val manageLawnLiveData: LiveData<ManageLawnDataResponse> get() = _manageLawnLiveData

    val _manageLawnDetailsLiveData = MutableLiveData<ManageLawnDetailResponse>()
    val manageLawnDetailsLiveData: LiveData<ManageLawnDetailResponse> get() = _manageLawnDetailsLiveData

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    fun getMyLawn(page: String) {
        val data = mutableMapOf(UseCaseConstants.PER_PAGE to page)

        manageLawnUsecase.execute(data).customSubscribe({
            Utility.cancelProgress()

            if (it.success && it.data != null) {
                _manageLawnLiveData.value = it
//                _isNextPageUrl.value = it.data.nextPageUrl != null
            } else
                _errorLiveData.value = it.message

        },
            {
                Utility.cancelProgress()
                if (it is AncoHttpException.AncoUnauthorizedException) {
                    _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
                } else {
                    _errorLiveData.value = it?.getErrorMessage()
                }
            }
        ).collect()
    }

    fun getMyLawnById(id: Int) {
        val data = mapOf(UseCaseConstants.MANAGE_LAWN_ID to id)
        manageLawnDetailUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            if (it.success)
                _manageLawnDetailsLiveData.value = it
            else
                _errorLiveData.value = it.message
        }, {
            Utility.cancelProgress()
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        })
            .collect()
    }

}
