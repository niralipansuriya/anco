package com.app.ancoturf.di.module

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.ancoturf.BuildConfig
import com.app.ancoturf.R
import com.app.ancoturf.data.cart.CartDataRepository
import com.app.ancoturf.data.cart.DeliveryDatesDataRepository
import com.app.ancoturf.data.common.local.AncoRoomDatabase
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.common.network.ErrorInterceptor
import com.app.ancoturf.data.common.network.NetworkAvailabilityInterceptor
import com.app.ancoturf.domain.cart.CartRepository
import com.app.ancoturf.domain.cart.DeliveryDatesRepository
import com.app.ancoturf.presentation.app.AncoTurfApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideApplication(app: AncoTurfApp): Application = app

    @Singleton
    @Provides
    fun provideConnectivityManager(app: Application): ConnectivityManager {
        return app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Singleton
    @Provides
    fun provideSharedPref(app: AncoTurfApp): SharedPrefs {
        return SharedPrefs(app)
    }

    @Singleton
    @Provides
    fun provideNetworkAvailabilityInterceptor(
        app: Application,
        connectivityManager: ConnectivityManager,
        sharedPrefs: SharedPrefs
    ) = NetworkAvailabilityInterceptor(
        connectivityManager,
        app.getString(R.string.error_no_network),
        sharedPrefs
    )

    @Singleton
    @Provides
    fun provideErrorInterceptor() = ErrorInterceptor()


    @Singleton
    @Provides
    fun provideRoomDatabase(app: AncoTurfApp): AncoRoomDatabase {
        return Room.databaseBuilder(app, AncoRoomDatabase::class.java, BuildConfig.DATABASE_NAME)
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideCartRepository(cartDataRepository: CartDataRepository): CartRepository =
        cartDataRepository

    @Singleton
    @Provides
    fun provideDeliveryDatesRepository(deliveryDatesDataRepository: DeliveryDatesDataRepository): DeliveryDatesRepository =
        deliveryDatesDataRepository

}