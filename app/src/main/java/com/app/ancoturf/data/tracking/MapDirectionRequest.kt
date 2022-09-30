package com.app.ancoturf.data.tracking

import com.google.gson.annotations.SerializedName

data class MapDirectionRequest(
    @SerializedName("origin")
    var origin:String="",
    @SerializedName("destination")
    var destination:String=""
)