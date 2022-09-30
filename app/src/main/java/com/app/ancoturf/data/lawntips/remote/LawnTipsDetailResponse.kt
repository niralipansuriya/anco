package com.app.ancoturf.data.lawntips.remote

import com.google.gson.annotations.SerializedName

class LawnTipsDetailResponse(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("title")
    var title: String = "",
    @SerializedName("featured_image_url")
    var featureImageUrl: String = "",
    var details: String = "",
    @SerializedName("descriptions")
    var descriptions: List<Description> = listOf()
){
    data class Description(
        @SerializedName("key")
        var key: String = "",
        @SerializedName("detail")
        var detail: String = "",
        var open: Boolean = false

    )
}