package com.app.ancoturf.presentation.home.quote.interfaces

import com.app.ancoturf.data.portfolio.remote.entity.PortfolioDetailResponse
import com.app.ancoturf.data.quote.remote.entity.QuoteProducts

interface OnProductChangeListener {

    fun onItemDelete(product: QuoteProducts)

    fun onQuntityChange(product: QuoteProducts)
}