package com.app.ancoturf.data.common

import android.text.TextUtils
import com.app.ancoturf.data.common.entity.ErrorResponse
import com.google.gson.Gson
import org.json.JSONObject

/**
 * this util is used for handle firebase function response
 */
object FunctionResultUtil {

    private const val CODE_KEY = "code"
    private const val SUCCESS_CODE = "200"
    private const val UNKNOWN_ERROR = "UNKNOWN ERROR"
    private const val NULL_ERROR = "NULL DATA ERROR"

    fun handleResponse(jsonResult: String): Any {
        val responseObj = JSONObject(jsonResult)
        return if (responseObj.has(CODE_KEY)) {
            val code = responseObj.get(CODE_KEY) as String
            if (code == SUCCESS_CODE) {
                responseObj
            } else {
                val errorResponse = Gson().fromJson(jsonResult, ErrorResponse::class.java)
                var message = ""
//                errorResponse.data?.forEach {
//                    message = when {
//                        it == null -> NULL_ERROR
//                        TextUtils.isEmpty(message) -> it
//                        else -> "$message, $it"
//                    }
//                }
                message
            }
        } else {
            UNKNOWN_ERROR
        }
    }
}