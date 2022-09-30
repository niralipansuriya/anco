package com.app.ancoturf.data.order.remote.entity.response

import com.google.gson.annotations.SerializedName

data class DriverModel(
    @SerializedName("allowed_to_place_order")
    var allowed_to_place_order: String,
    @SerializedName("device_token")
    var device_token: Any,
    @SerializedName("device_type")
    var device_type: Any,
    @SerializedName("email")
    var email: String,
    @SerializedName("first_name")
    var first_name: String,
    @SerializedName("id")
    var id: Int,
    @SerializedName("id_cms_privileges")
    var id_cms_privileges: String,
    @SerializedName("image_url")
    var image_url: String,
    @SerializedName("last_name")
    var last_name: String,
    @SerializedName("last_search_key")
    var last_search_key: Any,
    @SerializedName("phone_number")
    var phone_number: Any,
    @SerializedName("pivot")
    var pivot: Pivot,
    @SerializedName("status")
    var status: String
)

data class Pivot(
    @SerializedName("driver_id")
    var driver_id: String,
    @SerializedName("order_id")
    var order_id: String,
    @SerializedName("vehicle_id")
    var vehicle_id: String
)