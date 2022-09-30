package com.app.ancoturf.domain.search.usecases

import com.app.ancoturf.data.search.remote.entity.response.SearchProductResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.search.SearchRepository
import io.reactivex.Single
import javax.inject.Inject

class SearchUseCase @Inject constructor(private val searchRepository: SearchRepository) :  BaseUseCase<SearchProductResponse>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<SearchProductResponse> {

        return searchRepository.getSearchedProduct(lastSearchKey = data?.get(UseCaseConstants.LAST_SEARCH_KEY) as  String)
    }


}