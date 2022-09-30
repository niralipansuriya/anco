package com.app.ancoturf.domain.cart.usecases

import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.cart.CartRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class InsertProductUseCase @Inject constructor(private val cartRepository: CartRepository) : BaseUseCase<Boolean>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<Boolean> {
        var productDB = ProductDB(
            product_id = data?.get(UseCaseConstants.PRODUCT_ID) as Int,
            feature_img_url = data?.get(UseCaseConstants.PRODUCT_FEATURE_IMAGE_URL) as String,
            qty = data?.get(UseCaseConstants.PRODUCT_QTY) as Int,
            price = data?.get(UseCaseConstants.PRODUCT_PRICE) as Float,
            product_category_id = data?.get(UseCaseConstants.PRODUCT_CATEGORY_ID) as Int,
            product_name = data?.get(UseCaseConstants.PRODUCT_NAME) as String,
            product_unit = data?.get(UseCaseConstants.PRODUCT_UNIT) as String,
            product_unit_id = data?.get(UseCaseConstants.PRODUCT_UNIT_ID) as Int,
            base_total_price = data?.get(UseCaseConstants.PRODUCT_BASE_TOTAL_PRICE) as Float,
            is_turf = data?.get(UseCaseConstants.PRODUCT_IS_TURF) as Int?,
            total_price = data?.get(UseCaseConstants.PRODUCT_TOTAL_PRICE) as Float,
            product_redeemable_against_credit = data?.get(UseCaseConstants.PRODUCT_REDEEMABLE_AGAINST_CREDIT) as Boolean
        )
        return if (data.get(UseCaseConstants.PRODUCT_UPDATE) as Boolean)
            cartRepository.updateProduct(productDB)
        else
            cartRepository.insertProduct(productDB)
    }
}