package com.app.ancoturf.data.aboutUs.remote

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.aboutUs.FAQResponse
import com.app.ancoturf.data.aboutUs.FAQTagsResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDataResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDetailResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AboutUsService {

    @GET("api/aboutus")
    fun getAboutUs(@Query("page") page: String): Single<BaseResponse<LawnTipsDataResponse>>

    @GET("api/aboutus/{id}")
    fun getAboutUsDetail(@Path("id") id : String): Single<BaseResponse<LawnTipsDetailResponse>>

    @GET("api/faqs")
    fun getFAQs(@Query("page") page: String): Single<FAQResponse>

    @GET("api/faqTags")
    fun getFAQTags(@Query("page") page: String): Single<FAQTagsResponse>
}