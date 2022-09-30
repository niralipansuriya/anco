package com.app.ancoturf.domain.product

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.product.remote.entity.response.*
import io.reactivex.Single

interface ProductRepository {

    fun getProductCategories(): Single<BaseResponse<ArrayList<ProductCategoryData>>>

    fun getAllProducts(page : String): Single<BaseResponse<ProductsResponse>>

    fun getProductsByCategory(categoryIds :String , tagIds : String , minPrice : String , maxPrice : String , search : String ,sortBy : String,page : String): Single<BaseResponse<ProductsResponse>>

    fun getProductDetail(productId :Int): Single<BaseResponse<ProductDetailResponse>>

    fun getRelatedProducts(productId :Int , perPage: Int): Single<BaseResponse<RelatedProductsResponse>>

    fun addReview(productId :Int , review_text : String , rating : Int , user_id : Int , email : String , name : String): Single<BaseResponse<AddReviewResponse>>

    fun getProductFullname(productSortForm: String):Single<BaseResponse<ArrayList<ProductDetailResponse>>>

}