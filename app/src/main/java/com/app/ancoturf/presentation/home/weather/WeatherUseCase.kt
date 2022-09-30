package com.app.ancoturf.presentation.home.weather

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.account.AccountRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class WeatherUseCase @Inject constructor(private val weatherRepository: WeatherRepository) :  BaseUseCase<WeatherResponse>(){

        override fun buildSingle(data: Map<String, Any?>?): Single<WeatherResponse> {

            return weatherRepository.postWeather(
                latitude = data!![UseCaseConstants.latitude] as String,
                longitude = data[UseCaseConstants.longitude] as String
                )
        }

}