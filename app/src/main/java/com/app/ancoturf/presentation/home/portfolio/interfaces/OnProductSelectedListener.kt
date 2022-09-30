package com.app.ancoturf.presentation.home.portfolio.interfaces

import com.app.ancoturf.data.portfolio.remote.entity.PortfolioDetailResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse

interface OnProductSelectedListener {

    fun onProductSelectedListener(product: ProductsResponse.Data, selected: Boolean)
}