package com.app.ancoturf.di.module

import com.app.ancoturf.data.manageLawn.ManageLawnDataRepository
import com.app.ancoturf.data.manageLawn.remote.ManageLawnDataResponse
import com.app.ancoturf.domain.manageLawn.ManageLawnRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ManageLawnModule {
    @Singleton
    @Provides
    fun provideManageLawnRepository(manageLawnDataRepository: ManageLawnDataRepository): ManageLawnRepository =
        manageLawnDataRepository
}