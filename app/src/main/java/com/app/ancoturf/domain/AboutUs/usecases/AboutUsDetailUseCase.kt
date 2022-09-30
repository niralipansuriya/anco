package com.app.ancoturf.domain.AboutUs.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDetailResponse
import com.app.ancoturf.domain.AboutUs.AboutUsRepository
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.lawntips.LawnTipsRepository
import io.reactivex.Single
import javax.inject.Inject

class AboutUsDetailUseCase @Inject constructor(private val aboutUsRepository: AboutUsRepository) :
    BaseUseCase<BaseResponse<LawnTipsDetailResponse>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<LawnTipsDetailResponse>> {
        return aboutUsRepository.getAboutUsDetail(id = data?.get(UseCaseConstants.LAWNTIPS_ID) as Int)
    }
}