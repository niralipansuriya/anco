package com.app.ancoturf.presentation.home.turfcalculator

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.data.common.exceptions.AncoHttpException
import com.app.ancoturf.data.product.remote.entity.response.ProductCategoryData
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.product.usecases.ProductCategoryUseCase
import com.app.ancoturf.domain.turfcalculator.usecases.TurfProductsUseCase
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import javax.inject.Inject

class TurfCalculatorViewModel @Inject constructor(
    val app: Application
    , val turfProductsUseCase: TurfProductsUseCase
) : BaseObservableViewModel(app) {

    val _turfProductsLiveData = MutableLiveData<ArrayList<ProductsResponse.Data>>()
    val turfProductsLiveData: LiveData<ArrayList<ProductsResponse.Data>> get() = _turfProductsLiveData

    fun callGetTurfProducts() {
        turfProductsUseCase.execute().customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _turfProductsLiveData.value = it.data
            else
                _errorLiveData.value = it.message
        }, {
            Utility.cancelProgress()
            Log.e("failure", "$it")
            if(it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        }).collect()
    }
}