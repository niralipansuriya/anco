package com.app.ancoturf.presentation.home.offers

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.data.common.exceptions.AncoHttpException
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.offer.remote.OfferDataResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.offer.usecases.OffersUseCase
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import javax.inject.Inject

class OffersViewModel @Inject constructor(
    val app: Application
    , val offersUseCase: OffersUseCase
) : BaseObservableViewModel(app) {

    val _offersLiveData = MutableLiveData<OfferDataResponse>()
    val offersLiveData: LiveData<OfferDataResponse> get() = _offersLiveData

    @Inject
    lateinit var sharedPrefs: SharedPrefs


    fun callGetOffers(page: String) {
        val data = mutableMapOf(
            UseCaseConstants.PER_PAGE to page
        )

        offersUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            if (it.success && it.data != null) {
                _offersLiveData.value = it.data
                _isNextPageUrl.value = it.data.nextPageUrl != null
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

}

