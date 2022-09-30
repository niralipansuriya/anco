package com.app.ancoturf.data.aboutUs

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.aboutUs.remote.AboutUsService
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.data.lawntips.remote.LawnTipsDataResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDetailResponse
import com.app.ancoturf.domain.AboutUs.AboutUsRepository
import io.reactivex.Single
import javax.inject.Inject

class AboutUsDataRepository @Inject constructor() : AboutUsRepository,
    CommonService<AboutUsService>() {

    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = AboutUsService::class.java

    override fun getAboutUs(page: String): Single<BaseResponse<LawnTipsDataResponse>> {
        return networkService.getAboutUs(page).map {
            it
        }
    }

    override fun getAboutUsDetail(id: Int): Single<BaseResponse<LawnTipsDetailResponse>> {
        return networkService.getAboutUsDetail(id.toString()).map { it }
    }

    override fun getFAQs(page: String): Single<FAQResponse> {
        return networkService.getFAQs(page).map {
            it
        }
    }

    override fun getFAQTags(page: String): Single<FAQTagsResponse> {
        return networkService.getFAQTags(page).map {
            it
        }
    }
}