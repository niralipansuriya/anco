package com.app.ancoturf.presentation.home.weather

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class WeatherModule {
    @Singleton
    @Provides
    fun provideWeatherRepository(weatherDataRepository: WeatherDataRepository): WeatherRepository =
        weatherDataRepository

}