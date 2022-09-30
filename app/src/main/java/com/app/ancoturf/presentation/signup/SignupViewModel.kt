package com.app.ancoturf.presentation.signup

import android.app.Application
import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.R
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.domain.account.usecases.RegisterUseCase
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.utils.Utility
import javax.inject.Inject

class SignupViewModel @Inject constructor(
    app: Application
    , private val registerUseCase: RegisterUseCase
) : SignupvariableViewModel(app) {

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    val _signupLiveData = MutableLiveData<BaseResponse<UserInfo>>()
    val signupLiveData: LiveData<BaseResponse<UserInfo>> get() = _signupLiveData

    fun callRegister() {
        val data = mapOf(
            UseCaseConstants.FIRST_NAME to firstName,
            UseCaseConstants.LAST_NAME to lastName,
            UseCaseConstants.EMAIL to email,
            UseCaseConstants.PASSWORD to password,
            UseCaseConstants.BUSINESS_NAME to businessName,
            UseCaseConstants.ABN to abn,
            UseCaseConstants.PHONE_NUMBER to phoneNumber,
            UseCaseConstants.DEVICE_TOKEN to sharedPrefs?.fcmToken

        )
        registerUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            _signupLiveData.value = it
        }, {
            Utility.cancelProgress()
           _errorLiveData.value = it?.getErrorMessage()
        }).collect()
    }

    fun onClickOfLandscaper() {
        landscaper = !landscaper
    }

    fun checkValidation(): Int {
        firstName.let {
            if (it.isEmpty()) {
                return ErrorConstants.BLANK_FIRST_NAME
            }
        }
        lastName.let {
            if (it.isEmpty()) {
                return ErrorConstants.BLANK_LAST_NAME
            }
        }
        email.let {
            if (it.isEmpty()) {
                return ErrorConstants.BLANK_EMAIL
            }
        }
        email.let {
            if (it.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(it).matches()) {
                return ErrorConstants.INVALID_EMAIL
            }
        }
        password.let {
            if (it.isEmpty()) {
                return ErrorConstants.BLANK_PASSWORD
            }
        }
        password.let {
            if (it.isEmpty() || password.length < 6 || password.length > 50) {
                return ErrorConstants.INVALID_PASSWORD
            }
        }
        if(landscaper) {
            businessName.let {
                if (it.isEmpty()) {
                    return ErrorConstants.BLANK_BUSINESS_NAME
                }
            }
            abn.let {
                if (it.isEmpty()) {
                    return ErrorConstants.BLANK_ABN
                }
            }
            abn.let {
                if (it.isEmpty() || abn.length < 11) {
                    return ErrorConstants.INVALID_ABN
                }
            }
            phoneNumber.let {
                if (it.isEmpty()) {
                    return ErrorConstants.BLANK_PHONE_NUMBER
                }
            }
            phoneNumber.let {
                if (it.isEmpty() || phoneNumber.length < 10) {
                    return ErrorConstants.INVALID_PHONE_NUMBER
                }
            }
        }
        return ErrorConstants.NO_ERROR
    }
}