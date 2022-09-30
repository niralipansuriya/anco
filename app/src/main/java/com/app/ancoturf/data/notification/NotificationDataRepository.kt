package com.app.ancoturf.data.notification

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.data.notification.remote.NotificationServices
import com.app.ancoturf.data.notification.remote.entity.response.NotificationDataResponse
import com.app.ancoturf.data.order.remote.OrderService
import com.app.ancoturf.domain.notification.NotificationRepository
import io.reactivex.Single
import javax.inject.Inject

class NotificationDataRepository @Inject constructor() : NotificationRepository,
    CommonService<NotificationServices>() {
    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = NotificationServices::class.java

    override fun getAllNotification(page:String): Single<BaseResponse<NotificationDataResponse>> {
        return networkService.getNotifications(page).map {
            it
        }
    }
}