package com.app.ancoturf.di.module

import com.app.ancoturf.data.setting.SettingDataRepository
import com.app.ancoturf.domain.setting.SettingRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SettingModule {
    @Singleton
    @Provides
    fun provideSettingRepository(settingDataRepository: SettingDataRepository): SettingRepository =
        settingDataRepository
}