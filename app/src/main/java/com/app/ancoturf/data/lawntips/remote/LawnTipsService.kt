package com.app.ancoturf.data.lawntips.remote

import com.app.ancoturf.data.BaseResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LawnTipsService {

    @GET("api/lawntips")
    fun getLawnTips(@Query("page") page: String): Single<BaseResponse<LawnTipsDataResponse>>

    @GET("api/lawntip/{id}")
    fun getLawnTipsDetail(@Path("id") id : String): Single<BaseResponse<LawnTipsDetailResponse>>
}