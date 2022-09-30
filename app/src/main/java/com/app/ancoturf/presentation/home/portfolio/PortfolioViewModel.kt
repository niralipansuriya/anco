package com.app.ancoturf.presentation.home.portfolio

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.exceptions.AncoHttpException
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.portfolio.remote.entity.*
import com.app.ancoturf.data.product.remote.entity.response.ProductCategoryData
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.portfolio.usecases.AddEditPortfolioUseCase
import com.app.ancoturf.domain.portfolio.usecases.DeletePortfolioUseCase
import com.app.ancoturf.domain.portfolio.usecases.PortfolioDetailUseCase
import com.app.ancoturf.domain.portfolio.usecases.PortfolioUseCase
import com.app.ancoturf.domain.product.usecases.ProductCategoryUseCase
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import javax.inject.Inject

class PortfolioViewModel @Inject constructor(
    val app: Application
    , val portfolioUseCase: PortfolioUseCase
    , val productCategoryUseCase: ProductCategoryUseCase
    , val portfolioDetailUseCase: PortfolioDetailUseCase
    , val addEditPortfolioUseCase: AddEditPortfolioUseCase
    , val deletePortfolioUseCase: DeletePortfolioUseCase
) : BaseObservableViewModel(app) {

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private val _portfoliosLiveData = MutableLiveData<PortfolioResponse>()
    val portfoliosLiveData: LiveData<PortfolioResponse> get() = _portfoliosLiveData

    private val _productCategoryLiveData = MutableLiveData<ArrayList<ProductCategoryData>>()
    val productCategoryLiveData: LiveData<ArrayList<ProductCategoryData>> get() = _productCategoryLiveData

    val _portfolioDetailLiveData = MutableLiveData<PortfolioDetailResponse>()
    val portfolioDetailLiveData: LiveData<PortfolioDetailResponse> get() = _portfolioDetailLiveData

    val _addEditPortfolioLiveData = MutableLiveData<BaseResponse<AddEditPortfolioResponse>>()
    val addEditPortfolioLiveData: LiveData<BaseResponse<AddEditPortfolioResponse>> get() = _addEditPortfolioLiveData

    val _deletePortfolioLiveData = MutableLiveData<BaseResponse<PortfolioDetailResponse>>()
    val deletePortfolioLiveData: LiveData<BaseResponse<PortfolioDetailResponse>> get() = _deletePortfolioLiveData

    val _ancoProductLiveData = MutableLiveData<ArrayList<ProductsResponse.Data>>()
    val ancoProductLiveData: LiveData<ArrayList<ProductsResponse.Data>> get() = _ancoProductLiveData

    var isAnythingEdited = false

    fun callGetPortfolios() {
        portfolioUseCase.execute().customSubscribe({
            Utility.cancelProgress()
            if (it.success && it.data != null)
                _portfoliosLiveData.value = it.data
            else
                _errorLiveData.value = it.message
        }, {
            Utility.cancelProgress()
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        }).collect()
    }

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
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()

            }
        }).collect()
    }

    fun callGetPortfolioDetails(portfolioId: Int) {
        val data = mapOf(UseCaseConstants.PORTFOLIO_ID to portfolioId)
        portfolioDetailUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _portfolioDetailLiveData.value = it.data
            else
                _errorLiveData.value = it.message
        }, {
            Utility.cancelProgress()
            Log.e("failure", "$it")
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        }).collect()
    }

    fun callDeletePortfolio(portfolioId: Int) {
        val data = mapOf(UseCaseConstants.PORTFOLIO_ID to portfolioId)
        deletePortfolioUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _deletePortfolioLiveData.value = it
            else
                _errorLiveData.value = it.message
        }, {
            Utility.cancelProgress()
            Log.e("failure", "$it")
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        }).collect()
    }

    fun callAddEditPortfolio(
        portfolioId: Int,
        projectName: String,
        city: String,
        budget: Float,
        projectDescription: String,
        address: String,
        featuredImageIndex: Int,
        featuredImageId: Int,
        imageIds: ArrayList<Int>,
        deletedImageIds: ArrayList<Int>,
        deletedProductIds: ArrayList<Int>,
        deletedCustomProductIds: ArrayList<Int>,
        filePaths: ArrayList<String>,
        nonAncoProducts: ArrayList<NonAncoProductRequest>,
        updatednonAncoProducts: ArrayList<NonAncoProductRequest>,
        orderedImageIds: String?
    ) {

        val ancoProducts = ArrayList<QuoteAncoProductRequest>()
        if (ancoProductLiveData.value != null && ancoProductLiveData.value!!.size > 0) {
            for (i in 0 until ancoProductLiveData.value!!.size) {
                ancoProducts.add(
                    QuoteAncoProductRequest(
                        ancoProductLiveData.value!![i].productId,
                        ancoProductLiveData.value!![i].qty
                    )
                )
            }
        }

        val data = mapOf(
            UseCaseConstants.PORTFOLIO_ID to portfolioId,
            UseCaseConstants.PROJECT_NAME to projectName,
            UseCaseConstants.CITY to city,
            UseCaseConstants.BUDGET to budget,
            UseCaseConstants.PROJECT_DESCRIPTION to projectDescription,
            UseCaseConstants.ADDRESS to address,
            UseCaseConstants.PRODUCTS to ancoProducts,
            UseCaseConstants.CUSTOM_PRODUCTS to nonAncoProducts,
            UseCaseConstants.FEATURED_IMAGE_INDEX to featuredImageIndex,
            UseCaseConstants.DELETED_IMAGE_IDS to deletedImageIds,
            UseCaseConstants.PORTFOLIO_IMAGES to filePaths,
            UseCaseConstants.IMAGE_IDS to imageIds,
            UseCaseConstants.FEATURED_IMAGE_ID to featuredImageId,
            UseCaseConstants.DELETED_PRODUCT_IDS to deletedProductIds,
            UseCaseConstants.DELETED_IMAGE_IDS to deletedImageIds,
            UseCaseConstants.DELETED_CUSTOM_PRODUCT_IDS to deletedCustomProductIds,
            UseCaseConstants.UPDATED_CUSTOM_PRODUCTS to updatednonAncoProducts,
            UseCaseConstants.ORDERED_IMAGE_IDS to orderedImageIds
        )
        addEditPortfolioUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _addEditPortfolioLiveData.value = it
            else
                _errorLiveData.value = it.message
        }, {
            Utility.cancelProgress()
            Log.e("failure", "$it")
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        }).collect()
    }


}

