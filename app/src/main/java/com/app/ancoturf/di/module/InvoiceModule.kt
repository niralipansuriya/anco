package com.app.ancoturf.di.module

import com.app.ancoturf.data.invoice.InvoiceDataRepository
import com.app.ancoturf.domain.invoice.InvoiceRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class InvoiceModule {
    @Singleton
    @Provides
    fun providerInvoiceRepository(invoiceDataRepository: InvoiceDataRepository): InvoiceRepository =
        invoiceDataRepository
}