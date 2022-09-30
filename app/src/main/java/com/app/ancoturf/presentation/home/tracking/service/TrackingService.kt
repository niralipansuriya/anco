package com.app.ancoturf.presentation.home.tracking.service

import RouteDataModel
import com.app.ancoturf.data.tracking.MapDirectionRequest
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TrackingService {

    @Headers("Content-Type: text/xml")
    @POST("service.asmx")
    fun callInitXmlRequest(@Body data: String): Call<String>

    @POST("api/directions_map")
    fun mapDirectionApi(@Body data: MapDirectionRequest): Call<Object>


}