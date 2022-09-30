package com.app.ancoturf.presentation.home.interfaces

import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse

interface OnProductAddedToCart {

    fun OnProductAddedToCart(product: ProductDetailResponse)
}