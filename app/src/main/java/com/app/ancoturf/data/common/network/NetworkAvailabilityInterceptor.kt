package com.app.ancoturf.data.common.network

import android.net.ConnectivityManager
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.util.hasNetwork
import com.app.ancoturf.data.util.NoNetworkException
import okhttp3.Interceptor
import okhttp3.Response

class NetworkAvailabilityInterceptor(
    private val cm: ConnectivityManager,
    private val noNetworkMessage: String,
    private val sharedPrefs: SharedPrefs
) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!hasNetwork(cm)) throw NoNetworkException(noNetworkMessage)
        val request = chain.request()
        val requestWithUserAgent = request.newBuilder()
//            .header("Authorization", sharedPrefs.accessToken)
            .build()

        return chain.proceed(requestWithUserAgent)
    }
}