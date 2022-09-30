package com.app.ancoturf.di.module

import com.app.ancoturf.data.account.AccountDataRepository
import com.app.ancoturf.data.portfolio.PortfolioDataRepository
import com.app.ancoturf.data.product.ProductDataRepository
import com.app.ancoturf.domain.account.AccountRepository
import com.app.ancoturf.domain.portfolio.PortfolioRepository
import com.app.ancoturf.domain.product.ProductRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PortfolioModule {

    @Singleton
    @Provides
    fun providePortfolioRepository(portfolioDataRepository: PortfolioDataRepository): PortfolioRepository =
        portfolioDataRepository

}