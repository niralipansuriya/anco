package com.app.ancoturf.data.manageLawn.remote

import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ManageLawnDataResponse
    (
    @Expose
    @SerializedName("message")
    val message: String? = "",
    @Expose
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("data")
    var `data`: ArrayList<Data> = ArrayList()
) {
    data class Data(
        var cat_id: Int = 0,
        var cat_name: String = "",
        var image_url: String = "",
//        var cat_description: String = "",
        var details: ArrayList<details> = ArrayList()
    )
    data class details(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("title")
        var title: String = "",
        var my_lawn_details: String = "",
        @SerializedName("featured_image_url")
        var featureImageUrl: String = ""
    )
}