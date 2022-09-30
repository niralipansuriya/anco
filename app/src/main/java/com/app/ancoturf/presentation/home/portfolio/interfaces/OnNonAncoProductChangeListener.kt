package com.app.ancoturf.presentation.home.portfolio.interfaces

import com.app.ancoturf.data.portfolio.remote.entity.PortfolioDetailResponse

interface OnNonAncoProductChangeListener {

    fun onItemDelete(product: PortfolioDetailResponse.CustomProduct)

    fun onClick(product: PortfolioDetailResponse.CustomProduct)
}