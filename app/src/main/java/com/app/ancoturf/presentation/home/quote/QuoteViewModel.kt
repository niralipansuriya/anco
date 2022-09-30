package com.app.ancoturf.presentation.home.quote

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.R
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.common.exceptions.AncoHttpException
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.portfolio.remote.entity.QuoteAncoProductRequest
import com.app.ancoturf.data.portfolio.remote.entity.QuoteNonAncoProductRequest
import com.app.ancoturf.data.product.remote.entity.response.ProductCategoryData
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.data.quote.remote.entity.response.*
import com.app.ancoturf.domain.account.usecases.UserDetailUseCase
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.product.usecases.ProductCategoryUseCase
import com.app.ancoturf.domain.product.usecases.ProductsUseCase
import com.app.ancoturf.domain.quote.usecases.*
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import javax.inject.Inject

class QuoteViewModel @Inject constructor(
    val app: Application
    , val quotesUseCase: QuotesUseCase
    , val userDetailUseCase: UserDetailUseCase
    , val addEditBusinessDetailsUseCase: AddEditBusinessDetailsUseCase
    , val addEditCustomersUseCase: AddEditCustomersUseCase
    , val customersUseCase: CustomersUseCase
    , val addEditQuotesUseCase: AddEditQuotesUseCase
    , val resendQuotesUseCase: ResendQuotesUseCase
    , val duplicateQuotesUseCase: DuplicateQuotesUseCase
    , val quoteDetailsUseCase: QuoteDetailsUseCase
    , val addCustomProductsUseCase: AddCustomProductsUseCase
    , val productCategoryUseCase: ProductCategoryUseCase
    , val productsUseCase: ProductsUseCase
) : BaseObservableViewModel(app) {

    val _quotesLiveData = MutableLiveData<ArrayList<QuoteDetailsResponse>>()
    val quotesLiveData: LiveData<ArrayList<QuoteDetailsResponse>> get() = _quotesLiveData

    val _customersLiveData = MutableLiveData<CustomersDataResponse>()
    val customersLiveData: LiveData<CustomersDataResponse> get() = _customersLiveData

    val _userInfoLiveData = MutableLiveData<UserInfo>()
    val userInfoLiveData: LiveData<UserInfo> get() = _userInfoLiveData

    val _addEditQuotesLiveData = MutableLiveData<BaseResponse<AddEditQuoteResponse>>()
    val addEditQuotesLiveData: LiveData<BaseResponse<AddEditQuoteResponse>> get() = _addEditQuotesLiveData

    val _duplicateQuotesLiveData = MutableLiveData<DuplicateQuoteResponse>()
    val duplicateQuotesLiveData: LiveData<DuplicateQuoteResponse> get() = _duplicateQuotesLiveData

    val _quoteDetailsLiveData = MutableLiveData<QuoteDetailsResponse>()
    val quoteDetailsLiveData: LiveData<QuoteDetailsResponse> get() = _quoteDetailsLiveData

    val _customProductLiveData = MutableLiveData<BaseResponse<CustomProductResponse>>()
    val customProductLiveData: LiveData<BaseResponse<CustomProductResponse>> get() = _customProductLiveData

    private val _productCategoryLiveData = MutableLiveData<ArrayList<ProductCategoryData>>()
    val productCategoryLiveData: LiveData<ArrayList<ProductCategoryData>> get() = _productCategoryLiveData

    val _productsLiveData = MutableLiveData<ProductsResponse>()
    val productsLiveData: LiveData<ProductsResponse> get() = _productsLiveData

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    var isNextPage = false
    var filterAvailable = false


    val data = mutableMapOf(
        UseCaseConstants.STATUS to "",
        UseCaseConstants.COST_MIN to "",
        UseCaseConstants.COST_MAX to "",
        UseCaseConstants.ADDRESS to "",
        UseCaseConstants.SORT_BY to "",
        UseCaseConstants.PER_PAGE to ""
    )

    fun callGetQuotes() {
        quotesUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success && it.data != null && it.data.data != null)
            {
                sharedPrefs.quote_max_price  = it.quote_max_price
                _quotesLiveData.value = it.data.data
                _isNextPageUrl.value = it.data.nextPageUrl !=null
            }
            else
                _errorLiveData.value = "No quotes found"
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

    fun callGetAllQuotesWithPaging(page : String) : Boolean
    {
        val data = mutableMapOf(
            UseCaseConstants.STATUS to "",
            UseCaseConstants.COST_MIN to "",
            UseCaseConstants.COST_MAX to "",
            UseCaseConstants.ADDRESS to "",
            UseCaseConstants.SORT_BY to "",
            UseCaseConstants.PER_PAGE to page
        )

        quotesUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success && it.data != null && it.data.data != null)
            {
                sharedPrefs.quote_max_price  = it.quote_max_price
                _quotesLiveData.value = it.data.data
                _isNextPageUrl.value = it.data.nextPageUrl !=null
            }
            else
                _errorLiveData.value = "No quotes found"
        }, {
            Utility.cancelProgress()
            Log.e("failure", "$it")
            if (it is AncoHttpException.AncoUnauthorizedException) {
                _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
            } else {
                _errorLiveData.value = it?.getErrorMessage()
            }
        }).collect()



        return isNextPage
    }


    fun callGetUserDetails() {
        userDetailUseCase.execute().customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _userInfoLiveData.value = it.data
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

    fun callGetProducts(productCategory: String , perPage: String) {

        val data = mutableMapOf(
            UseCaseConstants.PRODUCT_CATEGORY_IDS to productCategory,
            UseCaseConstants.PRODUCT_TAG_IDS to "",
            UseCaseConstants.PRICE_MIN to "",
            UseCaseConstants.PRICE_MAX to "",
            UseCaseConstants.SEARCH to "",
            UseCaseConstants.SORT_BY to "",
            UseCaseConstants.PER_PAGE to perPage
        )

        productsUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            if (it.success) {
                _productsLiveData.value = it.data
                _isNextPageUrlForProducts.value = it.data?.nextPageUrl !=null
            }
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

    fun callAddEditBusinessDetails(
        quoteId: Int,
        contactName: String,
        businessName: String,
        abn: String,
        address: String,
        mobileNumber: String,
        phoneNumber: String?,
        email: String,
        webUrl: String?,
        paymentTerms: String?,
        disclaimer: String?,
        registeredForGst: String?,
        logoUrl: String?,
        products: ArrayList<QuoteAncoProductRequest>?
    ) {

        val data = mapOf(
            UseCaseConstants.QUOTE_ID to quoteId,
            UseCaseConstants.CONTACT_NAME to contactName,
            UseCaseConstants.BUSINESS_NAME to businessName,
            UseCaseConstants.ABN to abn,
            UseCaseConstants.ADDRESS to address,
            UseCaseConstants.MOBILE_NUMBER to mobileNumber,
            UseCaseConstants.PHONE_NUMBER to phoneNumber,
            UseCaseConstants.EMAIL to email,
            UseCaseConstants.WEB_URL to webUrl,
            UseCaseConstants.PAYMENT_TERMS to paymentTerms,
            UseCaseConstants.DISCLAIMER to disclaimer,
            UseCaseConstants.REGISTERED_FOR_GST to registeredForGst,
            UseCaseConstants.LOGO_URL to logoUrl,
            UseCaseConstants.PRODUCTS to products
        )

        addEditBusinessDetailsUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _addEditQuotesLiveData.value = it
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

    fun callGetCustomers() {
        customersUseCase.execute().customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _customersLiveData.value = it.data
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


    fun callAddEditCustomer(
        quoteId: Int,
        customerId: Int,
        customerName: String,
        customerAddress: String,
        customerEmail: String,
        customerMobile: String,
        customerPhone: String?,
        contactName: String,
        businessName: String,
        abn: String,
        address: String,
        mobileNumber: String,
        phoneNumber: String?,
        email: String,
        webUrl: String?,
        paymentTerms: String?,
        disclaimer: String?,
        registeredForGst: String?
    ) {

        val data = mapOf(
            UseCaseConstants.QUOTE_ID to quoteId,
            UseCaseConstants.CUSTOMER_ID to customerId,
            UseCaseConstants.CUSTOMER_NAME to customerName,
            UseCaseConstants.CUSTOMER_ADDRESS to customerAddress,
            UseCaseConstants.CUSTOMER_MOBILE to customerMobile,
            UseCaseConstants.CUSTOMER_EMAIL to customerEmail,
            UseCaseConstants.CUSTOMER_PHONE to customerPhone,
            UseCaseConstants.CONTACT_NAME to contactName,
            UseCaseConstants.BUSINESS_NAME to businessName,
            UseCaseConstants.ABN to abn,
            UseCaseConstants.ADDRESS to address,
            UseCaseConstants.MOBILE_NUMBER to mobileNumber,
            UseCaseConstants.PHONE_NUMBER to phoneNumber,
            UseCaseConstants.EMAIL to email,
            UseCaseConstants.WEB_URL to webUrl,
            UseCaseConstants.PAYMENT_TERMS to paymentTerms,
            UseCaseConstants.DISCLAIMER to disclaimer,
            UseCaseConstants.REGISTERED_FOR_GST to registeredForGst
        )

        addEditCustomersUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _addEditQuotesLiveData.value = it
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

    fun callAddEditQuote(
        quoteId: Int,
        customerId: Int,
        customerName: String,
        customerAddress: String,
        customerEmail: String,
        customerMobile: String,
        customerPhone: String?,
        deliveryCost: String?,
        deliveryDate: String?,
        products: ArrayList<QuoteAncoProductRequest>?,
        customProducts: ArrayList<QuoteNonAncoProductRequest>?,
        deletedProductIds: ArrayList<Int>?,
        deletedCustomProductIds: ArrayList<Int>?,
        contactName: String,
        businessName: String,
        abn: String,
        address: String,
        mobileNumber: String,
        phoneNumber: String?,
        email: String,
        webUrl: String?,
        paymentTerms: String?,
        disclaimer: String?,
        registeredForGst: String?,
        logoUrl: String?,
        sendQuoteTo: ArrayList<String>?,
        deepLinkUrl:String?,
        note: String?
    ) {

        val data = mapOf(
            UseCaseConstants.QUOTE_ID to quoteId,
            UseCaseConstants.CUSTOMER_ID to customerId,
            UseCaseConstants.CUSTOMER_NAME to customerName,
            UseCaseConstants.CUSTOMER_ADDRESS to customerAddress,
            UseCaseConstants.CUSTOMER_MOBILE to customerMobile,
            UseCaseConstants.CUSTOMER_EMAIL to customerEmail,
            UseCaseConstants.CUSTOMER_PHONE to customerPhone,
            UseCaseConstants.DELIVERY_COST to deliveryCost,
            UseCaseConstants.DELIVERY_DATE to deliveryDate,
            UseCaseConstants.PRODUCTS to products,
            UseCaseConstants.CUSTOM_PRODUCTS to customProducts,
            UseCaseConstants.DELETED_PRODUCT_IDS to deletedProductIds,
            UseCaseConstants.DELETED_CUSTOM_PRODUCT_IDS to deletedCustomProductIds,
            UseCaseConstants.CONTACT_NAME to contactName,
            UseCaseConstants.BUSINESS_NAME to businessName,
            UseCaseConstants.ABN to abn,
            UseCaseConstants.ADDRESS to address,
            UseCaseConstants.MOBILE_NUMBER to mobileNumber,
            UseCaseConstants.PHONE_NUMBER to phoneNumber,
            UseCaseConstants.EMAIL to email,
            UseCaseConstants.WEB_URL to webUrl,
            UseCaseConstants.PAYMENT_TERMS to paymentTerms,
            UseCaseConstants.DISCLAIMER to disclaimer,
            UseCaseConstants.REGISTERED_FOR_GST to registeredForGst,
            UseCaseConstants.LOGO_URL to logoUrl,
            UseCaseConstants.SEND_QUOTE_TO to sendQuoteTo,
            UseCaseConstants.DEEP_LINK_URL to deepLinkUrl,
            UseCaseConstants.NOTE to note
        )

        addEditQuotesUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _addEditQuotesLiveData.value = it
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

    fun callGetQuoteDetails(quoteId: Int) {

        val data = mapOf(
            UseCaseConstants.QUOTE_ID to quoteId
        )

        quoteDetailsUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _quoteDetailsLiveData.value = it.data
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

    fun callAddCustomProduct(
        customProductId: Int,
        name: String,
        descriptions: String,
        price: String,
        imagePath: String?
    ) {

        val data = mapOf(
            UseCaseConstants.CUSTOM_PRODUCT_ID to customProductId,
            UseCaseConstants.NAME to name,
            UseCaseConstants.DESCRIPTIONS to descriptions,
            UseCaseConstants.PRICE to price,
            UseCaseConstants.IMAGE to imagePath
        )

        addCustomProductsUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _customProductLiveData.value = it
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

    fun callResendQuote(quoteId: Int, sendQuoteTo: ArrayList<String>?) {
        val data = mapOf(
            UseCaseConstants.QUOTE_ID to quoteId,
            UseCaseConstants.SEND_QUOTE_TO to sendQuoteTo
        )

        resendQuotesUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _addEditQuotesLiveData.value = it
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

    fun callDuplicateQuote(quoteId: Int) {
        val data = mapOf(
            UseCaseConstants.QUOTE_ID to quoteId
        )

        duplicateQuotesUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success)
                _duplicateQuotesLiveData.value = it.data
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

    fun isFilterApplied(): Boolean {
        if (data != null) {
            return !(Utility.isValueNull(data.get(UseCaseConstants.SORT_BY) as String) &&
                    Utility.isValueNull(data.get(UseCaseConstants.STATUS) as String) &&
                    Utility.isValueNull(data.get(UseCaseConstants.ADDRESS) as String) &&
                    (Utility.isValueNull(data.get(UseCaseConstants.COST_MIN) as String) || data.get(UseCaseConstants.COST_MIN).equals(
                        "0"
                    )) &&
                    (Utility.isValueNull(data.get(UseCaseConstants.COST_MAX) as String) || data.get(UseCaseConstants.COST_MAX).equals(
                        sharedPrefs.quote_max_price
                    )))
        } else
            return false

    }

}

