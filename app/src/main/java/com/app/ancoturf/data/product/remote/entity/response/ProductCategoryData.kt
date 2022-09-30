package com.app.ancoturf.data.product.remote.entity.response


import com.google.gson.annotations.SerializedName

data class ProductCategoryData(
    @SerializedName("display_name")
    var displayName: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("image_url")
    var imageUrl: String = "",
    @SerializedName("slug")
    var slug: String = "",
    @SerializedName("status")
    var status: String = "",

    var checked: Boolean = false
)