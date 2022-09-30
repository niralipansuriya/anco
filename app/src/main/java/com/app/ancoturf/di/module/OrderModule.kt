package com.app.ancoturf.di.module

import com.app.ancoturf.data.order.OrderDataRepository
import com.app.ancoturf.domain.order.OrderRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class OrderModule {

    @Singleton
    @Provides
    fun provideOrderRepository(orderDataRepository: OrderDataRepository): OrderRepository =
        orderDataRepository

}