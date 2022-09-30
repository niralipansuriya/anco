package com.app.ancoturf.domain.AboutUs

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.aboutUs.FAQResponse
import com.app.ancoturf.data.aboutUs.FAQTagsResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDataResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDetailResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface AboutUsRepository {
    fun getAboutUs( page : String): Single<BaseResponse<LawnTipsDataResponse>>
    fun getAboutUsDetail( id : Int): Single<BaseResponse<LawnTipsDetailResponse>>
    fun getFAQs(page: String): Single<FAQResponse>
    fun getFAQTags(page: String): Single<FAQTagsResponse>


}