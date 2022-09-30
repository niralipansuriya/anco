package com.app.ancoturf.data.common.network

import android.accounts.NetworkErrorException
import android.text.TextUtils
import android.util.Log
import com.app.ancoturf.BuildConfig
import com.app.ancoturf.data.common.entity.ErrorResponse
import com.app.ancoturf.data.common.exceptions.AncoHttpException
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody

class ErrorInterceptor() : Interceptor {

    var gson = Gson()

    override fun intercept(chain: Interceptor.Chain?): Response {
        val request = chain?.request()


        val response = request?.let { chain.proceed(it) }
                ?: throw NetworkErrorException("Null response")

        return if (response.isSuccessful) {
            response
        } else {
            val body = response.body()?.string()?.let {
                //Handle error structure for http request
                var errorBody: ErrorResponse
                try {
                    errorBody = gson.fromJson(it, ErrorResponse::class.java)
                    errorBody.message = errorBody.message
                    errorBody.status_code = response.code().toString()
//                    errorBody.data?.forEach { data ->
//                        errorBody.message = when {
//                            data == null -> "Null response"
//                            TextUtils.isEmpty(errorBody.message) -> data
//                            else -> "${errorBody.message}, $data"
//                        }
//                    }
                } catch (exception: JsonSyntaxException) {
                    errorBody = ErrorResponse()
                    errorBody.status_code = response.code().toString()
                    errorBody.message = response.message()
                }
                if (BuildConfig.DEBUG) {
                    Log.d("OkHttp", gson.toJson(errorBody))
                }
                throw createAncoException(response.code().toString(), errorBody)
            } ?: ""
            response.newBuilder()
                    .body(ResponseBody.create(MediaType.parse("application/json"), body))
                    .build()
        }
    }

    private fun createAncoException(code: String, errorBody: ErrorResponse): AncoHttpException {
        return when (code) {
            AncoHttpException.CODE_BAD_REQUEST -> AncoHttpException.AncoBadRequestException(errorBody)
            AncoHttpException.CODE_UNAUTHORIZED -> AncoHttpException.AncoUnauthorizedException(errorBody)
            AncoHttpException.CODE_FORBIDDEN -> AncoHttpException.AncoForbiddenException(errorBody)
            AncoHttpException.CODE_UNPROCESSABLE -> AncoHttpException.AncoUnprocessableException(errorBody)
            AncoHttpException.CODE_SERVER_ERROR -> AncoHttpException.AncoServerException(errorBody)
            else -> AncoHttpException(errorBody)
        }
    }

}