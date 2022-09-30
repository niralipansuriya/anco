package com.app.ancoturf.domain.product.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.product.remote.entity.response.RelatedProductsResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.product.ProductRepository
import io.reactivex.Single
import javax.inject.Inject

class RelatedProductsUseCase @Inject constructor(private val productRepository: ProductRepository) :
    BaseUseCase<BaseResponse<RelatedProductsResponse>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<RelatedProductsResponse>> {
            return productRepository.getRelatedProducts(productId = data?.get(UseCaseConstants.PRODUCT_ID) as Int ,
                perPage = data?.get(UseCaseConstants.PER_PAGE) as Int)
    }

}