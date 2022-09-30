package com.app.ancoturf.domain.portfolio.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.portfolio.remote.entity.PortfolioDetailResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.portfolio.PortfolioRepository
import io.reactivex.Single
import javax.inject.Inject

class DeletePortfolioUseCase @Inject constructor(private val portfolioRepository : PortfolioRepository) :
BaseUseCase<BaseResponse<PortfolioDetailResponse>>(){
    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<PortfolioDetailResponse>> {
        return portfolioRepository.deletePortfolio(portfolioId = data?.get(UseCaseConstants.PORTFOLIO_ID) as Int)
    }
}