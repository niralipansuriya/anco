package com.app.ancoturf.data.product

import android.text.TextUtils
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.account.remote.entity.request.RegisterRequest
import com.app.ancoturf.data.common.LogStatus
import com.app.ancoturf.data.common.RequestConstants
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.data.product.remote.ProductService
import com.app.ancoturf.data.product.remote.entity.request.AddReviewRequest
import com.app.ancoturf.data.product.remote.entity.response.*
import com.app.ancoturf.domain.product.ProductRepository
import com.app.ancoturf.extension.printLog
import com.app.ancoturf.utils.Utility
import com.google.gson.JsonObject
import io.reactivex.Single
import java.util.*
import javax.inject.Inject

class ProductDataRepository @Inject constructor() : ProductRepository, CommonService<ProductService>() {


    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = ProductService::class.java

    override fun getProductCategories(): Single<BaseResponse<ArrayList<ProductCategoryData>>> {
        return networkService.getProductCategories().map {
            it
        }
    }

    override fun getAllProducts(page : String): Single<BaseResponse<ProductsResponse>> {
        return networkService.getAllProducts(page).map {
            it
        }
    }

    override fun getProductsByCategory(categoryIds: String , tagIds : String , minPrice : String , maxPrice : String , search : String ,sortBy : String,page : String): Single<BaseResponse<ProductsResponse>> {
        return networkService.getProductsByCategory(categoryIds , tagIds , minPrice , maxPrice , search , sortBy,page).map {
            it
        }
    }

    override fun getProductDetail(productId: Int): Single<BaseResponse<ProductDetailResponse>> {
        return networkService.getProductDetail(productId).map {
            it
        }
    }

    override fun getRelatedProducts(productId: Int , perPage: Int): Single<BaseResponse<RelatedProductsResponse>> {
        return networkService.getRelatedProducts(productId , perPage).map {
            it
        }
    }

    override fun addReview(
        productId: Int,
        review_text: String,
        rating: Int,
        user_id: Int,
        email : String,
        name: String
    ): Single<BaseResponse<AddReviewResponse>> {
        val addReviewRequest = AddReviewRequest(productId = productId , reviewText = review_text , rating = rating , userId = user_id , email = email , name = name)
        val jsonObject : JsonObject = createJsonObject(addReviewRequest)
        return networkService.addReview(jsonObject).map {
            it
        }
    }

    override fun getProductFullname(productSortForm: String): Single<BaseResponse<ArrayList<ProductDetailResponse>>> {
        return networkService.getProductFullName(productSortName =  productSortForm).map {
            it
        }
    }

    private fun createJsonObject(addReviewRequest: AddReviewRequest): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("product_id" , addReviewRequest.productId)
        jsonObject.addProperty("review_text" , addReviewRequest.reviewText)
        jsonObject.addProperty("rating" , addReviewRequest.rating)
        jsonObject.addProperty("name" , addReviewRequest.name)
        if(!Utility.isValueNull(addReviewRequest.email)) {
            jsonObject.addProperty("email", addReviewRequest.email)
        }
        if(addReviewRequest.userId != 0) {
            jsonObject.addProperty("user_id", addReviewRequest.userId)
        }
        return jsonObject
    }
}