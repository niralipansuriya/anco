package com.app.ancoturf.presentation.home.aboutUs

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.data.aboutUs.FAQResponse
import com.app.ancoturf.data.aboutUs.FAQTagsResponse
import com.app.ancoturf.data.common.exceptions.AncoHttpException
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.lawntips.remote.LawnTipsDataResponse
import com.app.ancoturf.data.lawntips.remote.LawnTipsDetailResponse
import com.app.ancoturf.domain.AboutUs.usecases.AboutUsDetailUseCase
import com.app.ancoturf.domain.AboutUs.usecases.AboutUsUseCases
import com.app.ancoturf.domain.AboutUs.usecases.FAQTagsUseCase
import com.app.ancoturf.domain.AboutUs.usecases.FAQUseCase
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.lawntips.usecases.LawnTipsDetailUseCase
import com.app.ancoturf.domain.lawntips.usecases.LawnTipsUseCase
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import javax.inject.Inject

class AboutUsViewModel @Inject constructor(
    val app: Application,
    var aboutUsUseCases: AboutUsUseCases,
    var aboutUsDetailUseCase: AboutUsDetailUseCase,
    var faqUseCase: FAQUseCase,
    var faqTagsUseCase: FAQTagsUseCase
) : BaseObservableViewModel(app) {

    val _lawntipsLiveData = MutableLiveData<LawnTipsDataResponse>()
    val lawntipsLiveData: LiveData<LawnTipsDataResponse> get() = _lawntipsLiveData

    val _lawntipsDetailsLiveData = MutableLiveData<LawnTipsDetailResponse>()
    val lawntipsDetailsLiveData: LiveData<LawnTipsDetailResponse> get() = _lawntipsDetailsLiveData

    val _faqLivaData = MutableLiveData<FAQResponse>()
    val faqLiveData: LiveData<FAQResponse> get() = _faqLivaData

    val _faqTagLivaData = MutableLiveData<FAQTagsResponse>()
    val faqTagLiveData: LiveData<FAQTagsResponse> get() = _faqTagLivaData

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    fun getFAQ(page: String){
        val data = mutableMapOf(UseCaseConstants.PER_PAGE to page)
        faqUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()

            if (it.success && it.data != null) {
                _faqLivaData.value = it
            } else
                _errorLiveData.value = it.message

        },
            {
                Utility.cancelProgress()
                if (it is AncoHttpException.AncoUnauthorizedException) {
                    _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
                } else {
                    _errorLiveData.value = it?.getErrorMessage()
                }
            }
        ).collect()
    }


    fun getFAQTags(page: String){
        val data = mutableMapOf(UseCaseConstants.PER_PAGE to page)
            faqTagsUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()

            if (it.success && it.data != null) {
                _faqTagLivaData.value = it
            } else
                _errorLiveData.value = it.message

        },
            {
                Utility.cancelProgress()
                if (it is AncoHttpException.AncoUnauthorizedException) {
                    _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
                } else {
                    _errorLiveData.value = it?.getErrorMessage()
                }
            }
        ).collect()


    }

    fun getAboutUs(page: String) {
        val data = mutableMapOf(UseCaseConstants.PER_PAGE to page)

        aboutUsUseCases.execute(data).customSubscribe({
            Utility.cancelProgress()

            if (it.success && it.data != null) {
                _lawntipsLiveData.value = it.data
                _isNextPageUrl.value = it.data.nextPageUrl != null
            } else
                _errorLiveData.value = it.message

        },
            {
                Utility.cancelProgress()
                if (it is AncoHttpException.AncoUnauthorizedException) {
                    _errorLiveData.value = ErrorConstants.UNAUTHORIZED_ERROR_CODE
                } else {
                    _errorLiveData.value = it?.getErrorMessage()
                }
            }
        ).collect()
    }

    fun getAboutUsDetail(id: Int) {
        val data = mapOf(UseCaseConstants.LAWNTIPS_ID to id)
        aboutUsDetailUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            if (it.success)
                _lawntipsDetailsLiveData.value = it.data
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
