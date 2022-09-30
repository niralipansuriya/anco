package com.app.ancoturf.presentation.home.shop

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.data.common.exceptions.AncoHttpException
import com.app.ancoturf.data.product.remote.entity.response.ProductCategoryData
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.product.usecases.ProductCategoryUseCase
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import javax.inject.Inject

class ShopViewModel @Inject constructor(
    val app: Application
    , val productCategoryUseCase: ProductCategoryUseCase
) : BaseObservableViewModel(app) {

    val _productCategoryLiveData = MutableLiveData<ArrayList<ProductCategoryData>>()
    val productCategoryLiveData: LiveData<ArrayList<ProductCategoryData>> get() = _productCategoryLiveData

    fun callGetProductCategories() {
        productCategoryUseCase.execute().customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _productCategoryLiveData.value = it.data
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