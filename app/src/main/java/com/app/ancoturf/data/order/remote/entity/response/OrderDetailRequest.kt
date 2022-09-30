package com.app.ancoturf.data.order.remote.entity.response

import com.google.gson.annotations.SerializedName

class OrderDetailRequest (@SerializedName("reference_number")
                          var referenceNumber: String = "")