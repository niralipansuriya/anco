package com.app.ancoturf.presentation.home.order

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.R
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.afterpay.remote.entity.response.OrderResponse
import com.app.ancoturf.data.common.exceptions.AncoHttpException
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.order.remote.entity.response.OrderDetailResponse
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.order.usecases.CancelOrderUseCase

import com.app.ancoturf.domain.order.usecases.OrderDetailsUseCase

import com.app.ancoturf.domain.order.usecases.OrderUseCase
import com.app.ancoturf.domain.order.usecases.RescheduleOrderUseCase
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import java.text.FieldPosition
import javax.inject.Inject

class OrderViewModel @Inject constructor(
    val app: Application
    , val orderUseCase: OrderUseCase
    , val orderDetailsUseCase: OrderDetailsUseCase
    , val rescheduleOrderUseCase: RescheduleOrderUseCase
    , val cancelOrderUseCase: CancelOrderUseCase
) : BaseObservableViewModel(app) {

    val _orderLiveData = MutableLiveData<ArrayList<OrderDetailResponse>>()
    val orderLiveData: LiveData<ArrayList<OrderDetailResponse>> get() = _orderLiveData

    val _orderStatusLiveData = MutableLiveData<ArrayList<SettingsResponse.Data.OrderStatus>>()
    val orderStatusLiveData: LiveData<ArrayList<SettingsResponse.Data.OrderStatus>> get() = _orderStatusLiveData

    val _rescheduleOrderLiveData = MutableLiveData<BaseResponse<OrderDetailResponse>>()
    val rescheduleOrderLiveData: LiveData<BaseResponse<OrderDetailResponse>> get() = _rescheduleOrderLiveData

    val _cancelOrderLiveData = MutableLiveData<BaseResponse<OrderDetailResponse>>()
    val cancelOrderLiveData: LiveData<BaseResponse<OrderDetailResponse>> get() = _cancelOrderLiveData

    val _orderDetailLiveData = MutableLiveData<OrderDetailResponse>()
    val orderDetailLiveData: LiveData<OrderDetailResponse> get() = _orderDetailLiveData

    val _tagsLiveData = MutableLiveData<ArrayList<SettingsResponse.Data.Tag>>()


    @Inject
    lateinit var sharedPrefs: SharedPrefs

    var filterAvailable = false
    var title: String? = app.getString(R.string.shop_all)
    var numberOfFilters: Int? = 0
    var isfilterApplied = false
    var position = 0

    val data = mutableMapOf(
        UseCaseConstants.PER_PAGE to "",
        UseCaseConstants.ORDER_STATUS to "",
        UseCaseConstants.DELIVERY_STATUS to "",
        UseCaseConstants.PRICE_MIN to "",
        UseCaseConstants.PRICE_MAX to "",
        UseCaseConstants.ADDRESS to "",
        UseCaseConstants.DELIVERY_DATE_FROM to "",
        UseCaseConstants.SORT_BY to ""

    )


    fun callGetOrders() {
        orderUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success && it.data != null && it.data.data != null) {
                sharedPrefs.order_max_price = it.order_max_price
                _orderLiveData.value = it.data.data
                _isNextPageUrl.value = it.data.nextPageUrl != null
            } else
                _errorLiveData.value = "No order found"
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

    fun callGetAllOrdersWithPaging(page: String) {

        data.put(UseCaseConstants.PER_PAGE,page)

        orderUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success && it.data != null && it.data.data != null) {
                sharedPrefs.order_max_price = it.order_max_price
                _orderLiveData.value = it.data.data
                _isNextPageUrl.value = it.data.nextPageUrl != null
            } else
                _errorLiveData.value = "No order found"
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

    fun callGetOrderDetails(reference_number: String) {
        val data = mutableMapOf(
            UseCaseConstants.REFERENCE_NO to reference_number
        )

        orderDetailsUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            if (it.success && it.data != null)
                _orderDetailLiveData.value = it.data
            else
                _errorLiveData.value = "No order found"
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

    fun callRescheduleOrders(orderId: Int , deliveryDate: String) {
        val data = mutableMapOf(
            UseCaseConstants.ORDER_ID to orderId,
            UseCaseConstants.DELIVERY_DATE to deliveryDate)

        rescheduleOrderUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            _rescheduleOrderLiveData.value = it
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

    fun callCancelOrders(orderId: Int,position: Int) {
        val data = mutableMapOf(
            UseCaseConstants.ORDER_ID to orderId)
        this.position = position
        cancelOrderUseCase.execute(data).customSubscribe({
            Log.e("success", "${it.success}")
            Utility.cancelProgress()
            _cancelOrderLiveData.value = it
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
                    Utility.isValueNull(data.get(UseCaseConstants.ORDER_STATUS) as String) &&
                    Utility.isValueNull(data.get(UseCaseConstants.DELIVERY_STATUS) as String) &&
                    Utility.isValueNull(data.get(UseCaseConstants.ADDRESS) as String) &&
                    (Utility.isValueNull(data.get(UseCaseConstants.PRICE_MIN) as String) || data.get(
                        UseCaseConstants.PRICE_MIN
                    ).equals(
                        "0"
                    )) &&
                    (Utility.isValueNull(data.get(UseCaseConstants.PRICE_MAX) as String) || data.get(
                        UseCaseConstants.PRICE_MAX
                    ).equals(
                        sharedPrefs.order_max_price
                    ))
                    )
        } else
            return false

    }


}