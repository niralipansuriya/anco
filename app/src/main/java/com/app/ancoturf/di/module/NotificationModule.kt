package com.app.ancoturf.di.module

import com.app.ancoturf.data.notification.NotificationDataRepository
import com.app.ancoturf.domain.notification.NotificationRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NotificationModule {

    @Singleton
    @Provides
    fun providerNotificationRepository(notificationDataRepository: NotificationDataRepository) : NotificationRepository =
        notificationDataRepository
}