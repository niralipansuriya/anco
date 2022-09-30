package com.app.ancoturf.domain.notification

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.notification.remote.entity.response.NotificationDataResponse
import io.reactivex.Single

interface NotificationRepository {
    fun getAllNotification(page : String): Single<BaseResponse<NotificationDataResponse>>

}