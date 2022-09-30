package com.app.ancoturf.domain.portfolio

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.portfolio.remote.entity.*
import com.google.gson.JsonArray
import io.reactivex.Single

interface PortfolioRepository {

    fun getPortfolios(): Single<BaseResponse<PortfolioResponse>>

    fun getPortfolioRepositoryDetail(portfolioId : Int): Single<BaseResponse<PortfolioDetailResponse>>

    fun addPortfolio(filePaths: ArrayList<String>, projectName: String, city: String, budget: Float, projectDescription: String, address: String, featuredImageIndex: Int, products: ArrayList<QuoteAncoProductRequest>, customProducts: ArrayList<QuoteNonAncoProductRequest>): Single<BaseResponse<AddEditPortfolioResponse>>

    fun editPortfolio(portfolioId: Int, filePaths: ArrayList<String>, projectName: String, city: String, budget: Float, projectDescription: String, address: String, featuredImageIndex: Int, products: ArrayList<QuoteAncoProductRequest>, customProducts: ArrayList<QuoteNonAncoProductRequest>, updatedcustomProducts: ArrayList<QuoteNonAncoProductRequest>, deletedCustomProductIds: ArrayList<Int>, deletedImageIds: ArrayList<Int>, deletedProductIds: ArrayList<Int>, featuredImageId: Int, imageIds: ArrayList<Int>, imageSequences:String ): Single<BaseResponse<AddEditPortfolioResponse>>

    fun deletePortfolio(portfolioId: Int) : Single<BaseResponse<PortfolioDetailResponse>>
}