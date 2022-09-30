package com.app.ancoturf.presentation.home.quote.interfaces

import com.app.ancoturf.data.product.remote.entity.response.ProductCategoryData

interface OnProductCategorySelected {

    fun onProductCategorySelected(productCategoryData: ProductCategoryData)

}