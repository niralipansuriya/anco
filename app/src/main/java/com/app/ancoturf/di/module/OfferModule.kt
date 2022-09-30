package com.app.ancoturf.di.module

import com.app.ancoturf.data.offer.OfferDataRepository
import com.app.ancoturf.domain.offer.OfferRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class OfferModule {

    @Singleton
    @Provides
    fun provideOfferRepository(offerDataRepository: OfferDataRepository): OfferRepository =
        offerDataRepository

}