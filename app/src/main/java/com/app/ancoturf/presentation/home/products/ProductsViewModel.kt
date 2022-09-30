package com.app.ancoturf.presentation.home.products

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.R
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.exceptions.AncoHttpException
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.product.remote.entity.response.AddReviewResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.data.product.remote.entity.response.RelatedProductsResponse
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.product.usecases.*
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import javax.inject.Inject

class ProductsViewModel @Inject constructor(
    val app: Application
    , val productsUseCase: ProductsUseCase
    , val productDetailUseCase: ProductDetailUseCase
    , val relatedProductsUseCase: RelatedProductsUseCase
    , val addReviewUseCase: AddReviewUseCase
    , val productFullNameUseCase: ProductFullNameUseCase
) : BaseObservableViewModel(app) {

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    val _productsLiveData = MutableLiveData<BaseResponse<ProductsResponse>>()
    val productsLiveData: LiveData<BaseResponse<ProductsResponse>> get() = _productsLiveData


    val _productDetailsLiveData = MutableLiveData<ProductDetailResponse>()
    val productDetailsLiveData: LiveData<ProductDetailResponse> get() = _productDetailsLiveData


    val _productFullNameLiveData = MutableLiveData<ArrayList<ProductDetailResponse>>()
    val productFullNameLiveData: LiveData<ArrayList<ProductDetailResponse>> get() = _productFullNameLiveData

    val _relatedProductsLiveData = MutableLiveData<BaseResponse<RelatedProductsResponse>>()
    val relatedProductsLiveData: LiveData<BaseResponse<RelatedProductsResponse>> get() = _relatedProductsLiveData

    val _addReviewLiveData = MutableLiveData<BaseResponse<AddReviewResponse>>()
    val addReviewLiveData: LiveData<BaseResponse<AddReviewResponse>> get() = _addReviewLiveData

    val _tagsLiveData = MutableLiveData<ArrayList<SettingsResponse.Data.Tag>>()

    var filterAvailable = false
    var title: String? = app.getString(R.string.shop_all)
    var numberOfFilters: Int? = 0


    val data = mutableMapOf(
        UseCaseConstants.PRODUCT_CATEGORY_IDS to "",
        UseCaseConstants.PRODUCT_TAG_IDS to "",
        UseCaseConstants.PRICE_MIN to "",
        UseCaseConstants.PRICE_MAX to "",
        UseCaseConstants.SEARCH to "",
        UseCaseConstants.SORT_BY to "",
        UseCaseConstants.PER_PAGE to ""
    )

    fun callGetProducts(page: String) {

        data?.put(UseCaseConstants.PER_PAGE, page)

        productsUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            if (it.success) {
                _productsLiveData.value = it
                _isNextPageUrl.value = it.data?.nextPageUrl != null
            } else
                _errorLiveData.value = it.message
        }, {
            Utility.cancelProgress()
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()

            }
        })
            .collect()
    }

    fun callGetProductDetails(productId: Int) {
        val data = mapOf(UseCaseConstants.PRODUCT_ID to productId)
        productDetailUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            if (it.success)
                _productDetailsLiveData.value = it.data
            else
                _errorLiveData.value = it.message
        }, {
            Utility.cancelProgress()
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        })
            .collect()
    }

    fun callGetProductFullName(productSortName: String) {
        val data = mapOf(UseCaseConstants.PRODUCT_SORT_NAME to productSortName)
        productFullNameUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            if (it.success)
                _productFullNameLiveData.value = it.data
            else
                _errorLiveData.value = it.message
        }, {
            Utility.cancelProgress()
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        })
            .collect()
    }

    fun callGetRelatedProducts(productId: Int, perPage: Int) {
        val data =
            mapOf(UseCaseConstants.PRODUCT_ID to productId, UseCaseConstants.PER_PAGE to perPage)
        relatedProductsUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            if (it.success) {
                _relatedProductsLiveData.value = it
                _isNextPageUrl.value = it.data?.nextPageUrl != null
            } else {
                val response = BaseResponse<RelatedProductsResponse>()
                response.success = false
                _relatedProductsLiveData.value = response
            }
        }, {
            Utility.cancelProgress()
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        })
            .collect()
    }

    fun callAddReview(
        productId: Int,
        review_text: String,
        rating: Int,
        user_id: Int,
        email: String,
        name: String
    ) {
        val data = mapOf(
            UseCaseConstants.PRODUCT_ID to productId,
            UseCaseConstants.REVIEW_TEXT to review_text,
            UseCaseConstants.RATING to rating,
            UseCaseConstants.USER_ID to user_id,
            UseCaseConstants.EMAIL to email,
            UseCaseConstants.NAME to name
        )
        addReviewUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            if (it.success)
                _addReviewLiveData.value = it
            else
                _errorLiveData.value = it.message
        }, {
            Utility.cancelProgress()
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        })
            .collect()
    }
}

