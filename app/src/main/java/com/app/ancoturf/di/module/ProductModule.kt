package com.app.ancoturf.di.module

import com.app.ancoturf.data.product.ProductDataRepository
import com.app.ancoturf.domain.product.ProductRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ProductModule {

    @Singleton
    @Provides
    fun provideProductRepository(productDataRepository: ProductDataRepository): ProductRepository =
        productDataRepository

}