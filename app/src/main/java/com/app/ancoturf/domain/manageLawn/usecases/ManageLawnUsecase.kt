package com.app.ancoturf.domain.manageLawn.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDataResponse
import com.app.ancoturf.data.manageLawn.remote.ManageLawnDataResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.manageLawn.ManageLawnRepository
import io.reactivex.Single
import javax.inject.Inject

class ManageLawnUsecase @Inject constructor(private val manageLwnRepository: ManageLawnRepository) :
    BaseUseCase<ManageLawnDataResponse>() {

//    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<ManageLawnDataResponse>> {
    override fun buildSingle(data: Map<String, Any?>?): Single<ManageLawnDataResponse> {
        return manageLwnRepository.getMyLawns(  page = data?.get(UseCaseConstants.PER_PAGE) as String)
    }
}