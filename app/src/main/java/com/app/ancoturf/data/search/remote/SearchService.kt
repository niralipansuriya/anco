package com.app.ancoturf.data.search.remote

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.search.remote.entity.response.RequestProductResponse
import com.app.ancoturf.data.search.remote.entity.response.SearchProductResponse
import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SearchService {

    @GET("api/customer_search")
    fun getSearchedProduct(@Query("last_search_key") lastSearchKey : String): Single<SearchProductResponse>

    @POST("api/request_products")
    fun requestProduct(@Body requestProductRequest: JsonObject): Single<BaseResponse<RequestProductResponse>>
}