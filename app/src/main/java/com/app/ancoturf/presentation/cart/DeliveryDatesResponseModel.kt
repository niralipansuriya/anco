package com.app.ancoturf.presentation.cart

data class DeliveryDatesResponseModel(
    val disable_date: List<String>,
    val enable_date: List<String>,
    val max_date: String,
    val min_date: String
)