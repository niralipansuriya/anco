package com.app.ancoturf.data.cart

import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.data.cart.remote.CartService
import com.app.ancoturf.data.cart.remote.entity.CartDetailsResponse
import com.app.ancoturf.data.cart.remote.entity.CountryResponse
import com.app.ancoturf.data.cart.remote.entity.OrderDetails
import com.app.ancoturf.data.cart.remote.entity.PaymentMethodsResponse
import com.app.ancoturf.data.common.local.AncoRoomDatabase
import com.app.ancoturf.data.common.network.CommonService
import com.app.ancoturf.domain.cart.CartRepository
import io.reactivex.Single
import java.util.logging.Logger
import javax.inject.Inject

class CartDataRepository @Inject constructor(private val ancoRoomDatabase: AncoRoomDatabase) :
    CartRepository, CommonService<CartService>() {

    override val baseClass = CartService::class.java

    override fun insertCoupon(coupon: CouponDB): Single<Boolean> {
        return Single.create { emitter ->
            val count = ancoRoomDatabase.couponDao().insert(coupon)
            emitter.onSuccess(count > 0)
        }
    }

    override fun updateCoupon(coupon: CouponDB): Single<Boolean> {
        return Single.create { emitter ->
            val count = ancoRoomDatabase.couponDao().update(coupon)
            emitter.onSuccess(true)
        }
    }

    override fun getAllCoupons(): Single<List<CouponDB>> {
        return ancoRoomDatabase.couponDao().getItems()
    }

    override fun insertProduct(product: ProductDB): Single<Boolean> {
        return Single.create { emitter ->
            val count = ancoRoomDatabase.productDao().insert(product)
            emitter.onSuccess(count > 0)
        }
    }

    override fun updateProduct(product: ProductDB): Single<Boolean> {
        return Single.create { emitter ->
            val count = ancoRoomDatabase.productDao().update(product)
            emitter.onSuccess(true)
        }
    }

    override fun getAllProducts(): Single<List<ProductDB>> {
        return ancoRoomDatabase.productDao().getItems()
    }

    override fun getCountries(): Single<BaseResponse<ArrayList<CountryResponse>>> {
        return networkService.getCountries().map {
            it
        }
    }

    override fun getCartDetails(
        products: String,
        couponId: Array<String>?,
        shippingCountryCode: String?,
        shippingPostalCode: String?,
        shippingType: String?,
        credit: String?
    ): Single<BaseResponse<CartDetailsResponse>> {
        return networkService.getCartDetails(
            products,
            couponId,
            shippingCountryCode,
            shippingPostalCode,
            shippingType,
            credit
        ).map {
            it
        }
    }

    override fun getAvailableQty(products: String): Single<BaseResponse<Any>> {
        return networkService.getAvailableQty(
            products
        ).map {
            it
        }
    }

    override fun getPaymentMethods(): Single<BaseResponse<PaymentMethodsResponse>> {
        return networkService.getPaymentMethods().map {
            it
        }
    }

    override fun getCouponById(couponId: Int): Single<CouponDB> {
        return ancoRoomDatabase.couponDao().getItem(couponId.toLong())
    }

    override fun getProductById(productId: Int): Single<ProductDB> {
        return ancoRoomDatabase.productDao().getItem(productId.toLong())
    }

    override fun deleteCoupon(coupon: CouponDB): Single<Boolean> {
        return Single.create { emitter ->
            ancoRoomDatabase.couponDao().deleteItem(coupon.id!!.toLong())
            emitter.onSuccess(true)
        }
    }

    override fun deleteProduct(product: ProductDB): Single<Boolean> {
        return Single.create { emitter ->
            ancoRoomDatabase.productDao().deleteItem(product.product_id!!.toLong())
            emitter.onSuccess(true)
        }
    }

    override fun createOrder(
        email: String,
        purchaseFirstName: String?,
        purchaseLastName: String?,
        shippingFirstName: String?,
        shippingLastName: String?,
        shippingAddressLine1: String?,
        shippingAddressLine2: String?,
        shippingCountryCode: String?,
        shippingState: String?,
        shippingCity: String?,
        shippingPhone: String?,
        shippingPostalCode: Int,
        shippingType: String?,
        products: String,
        deliveryDate: String,
        credit: String,
        subTotalPrice: String,
        totalDiscount: String,
        creditDiscount: String,
        shippingPrice: String,
        totalCartPrice: String,
        couponCode: Array<String>,
        instructionNotes: String,
        is_checked: Int,
        adminFee: String,
        freeProduct : String
    ): Single<BaseResponse<OrderDetails>> {

        return networkService.createOrder(
            email,
            purchaseFirstName,
            purchaseLastName,
            shippingFirstName,
            shippingLastName,
            shippingAddressLine1,
            shippingAddressLine2,
            shippingCountryCode,
            shippingState,
            shippingCity,
            shippingPhone,
            shippingPostalCode,
            shippingType,
            products,
            deliveryDate,
            credit,
            subTotalPrice,
            totalDiscount,
            creditDiscount,
            shippingPrice,
            totalCartPrice,
            couponCode,
            instructionNotes,
            is_checked,
            adminFee,
            freeProduct
        ).map {
            it
        }
    }

    override fun updateOrder(
        orderId: Int,
        paymentMethod: String,
        paymentDetails: String?,
        paymentTransactionCompleted: String?,
        quoteID: String?
    ): Single<BaseResponse<OrderDetails>> {
        return networkService.updateOrder(
            orderId,
            paymentMethod,
            paymentDetails,
            paymentTransactionCompleted, quoteID
        ).map {
            it
        }
    }

    override fun deleteAllCoupons(): Single<Boolean> {
        return Single.create { emitter ->
            ancoRoomDatabase.couponDao().deleteAll()
            emitter.onSuccess(true)
        }
    }

    override fun deleteAllProducts(): Single<Boolean> {
        return Single.create { emitter ->
            ancoRoomDatabase.productDao().deleteAll()
            emitter.onSuccess(true)
        }
    }
}