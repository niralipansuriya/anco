package com.app.ancoturf.domain.product.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductCategoryData
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.product.ProductRepository
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class ProductCategoryUseCase @Inject constructor(private val productRepository: ProductRepository) :  BaseUseCase<BaseResponse<ArrayList<ProductCategoryData>>>(){

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<ArrayList<ProductCategoryData>>> {
        return productRepository.getProductCategories()
    }


}