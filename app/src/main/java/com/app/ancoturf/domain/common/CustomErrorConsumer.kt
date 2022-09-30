package com.app.ancoturf.domain.common

import com.app.ancoturf.data.common.entity.ErrorResponse
import com.app.ancoturf.data.common.exceptions.AncoHttpException
import io.reactivex.functions.Consumer
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.UnknownServiceException

abstract class CustomErrorConsumer : Consumer<Throwable> {

    override fun accept(throwable: Throwable) {
        if (throwable is AncoHttpException) {
            accept(throwable)
        } else {
            val errorBody = ErrorResponse()
            errorBody.message = throwable.message
            accept(createAncoException(throwable, errorBody))
        }
    }

    private fun createAncoException(throwable: Throwable, errorBody: ErrorResponse): AncoHttpException {
        return if (throwable is UnknownHostException ||
                throwable is ConnectException ||
                throwable is SocketTimeoutException ||
                throwable is UnknownServiceException
        ) {
            AncoHttpException.AncoNetWorkException(errorBody)
        } else {
            AncoHttpException(errorBody)
        }
    }

    abstract fun accept(it: AncoHttpException)
}