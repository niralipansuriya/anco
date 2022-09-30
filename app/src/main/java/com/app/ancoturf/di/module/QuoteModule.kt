package com.app.ancoturf.di.module

import com.app.ancoturf.data.account.AccountDataRepository
import com.app.ancoturf.data.quote.QuoteDataRepository
import com.app.ancoturf.domain.account.AccountRepository
import com.app.ancoturf.domain.quote.QuoteRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class QuoteModule {

    @Singleton
    @Provides
    fun provideQuoteRepository(quoteDataRepository: QuoteDataRepository): QuoteRepository =
        quoteDataRepository

}