package com.app.ancoturf.data.common.network

import androidx.annotation.CallSuper
import com.app.ancoturf.BuildConfig
import com.app.ancoturf.presentation.payment.westpac.WestpacPayment
import com.google.gson.GsonBuilder
import com.kotlindemo.retrofitclient.ToStringConverter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

abstract class BaseService<T> {

    private var _networkService: T? = null

    protected abstract val baseUrl: String

    protected abstract val baseClass: Class<T>

    val networkService: T
        get() {
            var service = _networkService
            return if (service == null) {
                service = initNetworkService()
                _networkService = service
                return service
            } else service
        }

    private fun initNetworkService(): T {
        return handleRetrofitBuilder(Retrofit.Builder().baseUrl(baseUrl))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(handleGson(GsonBuilder()).create()))
            .client(getOkHttpClient())
            .build()
            .create(baseClass)
    }

    private fun getOkHttpClient(): OkHttpClient {
        return handleOkHttpBuilder(OkHttpClient.Builder())
            .addInterceptor(
                HttpLoggingInterceptor().setLevel(
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                )
            )
            .build()
    }

    @CallSuper
    protected open fun handleRetrofitBuilder(builder: Retrofit.Builder) = builder

    @CallSuper
    protected open fun handleOkHttpBuilder(builder: OkHttpClient.Builder) = builder

    @CallSuper
    protected open fun handleGson(builder: GsonBuilder) = builder

    companion object {
        var retrofitWestpackInstance: Retrofit? = null
        fun retrofitWestpackInstance(): Retrofit? {
            if (retrofitWestpackInstance == null)
                retrofitWestpackInstance =
                    Retrofit.Builder().baseUrl(WestpacPayment.westPackBaseUrl)
                        .addConverterFactory(ToStringConverter())
                        .build()

            return retrofitWestpackInstance
        }

        var retrofitNowmanInstance: Retrofit? = null
        private var nowManBaseUrl =
            "https://onlineavl2api-au.navmanwireless.com/onlineavl/api/V2.6/"

        fun retrofitNowmanInstance(): Retrofit? {
            if (retrofitNowmanInstance == null)
                retrofitNowmanInstance = Retrofit.Builder().baseUrl(nowManBaseUrl)
                    .addConverterFactory(ToStringConverter()).build()
            return retrofitNowmanInstance
        }

        var retrofitGoogleMapDirection: Retrofit? = null
        fun retrofitGoogleMapDirectionInstance(): Retrofit? {
            if (retrofitGoogleMapDirection == null) {
                retrofitGoogleMapDirection =
                    Retrofit.Builder().baseUrl(BuildConfig.API_BASE_URL)
                        .client(
                            OkHttpClient.Builder()
                                .addInterceptor(
                                    HttpLoggingInterceptor().setLevel(
                                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                                    )
                                )
                                .build()
                        ).addConverterFactory(GsonConverterFactory.create()).build()
            }
            return retrofitGoogleMapDirection
        }
    }
}
