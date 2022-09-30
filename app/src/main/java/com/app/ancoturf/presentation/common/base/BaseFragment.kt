package com.app.ancoturf.presentation.common.base

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.app.ancoturf.R
import com.app.ancoturf.data.common.ApiCallback
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.showAlert
import com.app.ancoturf.presentation.notification.NotificationFragment
import com.google.gson.Gson
import dagger.android.support.DaggerFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class BaseFragment : DaggerFragment() {

    public val ADD_CARD_REQUEST_CODE: Int = 101

    @LayoutRes
    protected abstract fun getContentResource(): Int

    abstract fun viewModelSetup()

    abstract fun viewSetup()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getContentResource().takeIf { it != 0 }?.let {
            return inflater.inflate(getContentResource(), container, false)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    public fun openNotificationFragment() {
        (requireActivity() as AppCompatActivity).pushFragment(
            NotificationFragment(),
            true,
            true,
            false,
            R.id.flContainerHome
        )
    }

    /*public fun openNotificationFragment(ivBell: AppCompatImageView, activity : Activity) {
        ivBell.setOnClickListener {
            View.OnClickListener {
                (activity as AppCompatActivity).pushFragment(
                    NotificationFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
        }
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelSetup()
        viewSetup()
    }

    fun dismiss() {
        activity?.supportFragmentManager?.popBackStackImmediate()
    }


    fun callApi(type: String, call: Call<String>, apiCallback: ApiCallback) {
        try {
            call.enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    apiCallback.onError(type, t.message.toString())
//                    (requireActivity() as AppCompatActivity).( t.message.toString())
                }
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.code() == 200 || response.code() == 201) {
                        Log.e("BaseFragment", "response : " + response.body().toString())
//                        (requireActivity() as AppCompatActivity).showAlert(response.body().toString())
                        apiCallback.onSuccess(type, response.body().toString())
                    } else {
//                        (requireActivity() as AppCompatActivity).showAlert(response.body().toString())
                        apiCallback.onError(type, response.body().toString())
                    }
                }
            })
        } catch (e: Exception) {
            apiCallback.onError(type, getString(R.string.error))
            (requireActivity() as AppCompatActivity).showAlert(e.toString())
        }
    }

    fun callApiMapDirectionApi(type: String, call: Call<Object>, apiCallback: ApiCallback) {
        try {
            call.enqueue(object : Callback<Object> {
                override fun onFailure(call: Call<Object>, t: Throwable) {
                    apiCallback.onError(type, t.message.toString())
                }
                override fun onResponse(call: Call<Object>, response: Response<Object>) {
                    if (response.code() == 200 || response.code() == 201) {
                        Log.e("BaseFragment", "response : " + response.body().toString())
                        apiCallback.onSuccess(
                            type,
                            Gson().toJson(response.body())
                        ) //JSONObject(Gson().toJson(response.body()).toString())
                    } else {
                        apiCallback.onError(type, response.body().toString())
                    }
                }
            })
        } catch (e: Exception) {
            apiCallback.onError(type, getString(R.string.error))
        }
    }

    fun createShareProductUri(productId: String?): Uri {
        val builder = Uri.Builder()
        builder.scheme(getString(R.string.config_scheme)) // "https"
            .authority(getString(R.string.config_host)) // "ancoapp.page.link"
            .appendPath(getString(R.string.config_path_products)) // "products"  https://ancoapp.page.link/products
            .appendQueryParameter(Constants.PRODUCT_ID_PARAM, productId)
//            .appendQueryParameter(Constants.QUOTE_STATUS_PARAM, status)

        return builder.build()
    }


    val SINGLE_USE_TOKEN_API: String = "single_use_token_api"
    val MERCHANT_CALL_API: String = "merchant_call_api"
    val CREATE_CUSTOMER_API: String = "create_custome_api"
    val TRANSACTION_API: String = "transaction_api"

    val INIT_XML_REQUEST = "init_xml_request"
    val GET_DIRECTION_LATLNG = "get_direction_latlng"
    val GOOGLE_MAP_DIRECTION_API = "google_map_direction_api"
    val LOGOFF_NAVMAN_API = "logoff_navman_api"
}