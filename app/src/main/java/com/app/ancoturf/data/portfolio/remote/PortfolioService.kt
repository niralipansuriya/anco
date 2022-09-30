package com.app.ancoturf.data.portfolio.remote

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.portfolio.remote.entity.*
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface PortfolioService {

    @GET("api/portfolios")
    fun getPortfolios(): Single<BaseResponse<PortfolioResponse>>

    @GET("api/portfolio/{portfolioId}")
    fun getPortfolioDetail(@Path("portfolioId") portfolioId: Int): Single<BaseResponse<PortfolioDetailResponse>>

    @Multipart
    @POST("api/portfolio")
    fun addPortfolio(
        @Part files: Array<MultipartBody.Part?>,
        @Part("project_name") projectName: RequestBody,
        @Part("city") city: RequestBody,
        @Part("budget") budget: Float,
        @Part("project_description") projectDescription: RequestBody,
        @Part("address") address: RequestBody,
        @Part("featured_image_index") featuredImageIndex: RequestBody,
        @Part("products") products: RequestBody,
        @Part("new_custom_products") newCustomProducts: RequestBody
    ): Single<BaseResponse<AddEditPortfolioResponse>>

    @Multipart
    @POST("api/portfolio/{portfolioId}")
    fun editPortfolio(
        @Path("portfolioId") portfolioId: Int,
        @Part files: Array<MultipartBody.Part?>,
        @Part("portfolio_image_ids[]") imageIds: Array<Int?>,
        @Part("featured_image_index") featuredImageIndex: RequestBody,
        @Part("project_name") projectName: RequestBody,
        @Part("featured_image_id") featuredImageId: RequestBody,
        @Part("city") city: RequestBody,
        @Part("budget") budget: Float,
        @Part("project_description") projectDescription: RequestBody,
        @Part("address") address: RequestBody,
        @Part("products") products: RequestBody,
        @Part("deleted_portfolio_image_ids[]") deletedImageIds: Array<Int?>,
        @Part("deleted_product_ids[]") deletedProductIds: Array<Int?>,
        @Part("deleted_custom_product_ids[]") deletedCustomProductIds: Array<Int?>,
        @Part("new_custom_products") newCustomProducts: RequestBody,
        @Part("updated_custom_products") updatedCustomProducts: RequestBody,
        @Part("image_sequence") imageSequences:RequestBody
    ): Single<BaseResponse<AddEditPortfolioResponse>>

    @FormUrlEncoded
    @POST("api/deletePortfolio")
    fun deletePortfolio(
        @Field("id") portfolioId: Int
    ): Single<BaseResponse<PortfolioDetailResponse>>
}