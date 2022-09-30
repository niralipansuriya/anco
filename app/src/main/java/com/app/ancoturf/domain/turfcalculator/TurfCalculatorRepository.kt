package com.app.ancoturf.domain.turfcalculator

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.product.remote.entity.response.*
import io.reactivex.Single

interface TurfCalculatorRepository {


    fun getTurfProducts(): Single<BaseResponse<ArrayList<ProductsResponse.Data>>>


}