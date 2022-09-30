package com.app.ancoturf.presentation.chooseingMyLawn

interface ISelectRecommendedProduct {
    fun onProductClick(position : Int)
    fun onNextPreviousClick(position : Int, isPrevious : Boolean)
}