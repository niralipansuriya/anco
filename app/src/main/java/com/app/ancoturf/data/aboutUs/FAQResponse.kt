package com.app.ancoturf.data.aboutUs

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FAQResponse
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
        var id: Int = 0,
        var que: String = "",
        var ans: String = "",
        var isOpen: Boolean = false,
        var details: ArrayList<details> = ArrayList()
    )

    data class details(

        @SerializedName("faq_tag_id")
        var faq_tag_id: String = "",
        @SerializedName("display_name")
        var display_name: String = ""
    )


}
