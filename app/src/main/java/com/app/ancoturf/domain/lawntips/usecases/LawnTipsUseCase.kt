package com.app.ancoturf.domain.lawntips.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDataResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.lawntips.LawnTipsRepository
import io.reactivex.Single
import javax.inject.Inject

class LawnTipsUseCase @Inject constructor(private val lawntipsRepository: LawnTipsRepository) :
    BaseUseCase<BaseResponse<LawnTipsDataResponse>>() {
    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<LawnTipsDataResponse>> {
        return lawntipsRepository.getLawnTips(  page = data?.get(UseCaseConstants.PER_PAGE) as String)
    }
}