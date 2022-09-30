package com.app.ancoturf.di.module

import com.app.ancoturf.data.deliverydate.DeliveryDateValidationDataRepository
import com.app.ancoturf.domain.deliverydate.DeliveryDateValidationRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DateValidationModule {

    @Singleton
    @Provides
    fun provideOrderRepository(deliveryDateValidationDataRepository: DeliveryDateValidationDataRepository): DeliveryDateValidationRepository =
        deliveryDateValidationDataRepository

}