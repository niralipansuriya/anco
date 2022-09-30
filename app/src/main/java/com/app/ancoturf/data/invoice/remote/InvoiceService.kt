package com.app.ancoturf.data.invoice.remote

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.invoice.remote.entity.response.InvoiceDataResponse
import io.reactivex.Single
import retrofit2.http.*

interface InvoiceService {

    @FormUrlEncoded
    @POST("api/getxeroPdf")
    fun getInvoicePDF(@Field("InvoiceId") InvoiceId: String): Single<BaseResponse<InvoiceDataResponse>>
}