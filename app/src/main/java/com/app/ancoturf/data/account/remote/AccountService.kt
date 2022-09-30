package com.app.ancoturf.data.account.remote

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.request.LoginRequest
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.google.gson.JsonObject
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.http.*

interface AccountService {

    @POST("api/authenticate")
    fun login(@Body loginRequest: LoginRequest): Single<BaseResponse<UserInfo>>

    @POST("api/register")
    fun register(@Body registerRequest: JsonObject): Single<BaseResponse<UserInfo>>

    @FormUrlEncoded
    @POST("api/forgot_password")
    fun forgotPassword(@Field("email") email: String): Single<BaseResponse<Any>>

    @GET("api/user_details")
    fun getUserDetails(): Single<BaseResponse<UserInfo>>

    @FormUrlEncoded
    @POST("api/change_password")
    fun changePassword(@Field("old_password") oldPassword: String, @Field("password") password: String): Single<BaseResponse<Any>>

    @Multipart
    @POST("api/user/update")
    fun updateRetailersProfile(
        @Part("first_name") firstName: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("email") email: RequestBody,
        @Part profileUrl: MultipartBody.Part?
    ): Single<BaseResponse<UserInfo>>

    @Multipart
    @POST("api/user/update")
    fun updateProfile(
        @Part("first_name") firstName: RequestBody,
        @Part("last_name") lastName: RequestBody,
        @Part("email") email: RequestBody,
        @Part profileUrl: MultipartBody.Part?,
        @Part("business_name") businessName: RequestBody,
        @Part("abn") abn: RequestBody,
        @Part("phone_number") phoneNumber: RequestBody
    ): Single<BaseResponse<UserInfo>>

    @POST("api/logout")
    fun logout(@Body jsonReq: JsonObject): Single<BaseResponse<Any>>

}