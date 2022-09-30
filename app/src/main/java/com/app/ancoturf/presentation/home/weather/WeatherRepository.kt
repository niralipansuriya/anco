package com.app.ancoturf.presentation.home.weather

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import io.reactivex.Single

interface WeatherRepository {
    fun postWeather(latitude: String, longitude: String): Single<WeatherResponse>

}