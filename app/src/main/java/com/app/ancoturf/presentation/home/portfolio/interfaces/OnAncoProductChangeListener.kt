package com.app.ancoturf.presentation.home.portfolio.interfaces

import com.app.ancoturf.data.portfolio.remote.entity.PortfolioDetailResponse

interface OnAncoProductChangeListener {

    fun onItemDelete(product: PortfolioDetailResponse.Product)

    fun onQuntityChange(product: PortfolioDetailResponse.Product)
}