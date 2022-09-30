package com.app.ancoturf.data.search.remote.entity.request


import com.google.gson.annotations.SerializedName

data class AddProductRequest(
    @SerializedName("name")
    var name: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("product_description")
    var productDescription: String = "",
    @SerializedName("product_url ")
    var productUrl :String = ""
)