package com.app.ancoturf.presentation.cart.interfaces

import com.app.ancoturf.data.cart.remote.entity.PaymentMethodsResponse

interface OnPaymentMethodSelectedListener {

    fun onPaymentMethodSelected(paymentMethod: PaymentMethodsResponse.PaymentMethod)

}