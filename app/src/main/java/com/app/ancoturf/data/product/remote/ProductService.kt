package com.app.ancoturf.data.product.remote

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.product.remote.entity.response.*
import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.*

interface ProductService {

    @GET("api/product_categories")
    fun getProductCategories():Single<BaseResponse<ArrayList<ProductCategoryData>>>

    @GET("api/products")
    fun getAllProducts(@Query("page") page : String):Single<BaseResponse<ProductsResponse>>


    @FormUrlEncoded
    @POST("api/geProductFullname")
    fun getProductFullName(@Field("product_name") productSortName : String):Single<BaseResponse<ArrayList<ProductDetailResponse>>>

    @GET("api/products")
    fun getProductsByCategory(@Query("product_category_id") categoryIds : String,
                              @Query("product_tag_id") tagIds : String ,
                              @Query("price_min") minPrice : String ,
                              @Query("price_max") maxPrice : String ,
                              @Query("search") search : String ,
                              @Query("sort_by") sortBy : String,
                              @Query("page") page : String):Single<BaseResponse<ProductsResponse>>

    @GET("api/product/{productId}")
    fun getProductDetail(@Path("productId") productId : Int):Single<BaseResponse<ProductDetailResponse>>

    @GET("api/related_products/{productId}")
    fun getRelatedProducts(@Path("productId") productId : Int , @Query("per_page") perPage : Int):Single<BaseResponse<RelatedProductsResponse>>

    @POST("api/product/add_review")
    fun addReview(@Body addReviewRequest: JsonObject):Single<BaseResponse<AddReviewResponse>>

}