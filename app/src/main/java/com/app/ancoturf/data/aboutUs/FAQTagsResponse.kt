package com.app.ancoturf.data.aboutUs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FAQTagsResponse
    (
    @Expose
    @SerializedName("message")
    val message: String? = "",

    @Expose
    @SerializedName("success")
    var success: Boolean = false,

    @SerializedName("data")
    var `data`: ArrayList<Data> = ArrayList()
    )
    {
        data class Data(
            var id: Int = 0,
            var display_name: String = "",
            var created_at: String = "",
            var updated_at: String = ""
        )
}