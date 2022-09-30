package com.app.ancoturf.di.module

import com.app.ancoturf.data.afterpay.AfterpayDataRepository
import com.app.ancoturf.data.setting.SettingDataRepository
import com.app.ancoturf.domain.afterpay.AfterpayRepository
import com.app.ancoturf.domain.setting.SettingRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AfterpayModule {

    @Singleton
    @Provides
    fun provideSettingRepository(afterpayDataRepository: AfterpayDataRepository): AfterpayRepository =
        afterpayDataRepository

}