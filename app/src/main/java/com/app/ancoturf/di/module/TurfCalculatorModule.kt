package com.app.ancoturf.di.module

import com.app.ancoturf.data.account.AccountDataRepository
import com.app.ancoturf.data.turdcalculator.TurfCalculatorDataRepository
import com.app.ancoturf.domain.account.AccountRepository
import com.app.ancoturf.domain.turfcalculator.TurfCalculatorRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class TurfCalculatorModule {

    @Singleton
    @Provides
    fun provideAccountRepository(turfCalculatorDataRepository: TurfCalculatorDataRepository): TurfCalculatorRepository =
        turfCalculatorDataRepository

}