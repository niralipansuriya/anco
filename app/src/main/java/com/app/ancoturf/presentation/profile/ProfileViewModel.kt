package com.app.ancoturf.presentation.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.common.exceptions.AncoHttpException
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.domain.account.usecases.ChangePasswordUseCase
import com.app.ancoturf.domain.account.usecases.LogoutUseCase
import com.app.ancoturf.domain.account.usecases.UpdateProfileUseCase
import com.app.ancoturf.domain.account.usecases.UserDetailUseCase
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    val app: Application
    , val userDetailUseCase: UserDetailUseCase
    , val changePasswordUseCase: ChangePasswordUseCase
    , val updateProfileUseCase: UpdateProfileUseCase
    , val logoutUseCase: LogoutUseCase
) : BaseObservableViewModel(app) {

    val _userInfoLiveData = MutableLiveData<UserInfo>()
    val userInfoLiveData: LiveData<UserInfo> get() = _userInfoLiveData

    val _changePasswordLiveData = MutableLiveData<BaseResponse<Any>>()
    val changePasswordLiveData: LiveData<BaseResponse<Any>> get() = _changePasswordLiveData

    val _logoutLiveData = MutableLiveData<BaseResponse<Any>>()
    val logoutLiveData: LiveData<BaseResponse<Any>> get() = _logoutLiveData

    @Inject
    lateinit var sharedPrefs: SharedPrefs


    fun callGetUserDetails() {
        userDetailUseCase.execute().customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _userInfoLiveData.value = it.data
            else
                _errorLiveData.value = it.message
        }, {
            Utility.cancelProgress()
            Log.e("failure", "$it")
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        }).collect()
    }

    public fun logout(token: String) {
        val data = mutableMapOf(UseCaseConstants.LOGOUT_TOKEN to token)
        logoutUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            if (it.success)
                _logoutLiveData.value = it
            else
                _logoutLiveData.value = it
        }, {
            Utility.cancelProgress()
            Log.e("failure", "$it")
            if (it is AncoHttpException.AncoUnauthorizedException)
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            else
                _errorLiveData.value = it?.getErrorMessage()
        }).collect()
    }

    fun callChangePassword(oldPassword: String, password: String) {
        val data = mutableMapOf(
            UseCaseConstants.OLD_PASSWORD to oldPassword,
            UseCaseConstants.PASSWORD to password
        )
        changePasswordUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _changePasswordLiveData.value = it
            else
                _changePasswordLiveData.value = it
        }, {
            Utility.cancelProgress()
            Log.e("failure", "$it")
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        }).collect()
    }

    fun callUpdateProfile(
        userType: Int,
        firstName: String,
        lastName: String,
        email: String,
        profileUrl: String?,
        businessName: String?,
        abn: String?,
        phoneNumber: String?
    ) {
        val data = mutableMapOf(
            UseCaseConstants.USER_TYPE to userType,
            UseCaseConstants.FIRST_NAME to firstName,
            UseCaseConstants.LAST_NAME to lastName,
            UseCaseConstants.EMAIL to email,
            UseCaseConstants.BUSINESS_NAME to businessName,
            UseCaseConstants.ABN to abn,
            UseCaseConstants.PHONE_NUMBER to phoneNumber,
            UseCaseConstants.PROFILE_URL to profileUrl
        )

        updateProfileUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _userInfoLiveData.value = it.data
            else
                _errorLiveData.value = it.message
        }, {
            Utility.cancelProgress()
            Log.e("failure", "$it")
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        }).collect()
    }

}

