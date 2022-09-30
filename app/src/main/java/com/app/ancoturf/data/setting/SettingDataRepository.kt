package com.app.ancoturf.data.setting

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.data.setting.remote.SettingService
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.domain.setting.SettingRepository
import io.reactivex.Single
import javax.inject.Inject

class SettingDataRepository @Inject constructor() : SettingRepository, CommonService<SettingService>() {

    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = SettingService::class.java

    override fun getSettings(): Single<BaseResponse<SettingsResponse.Data>> {
        return networkService.getSettings().map {
            it
        }
    }
}