package com.app.ancoturf.presentation.home.quote.interfaces

import com.app.ancoturf.data.portfolio.remote.entity.PortfolioDetailResponse
import com.app.ancoturf.data.quote.remote.entity.QuoteProducts
import com.app.ancoturf.data.quote.remote.entity.response.QuoteDetailsResponse
import com.app.ancoturf.data.quote.remote.entity.response.QuotesDataResponse

interface OnQuoteButtonsClickedListener {

    fun onClickOfResend(quote: QuoteDetailsResponse)

    fun onClickOfDuplicate(quote: QuoteDetailsResponse)
}