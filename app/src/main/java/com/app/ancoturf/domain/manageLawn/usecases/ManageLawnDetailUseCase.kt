package com.app.ancoturf.domain.manageLawn.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDetailResponse
import com.app.ancoturf.data.manageLawn.remote.ManageLawnDetailResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.manageLawn.ManageLawnRepository
import io.reactivex.Single
import javax.inject.Inject

class ManageLawnDetailUseCase @Inject constructor(private val manageLawnRepository: ManageLawnRepository) :
    BaseUseCase<ManageLawnDetailResponse>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<ManageLawnDetailResponse> {
        return manageLawnRepository.getMyLawnById(  id = data?.get(UseCaseConstants.MANAGE_LAWN_ID) as Int)
    }
}
