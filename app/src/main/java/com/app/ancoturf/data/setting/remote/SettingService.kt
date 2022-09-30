package com.app.ancoturf.data.setting.remote

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import io.reactivex.Single
import retrofit2.http.*

interface SettingService {

    @GET("api/settings")
    fun getSettings(): Single<BaseResponse<SettingsResponse.Data>>

}