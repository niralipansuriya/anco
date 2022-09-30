package com.app.ancoturf.domain.product.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.product.ProductRepository
import io.reactivex.Single
import javax.inject.Inject

class ProductDetailUseCase @Inject constructor(private val productRepository: ProductRepository) :
    BaseUseCase<BaseResponse<ProductDetailResponse>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<ProductDetailResponse>> {
        return productRepository.getProductDetail(productId = data?.get(UseCaseConstants.PRODUCT_ID) as Int)
    }
}