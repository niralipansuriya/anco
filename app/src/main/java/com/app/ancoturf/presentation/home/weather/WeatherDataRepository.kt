package com.app.ancoturf.presentation.home.weather

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.data.product.remote.ProductService
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.domain.product.ProductRepository
import io.reactivex.Single
import javax.inject.Inject

class WeatherDataRepository @Inject constructor() : WeatherRepository, CommonService<WeatherService>() {
    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = WeatherService::class.java



    override fun postWeather(latitude: String, longitude: String): Single<WeatherResponse> {
        return networkService.weather(latitude , longitude).map {
            it
        }
    }

}