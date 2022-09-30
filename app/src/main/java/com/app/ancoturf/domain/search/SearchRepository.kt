package com.app.ancoturf.domain.search

import android.util.EventLogTags
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.search.remote.entity.response.RequestProductResponse
import com.app.ancoturf.data.search.remote.entity.response.SearchProductResponse
import io.reactivex.Single

interface SearchRepository {
    fun getSearchedProduct(lastSearchKey : String): Single<SearchProductResponse>

    fun requestProduct(name :String , email : String , productDescription : String , productUrl : String): Single<BaseResponse<RequestProductResponse>>
}