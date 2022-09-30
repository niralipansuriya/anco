package com.app.ancoturf.data.manageLawn.remote

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ManageLawnService {

    @GET("api/mylawns")
//    fun getMyLawns(@Query("page") page: String): Single<BaseResponse<ManageLawnDataResponse>>
    fun getMyLawns(@Query("page") page: String): Single<ManageLawnDataResponse>

    @GET("api/mylawn/{id}")
    fun getMyLawnById(@Path("id") id: String): Single<ManageLawnDetailResponse>


}