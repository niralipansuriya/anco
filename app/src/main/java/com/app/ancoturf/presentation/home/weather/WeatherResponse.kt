package com.app.ancoturf.presentation.home.weather

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WeatherResponse() : Parcelable {
    @SerializedName("success")
    @Expose
    private var success: Boolean? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    @SerializedName("data")
    @Expose
    private var data: Data? = null

    constructor(parcel: Parcel) : this() {
        success = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        message = parcel.readString()
    }

    fun getSuccess(): Boolean? {
        return success
    }

    fun setSuccess(success: Boolean?) {
        this.success = success
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getData(): Data? {
        return data
    }

    fun setData(data: Data?) {
        this.data = data
    }

    class Data {
        @SerializedName("current")
        @Expose
        var current: Current? = null

        @SerializedName("next_seven_day")
        @Expose
        var nextSevenDay: List<NextSevenDay>? = null

        @SerializedName("timewise")
        @Expose
        var timewise: List<Timewise>? = null

    }

    class Current {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("stn_name")
        @Expose
        var stnName: String? = null

        @SerializedName("datetime")
        @Expose
        var datetime: String? = null

        @SerializedName("latitude")
        @Expose
        var latitude: String? = null

        @SerializedName("longitude")
        @Expose
        var longitude: String? = null

        @SerializedName("cloud")
        @Expose
        var cloud: Any? = null

        @SerializedName("cloud_oktas")
        @Expose
        var cloudOktas: Any? = null

        @SerializedName("cloud_type_id")
        @Expose
        var cloudTypeId: Any? = null

        @SerializedName("cloud_type")
        @Expose
        var cloudType: Any? = null

        @SerializedName("air_temperature")
        @Expose
        var airTemperature: String? = null

        @SerializedName("maximum_air_temperature")
        @Expose
        var maximumAirTemperature: String? = null

        @SerializedName("minimum_air_temperature")
        @Expose
        var minimumAirTemperature: String? = null

        @SerializedName("maximum_gust_spd")
        @Expose
        var maximumGustSpd: String? = null

        @SerializedName("maximum_gust_kmh")
        @Expose
        var maximumGustKmh: String? = null

        @SerializedName("maximum_gust_dir")
        @Expose
        var maximumGustDir: String? = null

        @SerializedName("rainfall_24hr")
        @Expose
        var rainfall24hr: String? = null

        @SerializedName("created_date")
        @Expose
        var createdDate: String? = null

        @SerializedName("distance")
        @Expose
        var distance: String? = null

        @SerializedName("current_date")
        @Expose
        var currentDate: String? = null

    }

    class NextSevenDay {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("area_type")
        @Expose
        var areaType: String? = null

        @SerializedName("date")
        @Expose
        var date: String? = null

        @SerializedName("precis")
        @Expose
        var precis: String? = null

        @SerializedName("probability_of_precipitation")
        @Expose
        var probabilityOfPrecipitation: String? = null

        @SerializedName("forecast_icon_code")
        @Expose
        var forecastIconCode: String? = null

        @SerializedName("precipitation_range")
        @Expose
        var precipitationRange: String? = null

        @SerializedName("air_temperature_minimum")
        @Expose
        var airTemperatureMinimum: String? = null

        @SerializedName("air_temperature_maximum")
        @Expose
        var airTemperatureMaximum: String? = null

        @SerializedName("created_date")
        @Expose
        var createdDate: String? = null

    }

    class Timewise {
        @SerializedName("id")
        @Expose
        var id: String? = null

        @SerializedName("location_id")
        @Expose
        var locationId: String? = null

        @SerializedName("location_name")
        @Expose
        var locationName: String? = null

        @SerializedName("date")
        @Expose
        var date: String? = null

        @SerializedName("time")
        @Expose
        var time: String? = null

        @SerializedName("temperature")
        @Expose
        var temperature: String? = null

        @SerializedName("precisCode")
        @Expose
        var precisCode: String? = null

        @SerializedName("precis")
        @Expose
        var precis: String? = null

        @SerializedName("precisOverlayCode")
        @Expose
        var precisOverlayCode: String? = null

        @SerializedName("night")
        @Expose
        var night: String? = null

        @SerializedName("latitude")
        @Expose
        var latitude: String? = null

        @SerializedName("longitude")
        @Expose
        var longitude: String? = null

        @SerializedName("created_date")
        @Expose
        var createdDate: String? = null

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(success)
        parcel.writeString(message)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WeatherResponse> {
        override fun createFromParcel(parcel: Parcel): WeatherResponse {
            return WeatherResponse(parcel)
        }

        override fun newArray(size: Int): Array<WeatherResponse?> {
            return arrayOfNulls(size)
        }
    }
}