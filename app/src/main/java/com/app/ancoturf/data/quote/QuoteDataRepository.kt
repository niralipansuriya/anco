package com.app.ancoturf.data.quote

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.data.portfolio.remote.entity.QuoteAncoProductRequest
import com.app.ancoturf.data.portfolio.remote.entity.QuoteNonAncoProductRequest
import com.app.ancoturf.data.quote.remote.QuoteService
import com.app.ancoturf.data.quote.remote.entity.response.*
import com.app.ancoturf.domain.quote.QuoteRepository
import com.app.ancoturf.utils.Utility
import com.google.gson.Gson
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import javax.inject.Inject

class QuoteDataRepository @Inject constructor() : QuoteRepository, CommonService<QuoteService>() {

    companion object {
        const val PARSING_ERROR = "parsing error"
    }

    override val baseClass = QuoteService::class.java

    override fun getQuotesByCategories(
        status: String,
        minCost: String,
        maxCost: String,
        address: String,
        sortBy: String,
        page: String
    ): Single<BaseResponse<QuotesDataResponse>> {
        return networkService.getQuotesByCategories(status, minCost, maxCost, address, sortBy, page)
            .map {it
            }
    }

    override fun getAllQuotes(): Single<BaseResponse<QuotesDataResponse>> {
        return networkService.getAllQuotes().map {
            it
        }
    }


    override fun addEditQuotes(
        quoteId: Int,
        customerId: Int,
        customerName: String,
        customerAddress: String,
        customerEmail: String,
        customerMobile: String,
        customerPhone: String?,
        deliveryCost: String?,
        deliveryDate: String?,
        products: ArrayList<QuoteAncoProductRequest>?,
        customProducts: ArrayList<QuoteNonAncoProductRequest>?,
        deletedProductIds: ArrayList<Int>?,
        deletedCustomProductIds: ArrayList<Int>?,
        contactName: String,
        businessName: String,
        abn: String,
        address: String,
        mobileNumber: String,
        phoneNumber: String?,
        email: String,
        webUrl: String?,
        paymentTerms: String?,
        disclaimer: String?,
        registeredForGst: String?,
        logoUrl: String?,
        sendQuoteTo: ArrayList<String>?,
        deepLinkUrl: String,
        note: String
    ): Single<BaseResponse<AddEditQuoteResponse>> {

        val customerNameRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), customerName)
        val customerAddressRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), customerAddress)
        val customerPhoneRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), customerPhone)
        val customerEmailRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), customerEmail)
        val customerMobileRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), customerMobile)
        val deliveryCostRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), deliveryCost)
        val deliveryDateRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), deliveryDate)
        val productsRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), Gson().toJson(products))
        val customProductsRequestBody = RequestBody.create(
            MediaType.parse("text/plain"),
            Gson().toJson(customProducts)
        )

        val deletedCustomProducts = arrayOfNulls<Int>(deletedCustomProductIds!!.size)
        if (deletedCustomProductIds != null && deletedCustomProductIds.size > 0) {
            for (i in 0..(deletedCustomProductIds.size - 1)) {
                deletedCustomProducts[i] = deletedCustomProductIds[i]
            }
        }

        val deletedProducts = arrayOfNulls<Int>(deletedProductIds!!.size)
        if (deletedProductIds != null && deletedProductIds.size > 0) {
            for (i in 0..(deletedProductIds.size - 1)) {
                deletedProducts[i] = deletedProductIds[i]
            }
        }

        val contactNameRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), contactName)


        val businessNameRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), businessName)
        val abnRequestBody = RequestBody.create(MediaType.parse("text/plain"), abn)
        val addressRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), address)
        val mobileNumberRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), mobileNumber)
        val phoneNumberRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), phoneNumber)
        val emailRequestBody = RequestBody.create(MediaType.parse("text/plain"), email)
        val webUrlRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), webUrl)
        val paymentTermsRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), paymentTerms)
        val disclaimerRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), disclaimer)
        val registeredForGstRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), registeredForGst)

        val deepLinkUrlRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), deepLinkUrl)

        var logoUrlRequestBody: MultipartBody.Part? = null
        if (!Utility.isValueNull(logoUrl)) {
            val imageFile = File(logoUrl)
            val requestImageFile =
                RequestBody.create(MediaType.parse("image/*"), imageFile)
            logoUrlRequestBody =
                MultipartBody.Part.createFormData(
                    "logo_url",
                    imageFile.getName(),
                    requestImageFile
                )
        }
        val noteRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), note)

        val sendQuoteToEmails = arrayOfNulls<RequestBody>(sendQuoteTo!!.size)
        if (sendQuoteTo != null && sendQuoteTo.size > 0) {
            for (i in 0 until sendQuoteTo.size) {
                sendQuoteToEmails[i] =
                    RequestBody.create(MediaType.parse("text/plain"), sendQuoteTo[i])
            }
        }

        return networkService.addEditQuotes(
            quoteId = quoteId,
            customerId = customerId,
            customerName = customerNameRequestBody,
            customerAddress = customerAddressRequestBody,
            customerPhone = customerPhoneRequestBody,
            customerEmail = customerEmailRequestBody,
            customerMobile = customerMobileRequestBody,
            deliveryCost = deliveryCostRequestBody,
            deliveryDate = deliveryDateRequestBody,
            products = productsRequestBody,
            customProducts = customProductsRequestBody,
            deletedProductIds = deletedProducts,
            deletedCustomProductIds = deletedCustomProducts,
            contactName = contactNameRequestBody,
            businessName = businessNameRequestBody,
            abn = abnRequestBody,
            address = addressRequestBody,
            mobileNumber = mobileNumberRequestBody,
            phoneNumber = phoneNumberRequestBody,
            email = emailRequestBody,
            webUrl = webUrlRequestBody,
            paymentTerms = paymentTermsRequestBody,
            disclaimer = disclaimerRequestBody,
            registeredForGst = registeredForGstRequestBody,
            logoUrl = logoUrlRequestBody,
            sendQuoteTo = sendQuoteToEmails,
            deepLinkUrl = deepLinkUrlRequestBody,
            note = noteRequestBody
        ).map {
            it
        }

    }

    override fun addEditBusinessDetails(
        quoteId: Int,
        contactName: String,
        businessName: String,
        abn: String,
        address: String,
        mobileNumber: String,
        phoneNumber: String?,
        email: String,
        webUrl: String?,
        paymentTerms: String?,
        disclaimer: String?,
        registeredForGst: String?,
        logoUrl: String?,
        products: ArrayList<QuoteAncoProductRequest>?
    ): Single<BaseResponse<AddEditQuoteResponse>> {

        val contactNameRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), contactName)
        val businessNameRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), businessName)
        val abnRequestBody = RequestBody.create(MediaType.parse("text/plain"), abn)
        val addressRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), address)
        val mobileNumberRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), mobileNumber)
        var phoneNumberRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), "")
        if (!Utility.isValueNull(phoneNumber))
            phoneNumberRequestBody =
                RequestBody.create(MediaType.parse("text/plain"), phoneNumber)
        val emailRequestBody = RequestBody.create(MediaType.parse("text/plain"), email)
        var webUrlRequestBody = RequestBody.create(MediaType.parse("text/plain"), "")
        if (!Utility.isValueNull(webUrl))
            webUrlRequestBody =
                RequestBody.create(MediaType.parse("text/plain"), webUrl)
        var paymentTermsRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), "")
        if (!Utility.isValueNull(paymentTerms))
            paymentTermsRequestBody =
                RequestBody.create(MediaType.parse("text/plain"), paymentTerms)
        var disclaimerRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), "")
        if (!Utility.isValueNull(disclaimer))
            disclaimerRequestBody =
                RequestBody.create(MediaType.parse("text/plain"), disclaimer)
        var registeredForGstRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), "0")
        if (!Utility.isValueNull(registeredForGst))
            registeredForGstRequestBody =
                RequestBody.create(MediaType.parse("text/plain"), registeredForGst)
        val productsRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), Gson().toJson(products))

        var logoUrlRequestBody: MultipartBody.Part? = null
        if (!Utility.isValueNull(logoUrl)) {
            val imageFile = File(logoUrl)
            val requestImageFile =
                RequestBody.create(MediaType.parse("image/*"), imageFile)
            logoUrlRequestBody =
                MultipartBody.Part.createFormData(
                    "logo_url",
                    imageFile.getName(),
                    requestImageFile
                )
        }

        return networkService.addEditBusinessDetails(
            quoteId = quoteId,
            contactName = contactNameRequestBody,
            businessName = businessNameRequestBody,
            abn = abnRequestBody,
            address = addressRequestBody,
            mobileNumber = mobileNumberRequestBody,
            phoneNumber = phoneNumberRequestBody,
            email = emailRequestBody,
            webUrl = webUrlRequestBody,
            paymentTerms = paymentTermsRequestBody,
            disclaimer = disclaimerRequestBody,
            registeredForGst = registeredForGstRequestBody,
            logoUrl = logoUrlRequestBody,
            products = productsRequestBody
        ).map {
            it
        }
    }

    override fun getCustomers(): Single<BaseResponse<CustomersDataResponse>> {
        return networkService.getCustomers().map {
            it
        }
    }

    override fun addEditCustomers(
        quoteId: Int,
        customerId: Int,
        customerName: String,
        customerAddress: String,
        customerEmail: String,
        customerMobile: String,
        customerPhone: String?,
        contactName: String,
        businessName: String,
        abn: String,
        address: String,
        mobileNumber: String,
        phoneNumber: String?,
        email: String,
        webUrl: String?,
        paymentTerms: String?,
        disclaimer: String?,
        registeredForGst: String?
    ): Single<BaseResponse<AddEditQuoteResponse>> {

        val customerNameRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), customerName)
        val customerAddressRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), customerAddress)
        var customerPhoneRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), "")
        if (!Utility.isValueNull(customerPhone))
            customerPhoneRequestBody =
                RequestBody.create(MediaType.parse("text/plain"), customerPhone)
        val customerEmailRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), customerEmail)
        val customerMobileRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), customerMobile)

        val contactNameRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), contactName)
        val businessNameRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), businessName)
        val abnRequestBody = RequestBody.create(MediaType.parse("text/plain"), abn)
        val addressRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), address)
        val mobileNumberRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), mobileNumber)
        var phoneNumberRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), "")
        if (!Utility.isValueNull(phoneNumber))
            phoneNumberRequestBody =
                RequestBody.create(MediaType.parse("text/plain"), phoneNumber)
        val emailRequestBody = RequestBody.create(MediaType.parse("text/plain"), email)
        var webUrlRequestBody = RequestBody.create(MediaType.parse("text/plain"), "")
        if (!Utility.isValueNull(webUrl))
            webUrlRequestBody =
                RequestBody.create(MediaType.parse("text/plain"), webUrl)
        var paymentTermsRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), "")
        if (!Utility.isValueNull(paymentTerms))
            paymentTermsRequestBody =
                RequestBody.create(MediaType.parse("text/plain"), paymentTerms)
        var disclaimerRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), "")
        if (!Utility.isValueNull(disclaimer))
            disclaimerRequestBody =
                RequestBody.create(MediaType.parse("text/plain"), disclaimer)
        var registeredForGstRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), "0")
        if (!Utility.isValueNull(registeredForGst))
            registeredForGstRequestBody =
                RequestBody.create(MediaType.parse("text/plain"), registeredForGst)

        return networkService.addEditCustomer(
            quoteId = quoteId,
            customerId = customerId,
            customerName = customerNameRequestBody,
            customerAddress = customerAddressRequestBody,
            customerPhone = customerPhoneRequestBody,
            customerEmail = customerEmailRequestBody,
            customerMobile = customerMobileRequestBody,
            contactName = contactNameRequestBody,
            businessName = businessNameRequestBody,
            abn = abnRequestBody,
            address = addressRequestBody,
            mobileNumber = mobileNumberRequestBody,
            phoneNumber = phoneNumberRequestBody,
            email = emailRequestBody,
            webUrl = webUrlRequestBody,
            paymentTerms = paymentTermsRequestBody,
            disclaimer = disclaimerRequestBody,
            registeredForGst = registeredForGstRequestBody
        ).map {
            it
        }
    }

    override fun getQuoteDetails(quoteId: Int): Single<BaseResponse<QuoteDetailsResponse>> {
        return networkService.getQuoteDetail(quoteId).map {
            it
        }
    }

    override fun addCustomProducts(
        customProductId: Int,
        name: String,
        descriptions: String,
        price: String,
        imagePath: String?
    ): Single<BaseResponse<CustomProductResponse>> {

        val nameRequestBody = RequestBody.create(MediaType.parse("text/plain"), name)
        val descriptionsRequestBody =
            RequestBody.create(MediaType.parse("text/plain"), descriptions)
        val priceRequestBody = RequestBody.create(MediaType.parse("text/plain"), price)


        var imageRequestBody: MultipartBody.Part? = null
        if (!Utility.isValueNull(imagePath)) {
            val imageFile = File(imagePath)
            val requestImageFile =
                RequestBody.create(MediaType.parse("image/*"), imageFile)
            imageRequestBody =
                MultipartBody.Part.createFormData(
                    "image",
                    imageFile.getName(),
                    requestImageFile
                )
        }

        return networkService.addCustomProduct(
            customProductId = customProductId,
            name = nameRequestBody,
            descriptions = descriptionsRequestBody,
            price = priceRequestBody,
            image = imageRequestBody
        ).map {
            it
        }

    }

    override fun resendQuote(
        quoteId: Int,
        sendQuoteTo: ArrayList<String?>?
    ): Single<BaseResponse<AddEditQuoteResponse>> {

        val sendQuoteToEmails = arrayOfNulls<RequestBody>(sendQuoteTo!!.size)
        if (sendQuoteTo != null && sendQuoteTo.size > 0) {
            for (i in 0 until sendQuoteTo.size) {
                sendQuoteToEmails[i] =
                    RequestBody.create(MediaType.parse("text/plain"), sendQuoteTo[i])
            }
        }
        return networkService.resendQuote(
            quoteId = quoteId,
            sendQuoteTo = sendQuoteToEmails
        ).map {
            it
        }
    }

    override fun duplicateQuote(quoteId: Int): Single<BaseResponse<DuplicateQuoteResponse>> {
        return networkService.duplicateQuote(quoteId = quoteId).map {
            it
        }
    }

}
