package com.app.ancoturf.data.common.network

import androidx.annotation.CallSuper
import com.app.ancoturf.BuildConfig
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Base64


abstract class AfterpayService<T> : BaseService<T>() {

    @Inject
    lateinit var networkAvailabilityInterceptor: NetworkAvailabilityInterceptor

    @Inject
    lateinit var errorInterceptor: ErrorInterceptor

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override val baseUrl = BuildConfig.AFTERPAY_API_URL

    @CallSuper
    override fun handleOkHttpBuilder(builder: OkHttpClient.Builder): OkHttpClient.Builder {

//        val accessToken: String = Constants.AFTERPAY_USER + ":" + Constants.AFTERPAY_USER_PASSWORD
        val accessToken: String = BuildConfig.AFTERPAY_USER + ":" + BuildConfig.AFTERPAY_USER_PASSWORD
        val data = accessToken.toByteArray()
        val base64 = "Basic "+(Base64.encodeToString(data, Base64.DEFAULT).replace("\n" , ""))

        return super.handleOkHttpBuilder(builder)
            .addInterceptor {
                val original: Request = it.request()
                val request: Request = original.newBuilder()
//                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", base64)
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