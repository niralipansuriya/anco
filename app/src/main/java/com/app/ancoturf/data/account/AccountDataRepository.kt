package com.app.ancoturf.data.account

import android.util.Log
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.AccountService
import com.app.ancoturf.data.account.remote.entity.request.LoginRequest
import com.app.ancoturf.data.account.remote.entity.request.RegisterRequest
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.common.LogStatus
import com.app.ancoturf.data.common.RequestConstants.REQUEST_LOGIN
import com.app.ancoturf.data.common.RequestConstants.REQUEST_REGISTER
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.domain.account.AccountRepository
import com.app.ancoturf.extension.printLog
import com.app.ancoturf.utils.Utility
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

class AccountDataRepository @Inject constructor() : AccountRepository,
    CommonService<AccountService>() {

    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = AccountService::class.java

    override fun postLogin(
        email: String,
        password: String,
        deviceToken: String
    ): Single<BaseResponse<UserInfo>> {
        val loginRequest =
            LoginRequest(email = email, password = password, deviceToken = deviceToken)
        loginRequest.toString().printLog(LogStatus.REQUEST, REQUEST_LOGIN)
        return networkService.login(loginRequest).map {
            if (it.success) {
                it.data?.let {
                    sharedPrefs.isLogged = true
                    sharedPrefs.accessToken = "Bearer " + it.token

                    Log.d("trace", "======================  ${sharedPrefs.accessToken}")
                    sharedPrefs.userType = it.idCmsPrivileges
                    sharedPrefs.userId = it.id
                    sharedPrefs.userEmail = it.email
                    it.credit?.let {
                        sharedPrefs.availableCredit = it.available_credit.toInt()
                    }
                    sharedPrefs.userName = it.firstName + " " + it.lastName
                    sharedPrefs.userInfo =
                        Gson().toJson(it, UserInfo::class.java)
                    sharedPrefs.quote_max_price = it.quote_max_price
                    sharedPrefs.order_max_price = it.order_max_price
                }
            }
            it
        }
    }

    override fun postRegister(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        businessName: String,
        abn: String,
        phoneNumber: String,
        deviceToken: String
    ): Single<BaseResponse<UserInfo>> {
        val registerRequest = RegisterRequest(
            firstName = firstName,
            lastName = lastName,
            email = email,
            password = password,
            businessName = businessName,
            abn = abn,
            phoneNumber = phoneNumber,
            deviceToken = deviceToken
        )
        registerRequest.toString().printLog(LogStatus.REQUEST, REQUEST_REGISTER)
        val jsonObject: JsonObject = createJsonObject(registerRequest)
        return networkService.register(jsonObject).map {
            it
        }
    }

    private fun createJsonObject(registerRequest: RegisterRequest): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("first_name", registerRequest.firstName)
        jsonObject.addProperty("last_name", registerRequest.lastName)
        jsonObject.addProperty("email", registerRequest.email)
        jsonObject.addProperty("password", registerRequest.password)
        jsonObject.addProperty("device_token", registerRequest.deviceToken)
        if (!Utility.isValueNull(registerRequest.abn) && !Utility.isValueNull(registerRequest.businessName) && !Utility.isValueNull(
                registerRequest.phoneNumber
            )
        ) {
            jsonObject.addProperty("abn", registerRequest.abn)
            jsonObject.addProperty("business_name", registerRequest.businessName)
            jsonObject.addProperty("phone_number", registerRequest.phoneNumber)
        }
        return jsonObject
    }

    override fun postForgotPassword(email: String): Single<BaseResponse<Any>> {
        return networkService.forgotPassword(email).map {
            it
        }
    }

    override fun geUserDetails(): Single<BaseResponse<UserInfo>> {
        return networkService.getUserDetails().map {
            it
        }
    }

    override fun postChangePassword(
        oldPassword: String,
        password: String
    ): Single<BaseResponse<Any>> {
        return networkService.changePassword(oldPassword, password).map {
            it
        }
    }

    override fun updateRetailerProfile(
        firstName: String,
        lastName: String,
        email: String,
        profileUrl: String?
    ): Single<BaseResponse<UserInfo>> {

        val firstNameRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), firstName)

        val lastNameRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), lastName)

        val emailRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), email)

        var profileUrlRequestBody: MultipartBody.Part? = null
        if (!Utility.isValueNull(profileUrl)) {
            val imageFile = File(profileUrl)
            val requestImageFile =
                RequestBody.create(MediaType.parse("image/*"), imageFile)
            profileUrlRequestBody =
                MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.getName(),
                    requestImageFile
                )
        }
        return networkService.updateRetailersProfile(
            firstNameRequestBody,
            lastNameRequestBody,
            emailRequestBody,
            profileUrlRequestBody
        ).map {
            it
        }
    }

    override fun updateProfile(
        firstName: String,
        lastName: String,
        email: String,
        profileUrl: String?,
        businessName: String,
        abn: String,
        phoneNumber: String
    ): Single<BaseResponse<UserInfo>> {


        val firstNameRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), firstName)

        val lastNameRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), lastName)

        val emailRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), email)

        var profileUrlRequestBody: MultipartBody.Part? = null
        if (!Utility.isValueNull(profileUrl)) {
            val imageFile = File(profileUrl)
            val requestImageFile =
                RequestBody.create(MediaType.parse("image/*"), imageFile)
            profileUrlRequestBody =
                MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.getName(),
                    requestImageFile
                )
        }

        val businessNameRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), businessName)

        val abnRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), abn)

        val phoneNumberRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), phoneNumber)

        return networkService.updateProfile(
            firstNameRequestBody,
            lastNameRequestBody,
            emailRequestBody,
            profileUrlRequestBody,
            businessNameRequestBody,
            abnRequestBody,
            phoneNumberRequestBody
        ).map {
            it
        }
    }

    override fun logout(token: String): Single<BaseResponse<Any>> {
        var json = JsonObject()
        json.addProperty("token",token)
        return networkService.logout(json).map {
            it
        }
    }
}