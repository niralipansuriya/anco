package com.app.ancoturf.data.cart.remote

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.cart.FreeProductAPI
import com.app.ancoturf.data.cart.remote.entity.*
import io.reactivex.Single
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface CartService {
    @GET("api/countries")
    fun getCountries(): Single<BaseResponse<ArrayList<CountryResponse>>>

    @FormUrlEncoded
    @POST("api/order/cart")
    fun getCartDetails(
        @Field("products") products: String,
        @Field("coupon_code[]") couponId: Array<String>?,
        @Field("shipping_country_code") shippingCountryCode: String?,
        @Field("shipping_postal_code") shippingPostalCode: String?,
        @Field("shipping_type") shippingType: String?,
        @Field("credit") credit: String?
    ): Single<BaseResponse<CartDetailsResponse>>

//    https://ancob2b.sharptest.com.au/api/check_availabel_qty
    @FormUrlEncoded
    @POST("api/check_availabel_qty")
    fun getAvailableQty(
        @Field("products") products: String
    ): Single<BaseResponse<Any>>

    @GET("api/payment_methods")
    fun getPaymentMethods(): Single<BaseResponse<PaymentMethodsResponse>>

    @FormUrlEncoded
    @POST("api/order/create")
    fun createOrder(
        @Field("email") email: String,
        @Field("purchase_first_name") purchaseFirstName: String?,
        @Field("purchase_last_name") purchaseLastName: String?,
        @Field("shipping_first_name") shippingFirstName: String?,
        @Field("shipping_last_name") shippingLastName: String?,
        @Field("shipping_address_line_1") shippingAddressOne: String?,
        @Field("shipping_address_line_2") shippingAddressTwo: String?,
        @Field("shipping_country_code") shippingCountryCode: String?,
        @Field("shipping_state") shippingState: String?,
        @Field("shipping_city") shippingCity: String?,
        @Field("shipping_phone") shippingPhone: String?,
        @Field("shipping_postal_code") shippingPostalCode: Int,
        @Field("shipping_type") shippingType: String?,
        @Field("products") products: String,
        @Field("delivery_date") deliveryDate: String,
        @Field("credit") credit: String?,
        @Field("sub_total_price") subTotalPrice: String,
        @Field("total_discount") totalDiscount: String,
        @Field("credit_discount") creditDiscount: String,
        @Field("shipping_price") shippingPrice: String,
        @Field("total_cart_price") totalCartPrice: String,
        @Field("coupon_code[]") couponId: Array<String>?,
        @Field("instructions_notes") instructionsNotes: String,
        @Field("is_checked") is_checked: Int,
        @Field("admin_fees") adminFee: String,
        @Field("free_products") freeProduct: String
    ): Single<BaseResponse<OrderDetails>>

    @FormUrlEncoded
    @POST("api/order/update")
    fun updateOrder(
        @Field("order_id") email: Int,
        @Field("payment_method") shippingFirstName: String,
        @Field("payment_details") shippingLastName: String?,
        @Field("payment_transaction_completed") shippingAddressOne: String?,
        @Field("quoteID") quoteID: String?
    ): Single<BaseResponse<OrderDetails>>

   /* @GET("api/deliveryDays")
    fun getDeliveryDates(): Single<Object>*/

    @FormUrlEncoded
    @POST("api/deliveryDays")
    fun getDeliveryDates(
        @Field("id_cms_privileges") user_type: Int
        ): Single<Object>
}

