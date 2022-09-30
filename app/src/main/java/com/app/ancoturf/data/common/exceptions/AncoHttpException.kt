package com.app.ancoturf.data.common.exceptions

import com.app.ancoturf.data.common.entity.ErrorResponse


open class AncoHttpException(private val response: ErrorResponse) : Exception() {

    companion object {
        const val CODE_BAD_REQUEST = "400"
        const val CODE_UNAUTHORIZED = "401"
        const val CODE_FORBIDDEN = "403"
        const val CODE_UNPROCESSABLE = "422"
        const val CODE_SERVER_ERROR = "500"
    }

    class AncoBadRequestException(response: ErrorResponse) : AncoHttpException(response)
    class AncoForbiddenException(response: ErrorResponse) : AncoHttpException(response)
    class AncoNetWorkException(response: ErrorResponse) : AncoHttpException(response)
    class AncoServerException(response: ErrorResponse) : AncoHttpException(response)
    class AncoUnauthorizedException(response: ErrorResponse) : AncoHttpException(response)
    class AncoUnprocessableException(response: ErrorResponse) : AncoHttpException(response)

    fun getErrorMessage(): String? {
        return response.message
    }
}