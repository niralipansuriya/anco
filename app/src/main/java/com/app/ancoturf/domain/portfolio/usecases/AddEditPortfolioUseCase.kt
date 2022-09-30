package com.app.ancoturf.domain.portfolio.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.portfolio.remote.entity.AddEditPortfolioResponse
import com.app.ancoturf.data.portfolio.remote.entity.QuoteAncoProductRequest
import com.app.ancoturf.data.portfolio.remote.entity.QuoteNonAncoProductRequest
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.portfolio.PortfolioRepository
import com.google.gson.JsonArray
import io.reactivex.Single
import javax.inject.Inject

class AddEditPortfolioUseCase @Inject constructor(private val portfolioRepository: PortfolioRepository) :
    BaseUseCase<BaseResponse<AddEditPortfolioResponse>>() {

    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<AddEditPortfolioResponse>> {
        if(data?.get(UseCaseConstants.PORTFOLIO_ID) == 0)
            return portfolioRepository.addPortfolio(address = data?.get(UseCaseConstants.ADDRESS) as String,
                budget = data?.get(UseCaseConstants.BUDGET) as Float,
                city = data?.get(UseCaseConstants.CITY) as String,
                projectName = data?.get(UseCaseConstants.PROJECT_NAME) as String,
                projectDescription = data?.get(UseCaseConstants.PROJECT_DESCRIPTION) as String,
                products = data?.get(UseCaseConstants.PRODUCTS) as ArrayList<QuoteAncoProductRequest>,
                customProducts = data?.get(UseCaseConstants.CUSTOM_PRODUCTS) as ArrayList<QuoteNonAncoProductRequest>,
                featuredImageIndex = data?.get(UseCaseConstants.FEATURED_IMAGE_INDEX) as Int,
                filePaths = data?.get(UseCaseConstants.PORTFOLIO_IMAGES) as ArrayList<String>)
        else
            return portfolioRepository.editPortfolio(portfolioId = data?.get(UseCaseConstants.PORTFOLIO_ID) as Int,
                address = data?.get(UseCaseConstants.ADDRESS) as String,
                budget = data?.get(UseCaseConstants.BUDGET) as Float,
                city = data?.get(UseCaseConstants.CITY) as String,
                projectName = data?.get(UseCaseConstants.PROJECT_NAME) as String,
                projectDescription = data?.get(UseCaseConstants.PROJECT_DESCRIPTION) as String,
                products = data?.get(UseCaseConstants.PRODUCTS) as ArrayList<QuoteAncoProductRequest>,
                customProducts = data?.get(UseCaseConstants.CUSTOM_PRODUCTS) as ArrayList<QuoteNonAncoProductRequest>,
                featuredImageIndex = data?.get(UseCaseConstants.FEATURED_IMAGE_INDEX) as Int,
                filePaths = data?.get(UseCaseConstants.PORTFOLIO_IMAGES) as ArrayList<String>,
                imageIds = data?.get(UseCaseConstants.IMAGE_IDS) as ArrayList<Int>,
                featuredImageId = data?.get(UseCaseConstants.FEATURED_IMAGE_ID) as Int ,
                deletedProductIds = data?.get(UseCaseConstants.DELETED_PRODUCT_IDS) as ArrayList<Int> ,
                deletedImageIds = data?.get(UseCaseConstants.DELETED_IMAGE_IDS) as ArrayList<Int>,
                deletedCustomProductIds = data?.get(UseCaseConstants.DELETED_CUSTOM_PRODUCT_IDS) as ArrayList<Int>,
                updatedcustomProducts = data?.get(UseCaseConstants.UPDATED_CUSTOM_PRODUCTS) as ArrayList<QuoteNonAncoProductRequest>,
                imageSequences = data?.get(UseCaseConstants.ORDERED_IMAGE_IDS) as String)

    }
}