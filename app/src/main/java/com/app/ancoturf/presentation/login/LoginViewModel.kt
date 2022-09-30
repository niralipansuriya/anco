package com.app.ancoturf.presentation.login

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.BR
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.domain.account.usecases.LoginUseCase
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    val app: Application
    , private val loginUseCase: LoginUseCase
) : BaseObservableViewModel(app) {

    private val _loginLiveData = MutableLiveData<UserInfo>()
    val loginLiveData: LiveData<UserInfo> get() = _loginLiveData

    @Bindable
    var userEmail: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.userEmail)
        }

    @Bindable
    var password: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.password)
        }

    fun callLogin(deviceToken:String) {
        val data = mapOf(
            UseCaseConstants.EMAIL to userEmail,
            UseCaseConstants.PASSWORD to password,
            UseCaseConstants.DEVICE_TOKEN to deviceToken
        )

        loginUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            Log.e("success", "${it.success}")
            if (it.success)
                _loginLiveData.value = it.data
            else
                _errorLiveData.value = it.message
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _errorLiveData.value = it?.getErrorMessage()
        }).collect()
    }

    fun callSkipLogin() {
    }


    fun checkValidation(): Int {
        userEmail.let {
            if (it.isEmpty()) {
                return ErrorConstants.BLANK_EMAIL
            }
        }
        userEmail.let {
            if (it.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                return ErrorConstants.INVALID_EMAIL
            }
        }
        password.let {
            if (it.isEmpty()) {
                return ErrorConstants.BLANK_PASSWORD
            }
        }
//        password.let {
//            if (it.isEmpty() || password.length < 6 || password.length > 12) {
//                return ErrorConstants.INVALID_PASSWORD
//            }
//        }
        return ErrorConstants.NO_ERROR
    }
}

