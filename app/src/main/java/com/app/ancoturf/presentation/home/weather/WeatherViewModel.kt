package com.app.ancoturf.presentation.home.weather

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import com.google.gson.Gson
import java.io.IOException
import javax.inject.Inject

class WeatherViewModel  @Inject constructor(
    val app: Application
    , private val weatherUseCase: WeatherUseCase
) : BaseObservableViewModel(app) {
    private val _weatherLiveData = MutableLiveData<WeatherResponse>()
    val weatherLiveData: LiveData<WeatherResponse> get() = _weatherLiveData


    fun callWeatherAPI(latitude:String,longitude: String) {
        val data = mapOf(
            UseCaseConstants.latitude to latitude,
            UseCaseConstants.longitude to longitude
        )

        weatherUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            Log.e("success", "${it.getSuccess()}")
            if (it.getSuccess()!!) {

                _weatherLiveData.value = it
              /*  val jsonFileString = getJsonDataFromAsset(app!!, "WeatherResponseJson")
                val gson = Gson()
                val weatherRes: WeatherResponse = gson.fromJson(jsonFileString, WeatherResponse::class.java)
                _weatherLiveData.value = weatherRes */
            }
            else {
                _errorLiveData.value = it.getMessage()
            }
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _errorLiveData.value = it?.getErrorMessage()
        }).collect()
    }
    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

}