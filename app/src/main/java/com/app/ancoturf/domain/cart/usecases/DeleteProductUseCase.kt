package com.app.ancoturf.domain.cart.usecases

import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.cart.CartRepository
import com.app.ancoturf.domain.common.UseCaseConstants
import io.reactivex.Single
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(private val cartRepository: CartRepository) : BaseUseCase<Boolean>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<Boolean> {
        var productDB: ProductDB? = null
        if(data != null)
        productDB = data?.get(UseCaseConstants.PRODUCT) as ProductDB

        return if (productDB != null)
            cartRepository.deleteProduct(productDB)
        else
            cartRepository.deleteAllProducts()
    }
}