package com.app.ancoturf.domain.setting.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.setting.SettingRepository
import io.reactivex.Single
import javax.inject.Inject

class SettingUseCase @Inject constructor(private val settingRepository: SettingRepository) :  BaseUseCase<BaseResponse<SettingsResponse.Data>>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<SettingsResponse.Data>> {
        return settingRepository.getSettings()
    }


}