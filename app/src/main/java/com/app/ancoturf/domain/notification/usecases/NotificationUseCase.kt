package com.app.ancoturf.domain.notification.usecases

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.notification.remote.entity.response.NotificationDataResponse
import com.app.ancoturf.domain.BaseUseCase
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.domain.notification.NotificationRepository
import io.reactivex.Single
import javax.inject.Inject

class NotificationUseCase @Inject constructor(private val notificationRepository: NotificationRepository) :
    BaseUseCase<BaseResponse<NotificationDataResponse>>(){
    override fun buildSingle(data: Map<String, Any?>?): Single<BaseResponse<NotificationDataResponse>> {
        return notificationRepository.getAllNotification(page = data?.get(UseCaseConstants.PER_PAGE) as String)
    }
}