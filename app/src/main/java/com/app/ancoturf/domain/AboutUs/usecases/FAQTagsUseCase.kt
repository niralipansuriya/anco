package com.app.ancoturf.domain.AboutUs.usecases

import com.app.ancoturf.data.aboutUs.FAQResponse
import com.app.ancoturf.data.aboutUs.FAQTagsResponse
import com.app.ancoturf.domain.AboutUs.AboutUsRepository
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class FAQTagsUseCase @Inject constructor(private val aboutUsRepository: AboutUsRepository) :
    BaseUseCase<FAQTagsResponse>() {
    override fun buildSingle(data: Map<String, Any?>?): Single<FAQTagsResponse> {
        return aboutUsRepository.getFAQTags(page = data?.get(UseCaseConstants.PER_PAGE) as String)
    }
}