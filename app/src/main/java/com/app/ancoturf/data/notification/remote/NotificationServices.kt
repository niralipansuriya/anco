package com.app.ancoturf.data.notification.remote

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.notification.remote.entity.response.NotificationDataResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface NotificationServices {
    @GET("api/notification")
    fun getNotifications(@Query("page") page: String): Single<BaseResponse<NotificationDataResponse>>

}