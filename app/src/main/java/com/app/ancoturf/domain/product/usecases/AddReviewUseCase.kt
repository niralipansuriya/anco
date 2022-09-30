package com.app.ancoturf.domain.product.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.product.remote.entity.response.AddReviewResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.product.ProductRepository
import io.reactivex.Single
import javax.inject.Inject

class AddReviewUseCase @Inject constructor(private val productRepository: ProductRepository) :
    BaseUseCase<BaseResponse<AddReviewResponse>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<AddReviewResponse>> {
        return productRepository.addReview(productId = data?.get(UseCaseConstants.PRODUCT_ID) as Int,
            review_text = data?.get(UseCaseConstants.REVIEW_TEXT) as String,
            rating = data?.get(UseCaseConstants.RATING) as Int,
            email = data?.get(UseCaseConstants.EMAIL) as String,
            user_id = data?.get(UseCaseConstants.USER_ID) as Int,
            name = data?.get(UseCaseConstants.NAME) as String)
    }
}