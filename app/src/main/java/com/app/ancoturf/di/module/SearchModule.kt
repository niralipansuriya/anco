package com.app.ancoturf.di.module

import com.app.ancoturf.data.search.SearchDataRepository
import com.app.ancoturf.domain.search.SearchRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SearchModule {

    @Singleton
    @Provides
    fun provideAccountRepository(searchDataRepository: SearchDataRepository): SearchRepository =
        searchDataRepository

}