package com.app.ancoturf.domain.cart

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.cart.FreeProductAPI
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.data.cart.remote.entity.CartDetailsResponse
import com.app.ancoturf.data.cart.remote.entity.CountryResponse
import com.app.ancoturf.data.cart.remote.entity.OrderDetails
import com.app.ancoturf.data.cart.remote.entity.PaymentMethodsResponse
import com.app.ancoturf.presentation.cart.DeliveryDatesResponseModel
import io.reactivex.Single

interface CartRepository {

    fun insertCoupon(coupon: CouponDB): Single<Boolean>

    fun updateCoupon(coupon: CouponDB): Single<Boolean>

    fun getAllCoupons(): Single<List<CouponDB>>

    fun getCouponById(couponId: Int): Single<CouponDB>

    fun deleteCoupon(coupon: CouponDB): Single<Boolean>

    fun deleteAllCoupons(): Single<Boolean>

    fun insertProduct(product: ProductDB): Single<Boolean>

    fun updateProduct(product: ProductDB): Single<Boolean>

    fun getAllProducts(): Single<List<ProductDB>>

    fun getProductById(productId: Int): Single<ProductDB>

    fun deleteProduct(product: ProductDB): Single<Boolean>

    fun deleteAllProducts(): Single<Boolean>

    fun getCountries(): Single<BaseResponse<ArrayList<CountryResponse>>>

    fun getCartDetails(products: String, couponId: Array<String>?, shippingCountryCode: String?, shippingPostalCode: String?, shippingType: String?, credit: String?): Single<BaseResponse<CartDetailsResponse>>

    fun getAvailableQty(products: String): Single<BaseResponse<Any>>

    fun getPaymentMethods(): Single<BaseResponse<PaymentMethodsResponse>>

    fun createOrder(email: String, purchaseFirstName: String?, purchaseLastName: String?, shippingFirstName: String?, shippingLastName: String? , shippingAddressLine1: String?, shippingAddressLine2: String?, shippingCountryCode: String?, shippingState: String?, shippingCity: String?, shippingPhone: String?, shippingPostalCode: Int, shippingType: String?, products: String , deliveryDate: String , credit: String , subTotalPrice: String , totalDiscount: String, creditDiscount: String, shippingPrice: String, totalCartPrice: String, couponCode: Array<String>,instructionNotes: String, is_checked: Int, adminFee : String, freeProductAPI: String): Single<BaseResponse<OrderDetails>>

    fun updateOrder(orderId: Int , paymentMethod: String , paymentDetails: String? , paymentTransactionCompleted: String?,quoteID:String?): Single<BaseResponse<OrderDetails>>


}