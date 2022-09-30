package com.app.ancoturf.di.module

import com.app.ancoturf.data.lawntips.LawnTipsDataRepository
import com.app.ancoturf.domain.lawntips.LawnTipsRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LawnTipsModule {

    @Singleton
    @Provides
    fun provideLawnTipsRepository(lawntipsDataRepository: LawnTipsDataRepository): LawnTipsRepository =
        lawntipsDataRepository

}