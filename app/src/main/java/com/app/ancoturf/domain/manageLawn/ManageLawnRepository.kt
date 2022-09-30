package com.app.ancoturf.domain.manageLawn

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.manageLawn.ManageLawnDataRepository
import com.app.ancoturf.data.manageLawn.remote.ManageLawnDataResponse
import com.app.ancoturf.data.manageLawn.remote.ManageLawnDetailResponse
import io.reactivex.Single

interface ManageLawnRepository {

//    fun getMyLawns(page: String): Single<BaseResponse<ManageLawnDataResponse>>
    fun getMyLawns(page: String): Single<ManageLawnDataResponse>

    fun getMyLawnById(id: Int): Single<ManageLawnDetailResponse>
}