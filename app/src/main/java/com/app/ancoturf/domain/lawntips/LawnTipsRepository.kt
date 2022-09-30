package com.app.ancoturf.domain.lawntips

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDataResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDetailResponse
import io.reactivex.Single

interface LawnTipsRepository {

    fun getLawnTips( page : String): Single<BaseResponse<LawnTipsDataResponse>>

    fun getLawnTipsDetails( id : Int): Single<BaseResponse<LawnTipsDetailResponse>>

}