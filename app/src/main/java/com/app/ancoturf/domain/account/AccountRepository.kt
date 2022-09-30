package com.app.ancoturf.domain.account

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import io.reactivex.Single

interface AccountRepository {

    fun postLogin(email: String, password: String,deviceToken:String): Single<BaseResponse<UserInfo>>

    fun postRegister(firstName: String, lastName: String, email: String, password: String, businessName: String, abn: String, phoneNumber: String,deviceToken: String): Single<BaseResponse<UserInfo>>

    fun postForgotPassword(email: String): Single<BaseResponse<Any>>

    fun geUserDetails(): Single<BaseResponse<UserInfo>>

    fun postChangePassword(oldPassword: String , password: String): Single<BaseResponse<Any>>

    fun updateRetailerProfile(firstName: String , lastName: String , email: String , profileUrl: String?): Single<BaseResponse<UserInfo>>

    fun updateProfile(firstName: String , lastName: String , email: String , profileUrl: String? , businessName: String , abn: String , phoneNumber: String): Single<BaseResponse<UserInfo>>

    fun logout(token:String):Single<BaseResponse<Any>>

}