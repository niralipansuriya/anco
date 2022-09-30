package com.app.ancoturf.presentation.home.weather

import com.google.gson.annotations.SerializedName

class WeatherRequest(
    @SerializedName("latitude")
    var latitude: String = "",
    @SerializedName("longitude")
    var longitude: String = ""
)