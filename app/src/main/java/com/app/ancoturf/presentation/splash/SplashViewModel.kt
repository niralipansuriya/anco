package com.app.ancoturf.presentation.splash

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.domain.setting.usecases.SettingUseCase
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import javax.inject.Inject

class SplashViewModel @Inject constructor(
    app: Application
    , private val settingUseCase: SettingUseCase
) : BaseObservableViewModel(app) {

    private val _settingsLiveData = MutableLiveData<BaseResponse<SettingsResponse.Data>>()
    val settingsLiveData: LiveData<BaseResponse<SettingsResponse.Data>> get() = _settingsLiveData

    fun callGetSettings() {
        settingUseCase.execute().customSubscribe({
            if (it.success) {
                _settingsLiveData.value = it
            } else {
                _errorLiveData.value = it.message
            }
        }, {
            _errorLiveData.value = it?.getErrorMessage()
        }).collect()
    }
}