package com.app.ancoturf.domain.turfcalculator.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.data.turdcalculator.TurfCalculatorDataRepository
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.turfcalculator.TurfCalculatorRepository
import io.reactivex.Single
import javax.inject.Inject

class TurfProductsUseCase @Inject constructor(private val turfCalculatorRepository: TurfCalculatorRepository) :
    BaseUseCase<BaseResponse<ArrayList<ProductsResponse.Data>>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<ArrayList<ProductsResponse.Data>>> {
        return turfCalculatorRepository.getTurfProducts()
    }

}