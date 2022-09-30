package com.app.ancoturf.data.cart.remote.entity


import com.google.gson.annotations.SerializedName

data class CountryResponse(
    @SerializedName("country_code")
    var countryCode: String = "",
    @SerializedName("country_name")
    var countryName: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("states")
    var states: List<State> = listOf(),
    @SerializedName("status")
    var status: String = ""
) {
    data class State(
        @SerializedName("country_id")
        var countryId: String = "",
        @SerializedName("id")
        var id: Int = 0,
        @SerializedName("state_code")
        var stateCode: String = "",
        @SerializedName("state_name")
        var stateName: String = "",
        @SerializedName("status")
        var status: String = ""
    )
}