package com.app.ancoturf.data.portfolio

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.data.portfolio.remote.PortfolioService
import com.app.ancoturf.data.portfolio.remote.entity.*
import com.app.ancoturf.domain.portfolio.PortfolioRepository
import com.google.gson.Gson
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

 class PortfolioDataRepository @Inject constructor() : PortfolioRepository, CommonService<PortfolioService>() {

    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = PortfolioService::class.java

    override fun getPortfolios(): Single<BaseResponse<PortfolioResponse>> {
        return networkService.getPortfolios().map {
            it
        }
    }

    override fun getPortfolioRepositoryDetail(portfolioId: Int): Single<BaseResponse<PortfolioDetailResponse>> {
        return networkService.getPortfolioDetail(portfolioId).map {
            it
        }
    }


    override fun deletePortfolio(portfolioId: Int): Single<BaseResponse<PortfolioDetailResponse>> {
        return networkService.deletePortfolio(portfolioId).map { it }
    }
    override fun addPortfolio(
        filePaths: ArrayList<String>,
        projectName: String,
        city: String,
        budget: Float,
        projectDescription: String,
        address: String,
        featuredImageIndex: Int,
        products: ArrayList<QuoteAncoProductRequest>,
        customProducts: ArrayList<QuoteNonAncoProductRequest>
    ): Single<BaseResponse<AddEditPortfolioResponse>> {
        // create list of file parts
        val files = arrayOfNulls<MultipartBody.Part>(filePaths.size)
        for (i in 0..(filePaths.size - 1)) {
            val imageFile = File(filePaths[i])
            val requestImageFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            files[i] = (MultipartBody.Part.createFormData("portfolio_images[]", imageFile.getName(), requestImageFile))
        }

        val projectNameRequestBody = RequestBody.create(MediaType.parse("text/plain"), projectName)
        val cityRequestBody = RequestBody.create(MediaType.parse("text/plain"), city)
        val projectDescriptionRequestBody = RequestBody.create(MediaType.parse("text/plain"), projectDescription)
        val addressRequestBody = RequestBody.create(MediaType.parse("text/plain"), address)
        val featureImageIndexRequestBody = RequestBody.create(MediaType.parse("text/plain"), "$featuredImageIndex")

        val productsRequestBody = RequestBody.create(MediaType.parse("text/plain"), Gson().toJson(products))

        val customProductsRequestBody = RequestBody.create(MediaType.parse("text/plain"), Gson().toJson(customProducts))

        return networkService.addPortfolio(
            files = files,
            projectName = projectNameRequestBody,
            city = cityRequestBody,
            budget = budget,
            projectDescription = projectDescriptionRequestBody,
            address = addressRequestBody,
            featuredImageIndex = featureImageIndexRequestBody,
            products = productsRequestBody,
            newCustomProducts = customProductsRequestBody
        ).map {
            it
        }
    }

    override fun editPortfolio(
        portfolioId: Int,
        filePaths: ArrayList<String>,
        projectName: String,
        city: String,
        budget: Float,
        projectDescription: String,
        address: String,
        featuredImageIndex: Int,
        products: ArrayList<QuoteAncoProductRequest>,
        customProducts: ArrayList<QuoteNonAncoProductRequest>,
        updatedcustomProducts: ArrayList<QuoteNonAncoProductRequest>,
        deletedCustomProductIds: ArrayList<Int>,
        deletedImageIds: ArrayList<Int>,
        deletedProductIds: ArrayList<Int>,
        featuredImageId: Int,
        imageIds: ArrayList<Int>,
        imageSequence:String
    ): Single<BaseResponse<AddEditPortfolioResponse>> {
        // create list of file parts
        val files = arrayOfNulls<MultipartBody.Part>(filePaths.size)
        for (i in 0..(filePaths.size - 1)) {
            val imageFile = File(filePaths[i])
            val requestImageFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            files[i] = (MultipartBody.Part.createFormData("portfolio_images[]", imageFile.getName(), requestImageFile))
        }

        val deletedCustomProducts = arrayOfNulls<Int>(deletedCustomProductIds.size)
        for (i in 0..(deletedCustomProductIds.size - 1)) {
            deletedCustomProducts[i] = deletedCustomProductIds[i]
        }

        val deletedProducts = arrayOfNulls<Int>(deletedProductIds.size)
        for (i in 0..(deletedProductIds.size - 1)) {
            deletedProducts[i] = deletedProductIds[i]
        }

        val deletedImages = arrayOfNulls<Int>(deletedImageIds.size)
        for (i in 0..(deletedImageIds.size - 1)) {
            deletedImages[i] = deletedImageIds[i]
        }

        val images = arrayOfNulls<Int>(imageIds.size)
        for (i in 0..(imageIds.size - 1)) {
            images[i] = imageIds[i]
        }

        val projectNameRequestBody = RequestBody.create(MediaType.parse("text/plain"), projectName)
        val cityRequestBody = RequestBody.create(MediaType.parse("text/plain"), city)
        val projectDescriptionRequestBody = RequestBody.create(MediaType.parse("text/plain"), projectDescription)
        val addressRequestBody = RequestBody.create(MediaType.parse("text/plain"), address)
        var featureImageIndexRequestBody: RequestBody
        if (featuredImageIndex != -1)
            featureImageIndexRequestBody = RequestBody.create(MediaType.parse("text/plain"), "$featuredImageIndex")
        else
            featureImageIndexRequestBody = RequestBody.create(MediaType.parse("text/plain"), "")

        var featureImageIdRequestBody: RequestBody
        if (featuredImageId != 0)
            featureImageIdRequestBody = RequestBody.create(MediaType.parse("text/plain"), "$featuredImageId")
        else
            featureImageIdRequestBody = RequestBody.create(MediaType.parse("text/plain"), "")

        var productsString = Gson().toJson(products)
        val productsRequestBody = RequestBody.create(MediaType.parse("text/plain"), productsString)

        val customProductsRequestBody = RequestBody.create(MediaType.parse("text/plain"), Gson().toJson(customProducts))
        val updatedCustomProductsRequestBody = RequestBody.create(MediaType.parse("text/plain"), Gson().toJson(updatedcustomProducts))

        val imageSequenceRequstBody = RequestBody.create(MediaType.parse("text/plain"),imageSequence)

        return networkService.editPortfolio(
            portfolioId = portfolioId,
            files = files,
            projectName = projectNameRequestBody,
            city = cityRequestBody,
            budget = budget,
            projectDescription = projectDescriptionRequestBody,
            address = addressRequestBody,
            featuredImageIndex = featureImageIndexRequestBody,
            products = productsRequestBody,
            newCustomProducts = customProductsRequestBody,
            deletedCustomProductIds = deletedCustomProducts,
            deletedImageIds = deletedImages,
            deletedProductIds = deletedProducts,
            featuredImageId = featureImageIdRequestBody,
            imageIds = images,
            updatedCustomProducts = updatedCustomProductsRequestBody,
            imageSequences = imageSequenceRequstBody
        ).map {
            it
        }
    }


}