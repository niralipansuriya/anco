package com.app.ancoturf.presentation.home.weather

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.request.LoginRequest
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface WeatherService {

    @FormUrlEncoded
    @POST("api/daily_weather_detail")
    fun weather(@Field("latitude") latitude: String, @Field("longitude") longitude: String): Single<WeatherResponse>


}