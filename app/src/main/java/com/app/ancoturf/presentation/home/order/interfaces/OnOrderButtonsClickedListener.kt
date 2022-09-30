package com.app.ancoturf.presentation.home.order.interfaces

import com.app.ancoturf.data.order.remote.entity.response.OrderDetailResponse
import com.app.ancoturf.data.quote.remote.entity.response.QuoteDetailsResponse

interface OnOrderButtonsClickedListener
{
    fun onClickOfReorder(orderDetailResponse: OrderDetailResponse)
}