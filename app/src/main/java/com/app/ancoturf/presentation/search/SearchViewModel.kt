package com.app.ancoturf.presentation.search
import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.exceptions.AncoHttpException
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.search.remote.entity.response.RequestProductResponse
import com.app.ancoturf.data.search.remote.entity.response.SearchProductResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.search.usecases.AddRequestProductsUseCase
import com.app.ancoturf.domain.search.usecases.SearchUseCase
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    val app: Application
    , val searchUseCase: SearchUseCase , val addRequestProductUseCase : AddRequestProductsUseCase
) : BaseObservableViewModel(app) {

    val _searchLiveData = MutableLiveData<SearchProductResponse>()
    val searchLiveData: LiveData<SearchProductResponse> get() = _searchLiveData

    val _requestProductLiveData = MutableLiveData<BaseResponse<RequestProductResponse>>()
    val requestProductLiveData: LiveData<BaseResponse<RequestProductResponse>> get() = _requestProductLiveData

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    //Only for guest user
    var searchKey = ""
    var isSearch = false


    fun callGetLastSearchRelatedProduct(lastSearchKey: String) {
        val data = mutableMapOf(
            UseCaseConstants.LAST_SEARCH_KEY to lastSearchKey
        )

        searchUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            if (it.success && it != null)
                _searchLiveData.value = it
        }, {
            Utility.cancelProgress()
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        }).collect()
    }

    fun callAddRequestProduct(name : String,email : String,productDescription : String,productUrl : String)
    {
        val data = mapOf(

            UseCaseConstants.NAME to name,
            UseCaseConstants.EMAIL to email,
            UseCaseConstants.DESCRIPTIONS to productDescription,
            UseCaseConstants.PRODUCT_URL to productUrl
        )

        addRequestProductUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            if (it.success && it != null)
                _requestProductLiveData.value = it
        }, {
            Utility.cancelProgress()
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        }).collect()
    }
}
