package com.app.ancoturf.data.lawntips

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.data.lawntips.remote.LawnTipsDataResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDetailResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsService
import com.app.ancoturf.domain.lawntips.LawnTipsRepository
import io.reactivex.Single
import javax.inject.Inject

class LawnTipsDataRepository @Inject constructor() : LawnTipsRepository, CommonService<LawnTipsService>() {

    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = LawnTipsService::class.java

    override fun getLawnTips( page : String): Single<BaseResponse<LawnTipsDataResponse>> {
        return networkService.getLawnTips(page).map {
            it
        }
    }

    override fun getLawnTipsDetails(id: Int): Single<BaseResponse<LawnTipsDetailResponse>> {
        return networkService.getLawnTipsDetail(id.toString()).map { it }
    }
}