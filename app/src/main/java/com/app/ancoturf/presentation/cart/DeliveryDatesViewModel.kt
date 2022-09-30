package com.app.ancoturf.presentation.cart

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.domain.cart.usecases.GetDeliveryDatesUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import com.google.gson.Gson
import javax.inject.Inject

class DeliveryDatesViewModel @Inject constructor(
    val app: Application,
    private val getDeliveryDatesUseCase: GetDeliveryDatesUseCase
) :
    BaseObservableViewModel(app) {

    val _deliveryDatesLiveData = MutableLiveData<String>()
    val deliveryDatesLiveData: LiveData<String> get() = _deliveryDatesLiveData

    fun getDeliveryDates(user_type :Int) {
        val data = mapOf(
            UseCaseConstants.USER_TYPE to user_type
        )
        getDeliveryDatesUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            Log.e("success", "${it}")
            _deliveryDatesLiveData.value = Gson().toJson(it, Map::class.java)
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _deliveryDatesLiveData.value = null
        }).collect()
    }
}