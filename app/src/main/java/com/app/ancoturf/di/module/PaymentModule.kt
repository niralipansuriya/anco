package com.app.ancoturf.di.module

import com.app.ancoturf.data.payment.PaymentDataRepository
import com.app.ancoturf.domain.payment.PaymentRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PaymentModule {

    @Singleton
    @Provides
    fun providePaymentRepository(paymentDataRepository: PaymentDataRepository): PaymentRepository =
        paymentDataRepository

}