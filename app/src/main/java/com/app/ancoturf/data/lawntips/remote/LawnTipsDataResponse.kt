package com.app.ancoturf.data.lawntips.remote

import com.google.gson.annotations.SerializedName

class LawnTipsDataResponse
    (
    @SerializedName("data")
    var `data`: ArrayList<Data> = ArrayList(),
    @SerializedName("current_page")
    var currentPage: Int = 0,
    @SerializedName("first_page_url")
    var firstPageUrl: String = "",
    @SerializedName("from")
    var from: Int = 0,
    @SerializedName("next_page_url")
    var nextPageUrl: Any? = Any(),
    @SerializedName("path")
    var path: String = "",
    @SerializedName("per_page")
    var perPage: Int = 0,
    @SerializedName("prev_page_url")
    var prevPageUrl: Any? = Any(),
    @SerializedName("to")
    var to: Int = 0
) {
    data class Data(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("title")
        var title: String = "",
        @SerializedName("featured_image_url")
        var featureImageUrl: String = ""
    )
}