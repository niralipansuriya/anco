package com.app.ancoturf.domain.AboutUs.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDataResponse
import com.app.ancoturf.domain.AboutUs.AboutUsRepository
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.lawntips.LawnTipsRepository
import io.reactivex.Single
import javax.inject.Inject

class AboutUsUseCases @Inject constructor(private val aboutUsRepository: AboutUsRepository) :
    BaseUseCase<BaseResponse<LawnTipsDataResponse>>() {
    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<LawnTipsDataResponse>> {
        return aboutUsRepository.getAboutUs(page = data?.get(UseCaseConstants.PER_PAGE) as String)
    }
}