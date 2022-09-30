package com.app.ancoturf.di.module

import com.app.ancoturf.data.account.AccountDataRepository
import com.app.ancoturf.domain.account.AccountRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AccountModule {

    @Singleton
    @Provides
    fun provideAccountRepository(accountDataRepository: AccountDataRepository): AccountRepository =
        accountDataRepository

}