package com.app.ancoturf.data.search

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.data.product.remote.entity.request.AddReviewRequest
import com.app.ancoturf.data.search.remote.SearchService
import com.app.ancoturf.data.search.remote.entity.request.AddProductRequest
import com.app.ancoturf.data.search.remote.entity.response.RequestProductResponse
import com.app.ancoturf.data.search.remote.entity.response.SearchProductResponse
import com.app.ancoturf.domain.search.SearchRepository
import com.app.ancoturf.utils.Utility
import com.google.gson.JsonObject
import io.reactivex.Single
import javax.inject.Inject

class SearchDataRepository @Inject constructor() : SearchRepository, CommonService<SearchService>() {

    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = SearchService::class.java

    override fun getSearchedProduct(lastSearchKey: String): Single<SearchProductResponse> {
        return networkService.getSearchedProduct(lastSearchKey).map {
            it
        }
    }

    override fun requestProduct(
        name: String,
        email: String,
        productDescription: String,
        productUrl: String
    ): Single<BaseResponse<RequestProductResponse>> {

        val requestProduct = AddProductRequest(
            name = name, email = email, productDescription = productDescription,
            productUrl = productUrl
        )
        val jsonObject : JsonObject = createJsonObject(requestProduct)


        return networkService.requestProduct(jsonObject).map {
            it
        }
    }

    private fun createJsonObject(addProductRequest: AddProductRequest): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("name" , addProductRequest.name)
        jsonObject.addProperty("email" , addProductRequest.email)
        jsonObject.addProperty("product_description" , addProductRequest.productDescription)
        jsonObject.addProperty("product_url " , addProductRequest.productUrl)

        return jsonObject
    }
}