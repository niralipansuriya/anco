package com.app.ancoturf.domain.setting

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import io.reactivex.Single

interface SettingRepository {

    fun getSettings(): Single<BaseResponse<SettingsResponse.Data>>

}