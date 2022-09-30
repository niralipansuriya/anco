package com.app.ancoturf.domain.AboutUs.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.aboutUs.FAQResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDataResponse
import com.app.ancoturf.domain.AboutUs.AboutUsRepository
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class FAQUseCase @Inject constructor(private val aboutUsRepository: AboutUsRepository) :
    BaseUseCase<FAQResponse>() {
    override fun buildSingle(data: Map<String, Any?>?): Single<FAQResponse> {
        return aboutUsRepository.getFAQs(page = data?.get(UseCaseConstants.PER_PAGE) as String)
    }
}