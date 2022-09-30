package com.app.ancoturf.presentation.cart

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.ancoturf.data.BaseResponse
import com.app.ancoturf.data.afterpay.remote.entity.response.CaptureResponse
import com.app.ancoturf.data.afterpay.remote.entity.response.OrderResponse
import com.app.ancoturf.data.cart.FreeProductAPI
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.data.cart.remote.entity.CartDetailsResponse
import com.app.ancoturf.data.cart.remote.entity.CountryResponse
import com.app.ancoturf.data.cart.remote.entity.OrderDetails
import com.app.ancoturf.data.cart.remote.entity.PaymentMethodsResponse
import com.app.ancoturf.data.cart.remote.entity.request.CartProductRequest
import com.app.ancoturf.data.offer.remote.OfferDataResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.domain.afterpay.usecases.CaptureUseCase
import com.app.ancoturf.domain.afterpay.usecases.OrdersUseCase
import com.app.ancoturf.domain.cart.usecases.*
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.extension.customSubscribe
import com.app.ancoturf.presentation.common.base.BaseObservableViewModel
import com.app.ancoturf.utils.Utility
import com.google.gson.Gson
import javax.inject.Inject

class CartViewModel @Inject constructor(
    val app: Application
    , private val insertCouponUseCase: InsertCouponUseCase
    , private val getCouponsUseCase: GetCouponsUseCase
    , private val deleteCouponUseCase: DeleteCouponUseCase
    , private val insertProductUseCase: InsertProductUseCase
    , private val getProductsUseCase: GetProductsUseCase
    , private val deleteProductUseCase: DeleteProductUseCase
    , private val getCountriesUseCase: GetCountriesUseCase
    , private val getCartDetailsUseCase: GetCartDetailsUseCase
    , private val getProductQuantityUseCase: GetProductQuantityUseCase
    , private val getPaymentMethodsUseCase: GetPaymentMethodsUseCase
    , private val getCouponByIdUseCase: GetCouponByIdUseCase
    , private val getProductByIdUseCase: GetProductByIdUseCase
    , private val ordersUseCase: OrdersUseCase
    , private val captureUseCase: CaptureUseCase
    , private val createOrderUseCase: CreateOrderUseCase
    , private val updateOrderUseCase: UpdateOrderUseCase
    , private val deliveryDateValidationUseCase: DeliveryDateValidationUseCase
) : BaseObservableViewModel(app) {

    val _couponsLiveData = MutableLiveData<List<CouponDB>>()
    val couponsLiveData: LiveData<List<CouponDB>> get() = _couponsLiveData

    val _productsLiveData = MutableLiveData<List<ProductDB>>()
    val productsLiveData: LiveData<List<ProductDB>> get() = _productsLiveData

    val _couponLiveData = MutableLiveData<CouponDB>()
    val couponLiveData: LiveData<CouponDB> get() = _couponLiveData

    val _productLiveData = MutableLiveData<ProductDB>()
    val productLiveData: LiveData<ProductDB> get() = _productLiveData

    val _couponInsertionLiveData = MutableLiveData<Boolean?>()
    val couponInsertionLiveData: LiveData<Boolean?> get() = _couponInsertionLiveData

    val _productInsertionLiveData = MutableLiveData<Boolean?>()
    val productInsertionLiveData: LiveData<Boolean?> get() = _productInsertionLiveData

    val _couponDeletionLiveData = MutableLiveData<Boolean?>()
    val couponDeletionLiveData: LiveData<Boolean?> get() = _couponDeletionLiveData

    val _productDeletionLiveData = MutableLiveData<Boolean?>()
    val productDeletionLiveData: LiveData<Boolean?> get() = _productDeletionLiveData

    val _countryListLiveData = MutableLiveData<ArrayList<CountryResponse>>()
    val countryListLiveData: LiveData<ArrayList<CountryResponse>> get() = _countryListLiveData

    val _cartDetailsLiveData = MutableLiveData<BaseResponse<CartDetailsResponse>>()
    val cartDetailsLiveData: LiveData<BaseResponse<CartDetailsResponse>> get() = _cartDetailsLiveData

    val _availableQtyLiveData = MutableLiveData<BaseResponse<Any>>()
    val AvailableQtyLiveData: LiveData<BaseResponse<Any>> get() = _availableQtyLiveData

    val _paymentMethodsLiveData = MutableLiveData<PaymentMethodsResponse>()
    val paymentMethodsLiveData: LiveData<PaymentMethodsResponse> get() = _paymentMethodsLiveData

    val _ordersLiveData = MutableLiveData<OrderResponse>()
    val ordersLiveData: LiveData<OrderResponse> get() = _ordersLiveData

    val _captureLiveData = MutableLiveData<CaptureResponse>()
    val captureLiveData: LiveData<CaptureResponse> get() = _captureLiveData

    val _orderDetailsLiveData = MutableLiveData<BaseResponse<OrderDetails>>()
    val orderDetailsLiveData: LiveData<BaseResponse<OrderDetails>> get() = _orderDetailsLiveData

    val _deliveryDateValidationLiveData = MutableLiveData<Boolean>()
    val deliveryDateValidationLiveData: LiveData<Boolean> get() = _deliveryDateValidationLiveData


    fun insertCoupon(coupon: OfferDataResponse.Data.Coupon) {
        val data = mapOf(
            UseCaseConstants.COUPON_UPDATE to false,
            UseCaseConstants.COUPON_ID to coupon.id,
            UseCaseConstants.COUPON_CODE to coupon.code,
            UseCaseConstants.COUPON_DISCOUNT to coupon.discount,
            UseCaseConstants.COUPON_DISCOUNT_TYPE to coupon.discountType,
            UseCaseConstants.COUPON_EXPIRY_DATE to coupon.expiryDate,
            UseCaseConstants.COUPON_ITEM_IDS to coupon.itemIds,
            UseCaseConstants.COUPON_ITEM_TYPE to coupon.itemType,
            UseCaseConstants.COUPON_NAME to coupon.name,
            UseCaseConstants.COUPON_START_DATE to coupon.startDate,
            UseCaseConstants.COUPON_STATUS to coupon.status
        )

        insertCouponUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            Log.e("success", "${it}")
            _couponInsertionLiveData.value = it
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _couponInsertionLiveData.value = false
        }).collect()
    }

    fun insertCoupon(coupon: CouponDB) {
        val data = mapOf(
            UseCaseConstants.COUPON_UPDATE to false,
            UseCaseConstants.COUPON_ID to coupon.id,
            UseCaseConstants.COUPON_CODE to coupon.code,
            UseCaseConstants.COUPON_DISCOUNT to coupon.discount,
            UseCaseConstants.COUPON_DISCOUNT_TYPE to coupon.discount_type,
            UseCaseConstants.COUPON_EXPIRY_DATE to coupon.expiry_date,
            UseCaseConstants.COUPON_ITEM_IDS to coupon.item_ids,
            UseCaseConstants.COUPON_ITEM_TYPE to coupon.item_type,
            UseCaseConstants.COUPON_NAME to coupon.name,
            UseCaseConstants.COUPON_START_DATE to coupon.start_date,
            UseCaseConstants.COUPON_STATUS to coupon.status
        )
        insertCouponUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            Log.e("success", "${it}")
            _couponInsertionLiveData.value = it
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _couponInsertionLiveData.value = false
        }).collect()
    }

    fun insertProduct(product: ProductDetailResponse, shouldUpdate: Boolean) {
        var id = if (product.productId == 0) product.id else product.productId
        val data = mapOf(
            UseCaseConstants.PRODUCT_UPDATE to shouldUpdate,
            UseCaseConstants.PRODUCT_ID to id,
            UseCaseConstants.PRODUCT_NAME to product.productName,
            UseCaseConstants.PRODUCT_FEATURE_IMAGE_URL to product.featureImageUrl,
            UseCaseConstants.PRODUCT_QTY to product.qty,
            UseCaseConstants.PRODUCT_PRICE to product.price,
            UseCaseConstants.PRODUCT_CATEGORY_ID to product.productCategoryId,
            UseCaseConstants.PRODUCT_UNIT to product.productUnit,
            UseCaseConstants.PRODUCT_UNIT to product.productUnit,
            UseCaseConstants.PRODUCT_UNIT_ID to product.productUnitId,
            UseCaseConstants.PRODUCT_BASE_TOTAL_PRICE to (product.price * product.qty),
            UseCaseConstants.PRODUCT_IS_TURF to if (product.productUnitId == 2) 1 else 0,
            UseCaseConstants.PRODUCT_TOTAL_PRICE to (product.price * product.qty),
            UseCaseConstants.PRODUCT_REDEEMABLE_AGAINST_CREDIT to false
        )
        insertProductUseCase.execute(data).customSubscribe({
            Log.e("success inserted", "${it}")
            _productInsertionLiveData.value = it
        }, {
            Log.e("failure inserted", "$it")
            _productInsertionLiveData.value = false
        }).collect()
    }

    fun insertProduct(product: ProductDB, shouldUpdate: Boolean) {
        val data = mapOf(
            UseCaseConstants.PRODUCT_UPDATE to shouldUpdate,
            UseCaseConstants.PRODUCT_ID to product.product_id,
            UseCaseConstants.PRODUCT_NAME to product.product_name,
            UseCaseConstants.PRODUCT_FEATURE_IMAGE_URL to product.feature_img_url,
            UseCaseConstants.PRODUCT_QTY to product.qty,
            UseCaseConstants.PRODUCT_PRICE to product.price,
            UseCaseConstants.PRODUCT_CATEGORY_ID to product.product_category_id,
            UseCaseConstants.PRODUCT_UNIT to product.product_unit,
            UseCaseConstants.PRODUCT_UNIT_ID to product.product_unit_id,
            UseCaseConstants.PRODUCT_BASE_TOTAL_PRICE to product.base_total_price,
            UseCaseConstants.PRODUCT_TOTAL_PRICE to product.total_price,
            UseCaseConstants.PRODUCT_REDEEMABLE_AGAINST_CREDIT to product.product_redeemable_against_credit
        )
        insertProductUseCase.execute(data).customSubscribe({
            Log.e("success", "${it}")
            _productInsertionLiveData.value = it
        }, {
            Log.e("failure", "$it")
            _productInsertionLiveData.value = false
        }).collect()
    }

    fun deleteCoupon(coupon: CouponDB?) {
        var data: Map<String, CouponDB>? = null
        if (coupon != null)
            data = mapOf(
                UseCaseConstants.COUPON to coupon
            )
        deleteCouponUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            Log.e("success", "${it}")
            _couponDeletionLiveData.value = it
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _couponDeletionLiveData.value = false
        }).collect()
    }

    fun deleteProduct(product: ProductDB?) {
        var data: Map<String, ProductDB>? = null
        if (product != null)
            data = mapOf(
                UseCaseConstants.PRODUCT to product
            )
        deleteProductUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            Log.e("success", "${it}")
            _productDeletionLiveData.value = it
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _productDeletionLiveData.value = false
        }).collect()
    }

    fun getCoupons() {
        getCouponsUseCase.execute().customSubscribe({
            Utility.cancelProgress()
            Log.e("success", "${it}")
            _couponsLiveData.value = it
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _errorLiveData.value = "No coupon found"
        }).collect()
    }

    fun getProducts() {
        getProductsUseCase.execute().customSubscribe({
            Utility.cancelProgress()
            Log.e("success", "${it}")
            _productsLiveData.value = it
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _errorLiveData.value = "No product found"
        }).collect()
    }

    fun getCouponById(couponId: Int) {
        val data = mapOf(
            UseCaseConstants.COUPON_ID to couponId
        )
        getCouponByIdUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            Log.e("success", "${it}")
            _couponLiveData.value = it
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _couponLiveData.value = null
        }).collect()
    }

    fun getProductById(productId: Int) {
        val data = mapOf(
            UseCaseConstants.PRODUCT_ID to productId
        )
        getProductByIdUseCase.execute(data).customSubscribe({
            Log.e("success", "${it}")
            _productLiveData.value = it
        }, {
            Log.e("failure", "${it?.message}")
            _productLiveData.value = null
        }).collect()
    }


    fun getCountries() {
        getCountriesUseCase.execute().customSubscribe({
            Utility.cancelProgress()
            Log.e("success", "${it}")
            _countryListLiveData.value = it.data
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _errorLiveData.value = it?.message
        }).collect()
    }

    fun getAvailableQty(cartProducts: ArrayList<ProductDB>){
        val products = ArrayList<CartProductRequest>()
        for (i in 0 until cartProducts.size) {
            var cartProductRequest =
                CartProductRequest(cartProducts[i].product_id, cartProducts[i].qty)
            products.add(cartProductRequest)
        }

        val data = mapOf(
            UseCaseConstants.PRODUCTS to Gson().toJson(products)
        )

        getProductQuantityUseCase.execute(data).customSubscribe({
            //            Utility.cancelProgress()
            Log.e("success", "${it}")
            _availableQtyLiveData.value = it
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _errorLiveData.value = it?.message
        }).collect()

    }
    fun getCartDetails(
        cartProducts: ArrayList<ProductDB>,
        cartCoupons: ArrayList<CouponDB>,
        couponCode: String?,
        shippingType: String?,
        shippingCountryCode: String?,
        shippingPostalCode: String?,
        credit: String?
    ) {
        var coupons: Array<String?>? = null
        if (!Utility.isValueNull(couponCode))
            coupons = arrayOfNulls(cartCoupons.size + 1)
        else
            coupons = arrayOfNulls<String>(cartCoupons.size)
        if (coupons != null && coupons.size > 0) {
            for (i in 0 until cartCoupons.size) {
                coupons[i] = cartCoupons[i].code
            }
        }

        if (!Utility.isValueNull(couponCode))
            coupons[coupons.size - 1] = couponCode

        val products = ArrayList<CartProductRequest>()
        for (i in 0 until cartProducts.size) {
            if (cartProducts[i].price != 0.0f) {
                var cartProductRequest =
                    CartProductRequest(cartProducts[i].product_id, cartProducts[i].qty)
                products.add(cartProductRequest)
            }
        }

        val data = mapOf(
            UseCaseConstants.PRODUCTS to Gson().toJson(products),
            UseCaseConstants.COUPON_CODE to coupons,
            UseCaseConstants.SHIPPING_COUNTRY_CODE to shippingCountryCode,
            UseCaseConstants.SHIPPING_POSTAL_CODE to shippingPostalCode,
            UseCaseConstants.SHIPPING_TYPE to shippingType,
            UseCaseConstants.CREDIT to credit
        )

        getCartDetailsUseCase.execute(data).customSubscribe({
            //            Utility.cancelProgress()
            Log.e("success", "${it}")
            _cartDetailsLiveData.value = it
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _errorLiveData.value = it?.message
        }).collect()
    }

    fun getPaymentMethods() {
        getPaymentMethodsUseCase.execute().customSubscribe({
            Utility.cancelProgress()
            Log.e("success", "${it}")
            _paymentMethodsLiveData.value = it.data
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _errorLiveData.value = it?.message
        }).collect()
    }

    fun afterpayOrders(
        amount: String,
        currency: String,
        phoneNumber: String?,
        name: String,
        surname: String,
        email: String
    ) {
        val data = mapOf(
            UseCaseConstants.AMOUNT to amount,
            UseCaseConstants.CURRENCY to currency,
            UseCaseConstants.PHONE_NUMBER to phoneNumber,
            UseCaseConstants.GIVEN_NAMES to name,
            UseCaseConstants.SURNAME to surname,
            UseCaseConstants.EMAIL to email,
            UseCaseConstants.REDIRECT_CONFIRM_URL to "https://www.google.com",
            UseCaseConstants.REDIRECT_CANCEL_URL to "https://www.wikipedia.org"
        )

        ordersUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            Log.e("success", "${it}")

            _ordersLiveData.value = it
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _errorLiveData.value = it?.getErrorMessage()
        }).collect()
    }

    fun afterpayCapture(token: String, merchantReference: String) {

        val data = mapOf(
            UseCaseConstants.TOKEN to token,
            UseCaseConstants.MERCHANT_REFERENCE to merchantReference
        )

        captureUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            Log.e("success", "${it}")
            _captureLiveData.value = it
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _errorLiveData.value = it?.message
        }).collect()
    }

    fun createOrder(
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
        cartProducts: ArrayList<ProductDB>,
        deliveryDate: String,
        credit: String?,
        subTotalPrice: String,
        totalDiscount: String,
        creditDiscount: String,
        shippingPrice: String,
        totalCartPrice: String,
        cartCoupons: ArrayList<CouponDB>,
        couponCode: String?,
        instructions_notes: String?,
        is_checked: Int,
        adminFees : String,
        freeProducts : ArrayList<FreeProductAPI>
    ) {
        var coupons: Array<String?>? = null
        if (!Utility.isValueNull(couponCode))
            coupons = arrayOfNulls(cartCoupons.size + 1)
        else
            coupons = arrayOfNulls<String>(cartCoupons.size)
        for (i in 0 until cartCoupons.size) {
            coupons[i] = cartCoupons[i].code
        }

        if (!Utility.isValueNull(couponCode))
            coupons[cartCoupons.size - 1] = couponCode

        val products = ArrayList<CartProductRequest>()
        for (i in 0 until cartProducts.size) {
            var cartProductRequest =
                CartProductRequest(cartProducts[i].product_id, cartProducts[i].qty)
            products.add(cartProductRequest)
        }

        val data = mapOf(
            UseCaseConstants.EMAIL to email,
            UseCaseConstants.PURCHASE_FIRST_NAME to purchaseFirstName,
            UseCaseConstants.PURCHASE_LAST_NAME to purchaseLastName,
            UseCaseConstants.SHIPPING_FIRST_NAME to shippingFirstName,
            UseCaseConstants.SHIPPING_LAST_NAME to shippingLastName,
            UseCaseConstants.SHIPPING_ADDRESS_LINE_1 to shippingAddressLine1,
            UseCaseConstants.SHIPPING_ADDRESS_LINE_2 to shippingAddressLine2,
            UseCaseConstants.SHIPPING_COUNTRY_CODE to shippingCountryCode,
            UseCaseConstants.SHIPPING_STATE to shippingState,
            UseCaseConstants.SHIPPING_CITY to shippingCity,
            UseCaseConstants.SHIPPING_PHONE to shippingPhone,
            UseCaseConstants.SHIPPING_POSTAL_CODE to shippingPostalCode,
            UseCaseConstants.SHIPPING_TYPE to shippingType,
            UseCaseConstants.PRODUCTS to Gson().toJson(products),
            UseCaseConstants.DELIVERY_DATE to deliveryDate,
            UseCaseConstants.CREDIT to credit,
            UseCaseConstants.SUB_TOTAL_PRICE to subTotalPrice,
            UseCaseConstants.TOTAL_DISCOUNT to totalDiscount,
            UseCaseConstants.CREDIT_DISCOUNT to creditDiscount,
            UseCaseConstants.SHIPPING_PRICE to shippingPrice,
            UseCaseConstants.TOTAL_CART_PRICE to totalCartPrice,
            UseCaseConstants.COUPON_CODE to coupons,
            UseCaseConstants.instructions_notes to instructions_notes,
            UseCaseConstants.IS_CHECKED to is_checked,
            UseCaseConstants.ADMIN_FEE to adminFees,
            UseCaseConstants.FREE_PRODUCTS to  Gson().toJson(freeProducts)
        )

        createOrderUseCase.execute(data).customSubscribe({
            Log.e("success", "${it}")
            Utility.cancelProgress()
            _orderDetailsLiveData.value = it
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _errorLiveData.value = it?.getErrorMessage()
        }).collect()
    }

    fun updateOrder(
        orderId: Int,
        paymentMethod: String,
        paymentDetails: String?,
        paymentTransactionCompleted: String?,
        quoteID: String?
    ) {
        val data = mapOf(
            UseCaseConstants.ORDER_ID to orderId,
            UseCaseConstants.PAYMENT_METHOD to paymentMethod,
            UseCaseConstants.PAYMENT_DETAILS to paymentDetails,
            UseCaseConstants.PAYMENT_TRANSACTION_COMPLETED to paymentTransactionCompleted,
            UseCaseConstants.QUOTEID to quoteID
        )

        updateOrderUseCase.execute(data).customSubscribe({
            Utility.cancelProgress()
            Log.e("success", "${it}")
            _orderDetailsLiveData.value = it
        },
            {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _errorLiveData.value = it?.message
        }).collect()
    }

    fun deliveryDAteValidation(
        deliveryDate: String
    ) {
        val data = mapOf(
            UseCaseConstants.DELIVERY_DATE to deliveryDate
        )
        deliveryDateValidationUseCase.execute(data).customSubscribe({
            //            Utility.cancelProgress()
            Log.e("success", "${it}")
            _deliveryDateValidationLiveData.value = it.valid == 1
        }, {
            Log.e("failure", "$it")
            Utility.cancelProgress()
            _deliveryDateValidationLiveData.value = false
        }).collect()
    }
}

