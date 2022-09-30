package com.app.ancoturf.presentation.cart.interfaces

import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.data.portfolio.remote.entity.PortfolioDetailResponse
import com.app.ancoturf.data.quote.remote.entity.QuoteProducts

interface OnCartProductChangeListener {

    fun onItemDelete(product: ProductDB)

    fun onQuantityChange(product: ProductDB)
}