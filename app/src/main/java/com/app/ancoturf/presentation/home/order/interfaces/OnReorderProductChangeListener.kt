package com.app.ancoturf.presentation.home.order.interfaces

import com.app.ancoturf.data.order.remote.entity.response.OrderDataResponse

interface OnReorderProductChangeListener {

    fun onItemChecked(product: OrderDataResponse.Product,position : Int)

    fun onQuantityChange(product: OrderDataResponse.Product)
}