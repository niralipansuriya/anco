package com.app.ancoturf.data.common.network

import androidx.annotation.CallSuper
import com.app.ancoturf.BuildConfig
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.utils.Logger
import com.app.ancoturf.utils.Utility
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import javax.inject.Inject

abstract class CommonService<T> : BaseService<T>() {

    @Inject
    lateinit var networkAvailabilityInterceptor: NetworkAvailabilityInterceptor

    @Inject
    lateinit var errorInterceptor: ErrorInterceptor

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override val baseUrl = BuildConfig.API_BASE_URL

    @CallSuper
    override fun handleOkHttpBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {

        return super.handleOkHttpBuilder(builder)
            .addInterceptor {
                val original: Request = it.request()
                val request: Request = original.newBuilder()
//                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", sharedPrefs.accessToken)
                    .method(original.method(), original.body())
                    .build()

                return@addInterceptor it.proceed(request)
            }
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(networkAvailabilityInterceptor)
            .addInterceptor(errorInterceptor)
    }
}