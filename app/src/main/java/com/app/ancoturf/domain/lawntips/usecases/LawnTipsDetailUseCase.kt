package com.app.ancoturf.domain.lawntips.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDataResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDetailResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.lawntips.LawnTipsRepository
import io.reactivex.Single
import javax.inject.Inject

class LawnTipsDetailUseCase @Inject constructor(private val lawntipsRepository: LawnTipsRepository) :
    BaseUseCase<BaseResponse<LawnTipsDetailResponse>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<LawnTipsDetailResponse>> {
        return lawntipsRepository.getLawnTipsDetails(  id = data?.get(UseCaseConstants.LAWNTIPS_ID) as Int)
    }
}