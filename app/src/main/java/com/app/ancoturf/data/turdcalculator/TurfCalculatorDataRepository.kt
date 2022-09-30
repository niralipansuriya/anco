package com.app.ancoturf.data.turdcalculator

import android.text.TextUtils
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.request.RegisterRequest
import com.app.ancoturf.data.common.LogStatus
import com.app.ancoturf.data.common.RequestConstants
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.data.product.remote.ProductService
import com.app.ancoturf.data.product.remote.entity.request.AddReviewRequest
import com.app.ancoturf.data.product.remote.entity.response.*
import com.app.ancoturf.data.turdcalculator.remote.TurfCalculatorService
import com.app.ancoturf.domain.product.ProductRepository
import com.app.ancoturf.domain.turfcalculator.TurfCalculatorRepository
import com.app.ancoturf.extension.printLog
import com.app.ancoturf.utils.Utility
import com.google.gson.JsonObject
import io.reactivex.Single
import javax.inject.Inject

class TurfCalculatorDataRepository @Inject constructor() : TurfCalculatorRepository, CommonService<TurfCalculatorService>() {


    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = TurfCalculatorService::class.java

    override fun getTurfProducts(): Single<BaseResponse<ArrayList<ProductsResponse.Data>>> {
        return networkService.getTurfProducts().map {
            it
        }
    }

}