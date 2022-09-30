package com.app.ancoturf.data.manageLawn

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.data.lawntips.remote.LawnTipsDataResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDetailResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsService
import com.app.ancoturf.data.manageLawn.remote.ManageLawnDataResponse
import com.app.ancoturf.data.manageLawn.remote.ManageLawnDetailResponse
import com.app.ancoturf.data.manageLawn.remote.ManageLawnService
import com.app.ancoturf.domain.lawntips.LawnTipsRepository
import com.app.ancoturf.domain.manageLawn.ManageLawnRepository
import io.reactivex.Single
import javax.inject.Inject

class ManageLawnDataRepository  @Inject constructor() : ManageLawnRepository, CommonService<ManageLawnService>() {

    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = ManageLawnService::class.java


//    override fun getMyLawns(page: String): Single<BaseResponse<ManageLawnDataResponse>> {
    override fun getMyLawns(page: String): Single<ManageLawnDataResponse> {
        return networkService.getMyLawns(page).map {
            it
        }
    }

    override fun getMyLawnById(id: Int): Single<ManageLawnDetailResponse> {
        return networkService.getMyLawnById(id.toString()).map { it }
    }
}