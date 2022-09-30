package com.app.ancoturf.di.module

import com.app.ancoturf.data.aboutUs.AboutUsDataRepository
import com.app.ancoturf.data.lawntips.LawnTipsDataRepository
import com.app.ancoturf.domain.AboutUs.AboutUsRepository
import com.app.ancoturf.domain.lawntips.LawnTipsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AboutUsModule {
    @Singleton
    @Provides
    fun provideLawnTipsRepository(aboutUsDataRepository: AboutUsDataRepository): AboutUsRepository =
        aboutUsDataRepository
}