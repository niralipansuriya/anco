package com.app.ancoturf.data.manageLawn.remote

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class   ManageLawnDetailResponse (
    @Expose
    @SerializedName("message")
    val message: String? = "",
    @Expose
    @SerializedName("success")
    var success: Boolean = false,
    @SerializedName("data")
    var data: ArrayList<Data> = ArrayList()

  /*  @SerializedName("id")
    @SerializedName("title")
    var title: String = "",
    @SerializedName("featured_image_url")
    var featureImageUrl: String = "",
    @SerializedName("my_lawn_details")
    var myLawnDetails: String = "",
    @SerializedName("descriptions")
    var descriptions: List<Description> = listOf()*/
) {
    data class Data(
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("title")
        var title: String = "",
        @SerializedName("featured_image_url")
        var featureImageUrl: String = "",
        @SerializedName("my_lawn_details")
        var myLawnDetails: String = "",
        @SerializedName("descriptions")
        var descriptions: List<Description> = listOf()
    )

    data class Description(
        @SerializedName("key")
        var key: String = "",
        @SerializedName("detail")
        var detail: String = "",
        var open: Boolean = false
    )
}