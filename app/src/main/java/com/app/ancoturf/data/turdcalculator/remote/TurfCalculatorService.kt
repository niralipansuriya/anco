package com.app.ancoturf.data.turdcalculator.remote

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.product.remote.entity.response.*
import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.*

interface TurfCalculatorService {

    @GET("api/turf_products")
    fun getTurfProducts(): Single<BaseResponse<ArrayList<ProductsResponse.Data>>>

}