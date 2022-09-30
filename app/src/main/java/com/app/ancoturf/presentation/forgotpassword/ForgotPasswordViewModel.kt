package com.app.ancoturf.presentation.forgotpassword

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.databinding.Bindable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.BR
import com.app.ancoturf.domain.account.usecases.ForgotPasswordUseCase
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import javax.inject.Inject

class ForgotPasswordViewModel @Inject constructor(
    val app: Application
    , private val forgotPasswordUseCase: ForgotPasswordUseCase
) : BaseObservableViewModel(app) {

    private val _forgotPasswordLiveData = MutableLiveData<String>()
    val forgotPasswordLiveData: LiveData<String> get() = _forgotPasswordLiveData

    @Bindable
    var userEmail: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.userEmail)

        }

    fun callForgotPassword() {
        val data = mapOf(
            UseCaseConstants.EMAIL to userEmail
        )

        forgotPasswordUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _forgotPasswordLiveData.value = it.message
            else
                _errorLiveData.value = it.message
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _errorLiveData.value = it?.getErrorMessage()
        }).collect()
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
        return ErrorConstants.NO_ERROR
    }
}

