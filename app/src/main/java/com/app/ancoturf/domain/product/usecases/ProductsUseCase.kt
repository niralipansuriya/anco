package com.app.ancoturf.domain.product.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.product.ProductRepository
import io.reactivex.Single
import javax.inject.Inject

class ProductsUseCase @Inject constructor(private val productRepository: ProductRepository) :
    BaseUseCase<BaseResponse<ProductsResponse>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<ProductsResponse>> {
        return if (data != null)
            productRepository.getProductsByCategory(categoryIds = data[UseCaseConstants.PRODUCT_CATEGORY_IDS] as String,
                tagIds = data[UseCaseConstants.PRODUCT_TAG_IDS] as String,
                minPrice = data[UseCaseConstants.PRICE_MIN] as String,
                maxPrice = data[UseCaseConstants.PRICE_MAX] as String,
                search = data[UseCaseConstants.SEARCH] as String,
                sortBy = data[UseCaseConstants.SORT_BY] as String,
                page = data[UseCaseConstants.PER_PAGE] as String)
        else
            productRepository.getAllProducts(page = data?.get(UseCaseConstants.PER_PAGE) as String)
    }

}