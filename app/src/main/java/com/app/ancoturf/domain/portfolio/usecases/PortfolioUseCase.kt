package com.app.ancoturf.domain.portfolio.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.portfolio.remote.entity.PortfolioResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.portfolio.PortfolioRepository
import io.reactivex.Single
import javax.inject.Inject

class PortfolioUseCase @Inject constructor(private val portfolioRepository: PortfolioRepository) :
    BaseUseCase<BaseResponse<PortfolioResponse>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<PortfolioResponse>> {
            return portfolioRepository.getPortfolios()
    }
}