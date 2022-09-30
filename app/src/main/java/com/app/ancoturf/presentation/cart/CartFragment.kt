package com.app.ancoturf.presentation.cart

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.data.cart.remote.entity.CartDetailsResponse
import com.app.ancoturf.data.cart.remote.entity.CountryResponse
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.data.product.remote.entity.response.RelatedProductsResponse
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.extension.showAlert
import com.app.ancoturf.presentation.cart.adapters.CartCouponsAdapter
import com.app.ancoturf.presentation.cart.adapters.CartProductsAdapter
import com.app.ancoturf.presentation.cart.adapters.SpinnerCountryAdapter
import com.app.ancoturf.presentation.cart.adapters.SpinnerStateAdapter
import com.app.ancoturf.presentation.cart.interfaces.OnCartCouponDeleteListener
import com.app.ancoturf.presentation.cart.interfaces.OnCartProductChangeListener
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.interfaces.OnProductAddedToCart
import com.app.ancoturf.presentation.home.products.adapters.RelatedProductsAdapter
import com.app.ancoturf.presentation.home.shop.ShopFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.InputFilterMinMax
import com.app.ancoturf.utils.Logger
import com.app.ancoturf.utils.Utility
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_cart.listRelatedProducts
import kotlinx.android.synthetic.main.fragment_cart.txtCalculationLabel
import kotlinx.android.synthetic.main.fragment_product_detail.*
import kotlinx.android.synthetic.main.header.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class CartFragment :
    BaseFragment(),
    View.OnClickListener, AdapterView.OnItemSelectedListener {

    private var redirectToCheckout: Boolean = false
    private var shouldCallGetProducts: Boolean = false

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var cartViewModel: CartViewModel? = null
    private var getDeliveryDatesViewModel: DeliveryDatesViewModel? = null

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private var dpd: DatePickerDialog? = null

    override fun getContentResource(): Int = R.layout.fragment_cart

    var cartProductsAdapter: CartProductsAdapter? = null
    var cartProducts = ArrayList<ProductDB>()

    var cartCouponsAdapter: CartCouponsAdapter? = null
    var cartCoupons = ArrayList<CouponDB>()

    private var productsAdapter: RelatedProductsAdapter? = null

    private var spinnerCountryAdapter: SpinnerCountryAdapter? = null
    var countries = ArrayList<CountryResponse>()

    private var spinnerStateAdapter: SpinnerStateAdapter? = null
    var states = ArrayList<CountryResponse.State>()

    private var cartDetailsResponse: CartDetailsResponse? = null
    private var fillCredit: Boolean = true

    var shippingDetailsOpened = false
    private var addToCartProduct: ProductDetailResponse? = null

    private var shippingType = "null"
    private var deliveryDate = ""
    private var tmpDelDate = ""
    var allowedToPlaceOrder = 0
    var isCreditApplied = false
    var isOtherDataAvailable = false
    private var quoteDate: String = ""
    private var quoteNote: String = ""
    private var isSetDRDFromQuote: Boolean = false
    private var dialogDropTurfOnProperty: Dialog? = null
    private var isTurf: Boolean = false
    private var adminFee: Float = 0f
    private var qtyTurfForDTD: Float = 0f   // Turf qty >= 25 than DTD show 06012021
    private var isFromCheckoutFragment = false;
    private var mFirebaseAnalytics: FirebaseAnalytics? = null


    override fun viewModelSetup() {
        cartViewModel =
            ViewModelProviders.of(
                requireActivity(),
                viewModelFactory
            )[CartViewModel::class.java]
        getDeliveryDatesViewModel =
            ViewModelProviders.of(
                requireActivity(),
                viewModelFactory
            )[DeliveryDatesViewModel::class.java]
        initObservers()
//        }
    }

    override fun viewSetup() {
        Logger.log("onQuantityChange viewSetup Call >>>>>>>>>>>>")

        if (activity is HomeActivity) {
            (activity as HomeActivity).hideShowFooter(true)
            (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, true)
        } else if (activity is PaymentActivity) {
            (activity as PaymentActivity).hideShowFooter(true)
            (activity as PaymentActivity).showCartDetails(imgCart, txtCartProducts, true)
        } else if (activity is ProfileActivity) {
            (activity as ProfileActivity).hideShowFooter(true)
            (activity as ProfileActivity).showCartDetails(imgCart, txtCartProducts, true)
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext())
        imgMore.setOnClickListener(this@CartFragment)
        imgLogo.setOnClickListener(this@CartFragment)
        imgBell.setOnClickListener(this@CartFragment)
        imgSearch.setOnClickListener(this)
        txtContinueShopping.setOnClickListener(this@CartFragment)
        txtCalculateShipping.setOnClickListener(this@CartFragment)
        txtDeliveryDate.setOnClickListener(this@CartFragment)
        txtCheckout.setOnClickListener(this@CartFragment)
        viewCheckout.setOnClickListener(this@CartFragment)
        txtApplyCoupon.setOnClickListener(this@CartFragment)
        txtApplyCredit.setOnClickListener(this@CartFragment)
        imgCheckCnC.setOnClickListener(this@CartFragment)
        imgCheckCnCTorq.setOnClickListener(this@CartFragment)
        imgCheckDtD.setOnClickListener(this@CartFragment)
        txtUpdateTotal.setOnClickListener(this@CartFragment)
        shippingDetailsOpened = false
        spinnerCountry.onItemSelectedListener = this
        edtCredit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fillCredit = false
            }
        })
        cartViewModel?.getProducts()
        cartViewModel?.getCoupons()
        getDeliveryDatesViewModel?.getDeliveryDates(sharedPrefs.userType!!)
        var c = Calendar.getInstance()
        c.add(Calendar.DATE, sharedPrefs.deliveryMinDays)
        deliveryDate =
            Utility.getDateFromMillis(c.timeInMillis, Utility.DATE_FORMAT_D_MMMM_YYYY)
        /* Toast.makeText(
             requireContext(),
             "isFromCheckoutFragment= $isFromCheckoutFragment",
             Toast.LENGTH_LONG
         ).show()*/
        setData()
//        }
    }

    var deliveryDates: DeliveryDatesResponseModel? = null

    private fun initObservers() {
        getDeliveryDatesViewModel?.deliveryDatesLiveData?.observe(this, Observer {
            if (it != null) {
                var response: String = it
                response.let {
                    var jsonObject: JSONObject = JSONObject(response)
                    var data: JSONObject = jsonObject.getJSONObject("data")
                    var gson: Gson = Gson()
                    deliveryDates =
                        gson.fromJson(data.toString(), DeliveryDatesResponseModel::class.java)
                    if (arguments != null && requireArguments().containsKey("deliveryDateOfQuote")) {
                        quoteDate = requireArguments().getString("deliveryDateOfQuote").toString()
                        var strQuoteDateCompare: String = Utility.changeDateFormat(
                            quoteDate,
                            Utility.DATE_FORMAT_D_MMMM_YYYY,
                            Utility.DATE_FORMAT_YYYY_MM_DD
                        )
                        var setDate: Boolean = false
                        deliveryDates?.enable_date?.size?.let {
                            for (value in deliveryDates?.enable_date!!) {
                                if (value == strQuoteDateCompare) {
                                    setDate = true
                                    break
                                }
                            }
                            if (setDate) {
                                txtDeliveryDate.text = quoteDate
                            }
                        }
                    }
                }
            }
        })

        cartViewModel!!.productsLiveData.observe(this, Observer {
            if (it != null) {
                if (it.size > 0) {
                    if (cartProducts == null)
                        cartProducts = ArrayList()
                    cartProducts.clear()
                    cartProducts.addAll(it)
                    if (cartProducts.size > 0) {
                        txtCartProducts.text = "${cartProducts.size}"
                        txtCartProducts.visibility = View.VISIBLE
                    } else
                        txtCartProducts.visibility = View.GONE
                    setProductAdapter()
                    isOtherDataAvailable = !isOtherDataAvailable
                    if (!isOtherDataAvailable) {
                        Utility.showProgress(requireContext(), "", false)
                        cartViewModel?.getCartDetails(
                            cartProducts,
                            cartCoupons,
                            "",
                            shippingType,
                            getCountryCode(),
                            getPostalCode(),
                            getCredit()
                        )
                    }
                } else {
//                    shortToast(getString(R.string.no_product_in_cart_message))
                    (requireActivity() as AppCompatActivity).pushFragment(
                        ShopFragment(),
                        false,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                }
                cartViewModel!!._productsLiveData.value = null
            } else {
                Log.e("failure", "nnn")
            }
        })

        cartViewModel!!.couponsLiveData.observe(this, Observer {
            if (it != null) {
                if (cartCoupons == null)
                    cartCoupons = ArrayList()
                cartCoupons.clear()
                cartCoupons.addAll(it)
                setCouponAdapter()
                isOtherDataAvailable = !isOtherDataAvailable
                if (!isOtherDataAvailable) {
                    Utility.showProgress(requireContext(), "", false)
                    cartViewModel?.getCartDetails(
                        cartProducts,
                        cartCoupons,
                        "",
                        shippingType,
                        getCountryCode(),
                        getPostalCode(),
                        getCredit()
                    )
                }
                cartViewModel!!._couponsLiveData.value = null
            } else {
                Log.e("failure", "nnn1")
            }
        })

        cartViewModel!!.countryListLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                if (countries != null)
                    countries = ArrayList()
                countries.addAll(it)
                spinnerCountryAdapter = SpinnerCountryAdapter(requireActivity(), countries)
                spinnerCountry.adapter = spinnerCountryAdapter
                if (!shippingDetailsOpened) {
                    edtSuburb.requestFocus()
                    shippingDetailsOpened = !shippingDetailsOpened
                    groupShippingDetails.visibility =
                        if (shippingDetailsOpened) View.VISIBLE else View.GONE
                }
                cartViewModel!!._countryListLiveData.value = null
            } else {
                Log.e("failure", "nnn2")

            }
        })

        cartViewModel!!.cartDetailsLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                if (it.success) {
                    cartDetailsResponse = it.data
                    if (cartDetailsResponse?.user != null && cartDetailsResponse?.user?.credit != null)
                        sharedPrefs.availableCredit =
                            cartDetailsResponse?.user!!.credit!!.available_credit.toInt()
                    setData()
                    if (redirectToCheckout) {
                        redirectToCheckout = false
                        if (arguments != null && requireArguments().containsKey("note")) {
                            quoteNote = arguments?.getString("note").toString()
                        }
                        isFromCheckoutFragment = true
                        tmpDelDate = deliveryDate
                        //uncommented nikita's code
                        val items: ArrayList<Bundle> = ArrayList()
                        var bundle = Bundle()

                        for (i in 0 until cartProducts.size) {
                            val itemArray = Bundle()

/*nikita
                            val bundle = Bundle()
                            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, cartProducts[i]?.product_id.toString())
                            bundle.putString(FirebaseAnalytics.Param.VALUE, cartProducts[i]?.price.toString())
                            bundle.putString(FirebaseAnalytics.Param.QUANTITY, cartProducts[i]?.qty.toString())
                            bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, cartProducts[i]?.product_category_id.toString())
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, cartProducts[i]?.product_name)
                            items.add(bundle)
*/


                            val QuantityCart = cartProducts[i].qty.toString()
                            itemArray.putString(
                                FirebaseAnalytics.Param.ITEM_ID,
                                cartProducts[i].product_id.toString()
                            )
                            itemArray.putString(
                                FirebaseAnalytics.Param.PRICE,
                                cartProducts[i].price.toString()
                            )
                            if (QuantityCart != null && !QuantityCart.contentEquals("") && !QuantityCart.contentEquals(
                                    "null"
                                )
                            ) {
                                itemArray.putLong(
                                    FirebaseAnalytics.Param.QUANTITY,
                                    QuantityCart.toLong()
                                )

                            }
                            itemArray.putString(
                                FirebaseAnalytics.Param.ITEM_CATEGORY,
                                cartProducts[i].product_category_id.toString()
                            )
                            itemArray.putString(
                                FirebaseAnalytics.Param.ITEM_NAME,
                                cartProducts[i].product_name
                            )



                            items.add(itemArray)
                        }
                        val totalPrice = cartDetailsResponse!!.totalCartPrice.toString()
                        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD")

                        if (totalPrice != null && !totalPrice.contentEquals("") && !totalPrice.contentEquals(
                                "null"
                            )
                        ) {
                            bundle.putDouble(FirebaseAnalytics.Param.VALUE, totalPrice.toDouble())

                        }
                        bundle.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, items)
                        mFirebaseAnalytics!!.logEvent(
                            FirebaseAnalytics.Event.BEGIN_CHECKOUT,
                            bundle
                        )

/*Nikita
                        val bundle = Bundle()
                        bundle.putString(FirebaseAnalytics.Param.VALUE,cartDetailsResponse!!.totalCartPrice.toString())
                        bundle.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, items)
                        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.BEGIN_CHECKOUT, bundle)
*/

                        val itemJeggings = Bundle().apply {
                            putString(FirebaseAnalytics.Param.ITEM_ID, "SKU_123")
                            putString(FirebaseAnalytics.Param.ITEM_NAME, "jeggings")
                            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "pants")
                            putString(FirebaseAnalytics.Param.ITEM_VARIANT, "black")
                            putString(FirebaseAnalytics.Param.ITEM_BRAND, "Google")
                            putDouble(FirebaseAnalytics.Param.PRICE, 9.99)
                        }

                        val itemBoots = Bundle().apply {
                            putString(FirebaseAnalytics.Param.ITEM_ID, "SKU_456")
                            putString(FirebaseAnalytics.Param.ITEM_NAME, "boots")
                            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "shoes")
                            putString(FirebaseAnalytics.Param.ITEM_VARIANT, "brown")
                            putString(FirebaseAnalytics.Param.ITEM_BRAND, "Google")
                            putDouble(FirebaseAnalytics.Param.PRICE, 24.99)
                        }

                        val itemSocks = Bundle().apply {
                            putString(FirebaseAnalytics.Param.ITEM_ID, "SKU_789")
                            putString(FirebaseAnalytics.Param.ITEM_NAME, "ankle_socks")
                            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "socks")
                            putString(FirebaseAnalytics.Param.ITEM_VARIANT, "red")
                            putString(FirebaseAnalytics.Param.ITEM_BRAND, "Google")
                            putDouble(FirebaseAnalytics.Param.PRICE, 5.99)
                        }
                        val itemJeggingsCart = Bundle(itemJeggings).apply {
                            putLong(FirebaseAnalytics.Param.QUANTITY, 2)
                        }
                        val itemJeggingsWithIndex = Bundle(itemJeggings)
                        itemJeggingsWithIndex.putLong(FirebaseAnalytics.Param.INDEX, 1)
                        val itemBootsWithIndex = Bundle(itemBoots)
                        itemBootsWithIndex.putLong(FirebaseAnalytics.Param.INDEX, 2)
                        val itemSocksWithIndex = Bundle(itemSocks)
                        itemSocksWithIndex.putLong(FirebaseAnalytics.Param.INDEX, 3)
                        val bundle10 = Bundle()
                        bundle10.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                        bundle10.putDouble(
                            FirebaseAnalytics.Param.VALUE,
                            cartDetailsResponse!!.totalCartPrice.toDouble()
                        )                       // bundle10.putString(FirebaseAnalytics.Param.COUPON, "SUMMER_FUN")
                        /* bundle10.putParcelableArray(
                             FirebaseAnalytics.Param.ITEMS,
                             arrayOf<Parcelable>(
                                 itemJeggingsWithIndex,
                                 itemBootsWithIndex,
                                 itemSocksWithIndex
                             )
                         )*/
/*
                        val item1 = Bundle()
                        item1.putString(FirebaseAnalytics.Param.ITEM_ID, "ABCD123")
                        item1.putString(FirebaseAnalytics.Param.ITEM_NAME, "jeggings")
                        item1.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "pants")
                        val item2 = Bundle()
                        item2.putString(FirebaseAnalytics.Param.ITEM_ID, "1234")
                        item2.putString(FirebaseAnalytics.Param.ITEM_NAME, "boots")
                        item2.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "shoes")
                        val items7 = ArrayList<Bundle>()
                        items7.add(item1)
                        items7.add(item2)
                        bundle10.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, items7)
*/
//                        bundle10.putParcelableArray(FirebaseAnalytics.Param.ITEMS, arrayOf(itemJeggings))
/*Nikita
                        mFirebaseAnalytics!!.logEvent(
                            FirebaseAnalytics.Event.BEGIN_CHECKOUT,
                            bundle10
                        )
*/
                        (requireActivity() as AppCompatActivity).pushFragment(
                            CheckoutFragment(
                                getCouponCode(),
                                shippingType,
                                getCountryCode(),
                                getState(),
                                getSuburb(),
                                getPostalCode(),
                                getCredit(),
                                txtDeliveryDate.text.toString(),
                                quoteNote
                            ),
                            true,
                            true,
                            false,
                            R.id.flContainerHome
                        )
                    }
                } else {
                    redirectToCheckout = false
//                    shortToast(it.message)//nnn
                    Logger.log("Toast : " + it.message)
//                    longToast(it.message)//nnn
                    (requireActivity() as AppCompatActivity).showAlert(it.message.toString())
                    txtShippingCharges.text = "$0.0"
                    Html.fromHtml(
                        getString(
                            R.string.total_cost,
                            txtSubTotal.text
                        )
                    )
//                    cartDetailsResponse = null
                }
                isOtherDataAvailable = true
                cartViewModel!!._cartDetailsLiveData.value = null
            } else {
                Log.e("failure", "nnn4")
            }
        })

        cartViewModel!!.productLiveData.observe(this, Observer {
            if (addToCartProduct != null) {
                if (it != null) {
                    Logger.log("insert product response>>>>>>>>")
                    addToCartProduct!!.qty = addToCartProduct!!.qty + it.qty
                } else {
                    sharedPrefs.totalProductsInCart = sharedPrefs.totalProductsInCart + 1
                }
                shouldCallGetProducts = true
                cartViewModel!!.insertProduct(addToCartProduct!!, it != null)
                addToCartProduct = null
            }
        })

        cartViewModel!!.productInsertionLiveData.observe(this, Observer {
            if (shouldCallGetProducts) {
                if (it != null && it) {
                    Handler().postDelayed({
                        Utility.cancelProgress()
                        shortToast(
                            if (addToCartProduct == null) getString(R.string.product_successfully_added_msg) else getString(
                                R.string.product_successfully_updated_msg
                            )
                        )
                        cartViewModel!!.getProducts()
                        addToCartProduct = null
                        cartViewModel!!._productInsertionLiveData.value = null
                    }, 1000)
                } else {
                    if (addToCartProduct != null) {
                        shortToast(getString(R.string.product_updation_failed_msg))
                        addToCartProduct = null
                    }
                }
            }
        })

        cartViewModel!!.couponDeletionLiveData.observe(this, Observer {
            if (it != null && it) {
                shortToast(getString(R.string.coupon_deletion_success_message))
                cartViewModel!!._couponDeletionLiveData.value = null
                cartViewModel!!.getCoupons()
            } else {
                Log.e("failure", "nnn5")

            }
        })

        cartViewModel!!.productDeletionLiveData.observe(this, Observer {
            if (it != null && it) {
                shortToast(getString(R.string.product_deletion_success_message))
                sharedPrefs.totalProductsInCart = sharedPrefs.totalProductsInCart - 1
                cartViewModel!!._productDeletionLiveData.value = null
                isTurf = false
                qtyTurfForDTD = 0f
                cartViewModel!!.getProducts()

                cartViewModel!!._productDeletionLiveData.value = null
            } else {
                Log.e("failure", "nnn6")
            }
        })

        cartViewModel!!.deliveryDateValidationLiveData.observe(this, Observer {
            //            Utility.cancelProgress()
// {"Valid":0}
            if (it != null) {
                if (it) {
                    redirectToCheckout = true
                    cartViewModel?.getCartDetails(
                        cartProducts,
                        cartCoupons,
                        "",
                        shippingType,
                        getCountryCode(),
                        getPostalCode(),
                        getCredit()
                    )
                } else {
                    Utility.cancelProgress()
                    shortToast(
                        if (shippingType.equals(Constants.DELIVER_TO_DOOR)) getString(R.string.invalid_delivery_date_messgae) else getString(
                            R.string.invalid_pickup_date_messgae
                        )
                    )
                }
                cartViewModel!!._deliveryDateValidationLiveData.value = null
            } else {
                Log.e("failure", "nnn7")
            }
        })

        cartViewModel!!.errorLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                it.let { it1 ->

                    shortToast(it1)
                }
                cartViewModel!!._errorLiveData.value = null
            } else {
                Log.e("failure", "nnn8")
            }
        })
    }

    private fun setData() {

        Logger.log("set data after viewsetup>>>>>>>>")
        /* if (isFromCheckoutFragment){
             isFromCheckoutFragment = false
         }*/
        if (arguments != null && requireArguments().containsKey("deliveryDateOfQuote") && !isSetDRDFromQuote) {
            shippingType = Constants.DELIVER_TO_DOOR
            isSetDRDFromQuote = true
        }
        var isAnyRedeemable = false
        var totalQuantity = 0
        if (cartDetailsResponse != null) {
            Logger.log("cartDetailsResponse >>>>>>>")
            if (cartDetailsResponse!!.user != null) {
                allowedToPlaceOrder = cartDetailsResponse!!.user.allowedToPlaceOrder
            }
            if (cartDetailsResponse!!.products != null
                && cartDetailsResponse!!.products.size > 0
            ) {
                Logger.log("cartDetailsResponse products >>>>>>>")

                cartProducts.clear()
                var tmpMainListProductList: ArrayList<ProductDB> = ArrayList()
                cartDetailsResponse?.products?.let { tmpMainListProductList.addAll(it) }
                /* var tmpProductList: ArrayList<ProductDB> = ArrayList()
                 var tmpMainListProductList: ArrayList<ProductDB> = ArrayList()
                 cartDetailsResponse?.products?.let { tmpMainListProductList.addAll(it) }
                 for (i in 0 until cartDetailsResponse!!.products.size) {
                     if (cartDetailsResponse!!.products[i].free_product != 0 && (cartDetailsResponse!!.products[i]).free_products.product_id != 0 && cartDetailsResponse!!.products[i].qty >= cartDetailsResponse!!.products[i].free_product_qty)  {
                         var temProducts: ProductDB? = ProductDB()
                         temProducts!!.is_turf = 0
                         temProducts!!.price = 0f
                         temProducts!!.feature_img_url =
                             (cartDetailsResponse!!.products[i]).free_products.feature_image_url
                         temProducts!!.product_id =
                             (cartDetailsResponse!!.products[i]).free_products.id
                         temProducts!!.qty =
                             (cartDetailsResponse!!.products[i]).free_products.qty
                         temProducts!!.product_name =
                             (cartDetailsResponse!!.products[i]).free_products.productName
                         temProducts!!.free_products.productName = "free"
                         tmpProductList.add(temProducts)
                     }
                 }
                 if (tmpProductList.size > 0) {
                     tmpMainListProductList.addAll(tmpProductList)
                 }*/

                qtyTurfForDTD = 0f
                for (i in 0 until tmpMainListProductList.size) {
                    cartProducts.add(tmpMainListProductList[i])
                    isTurf = ((tmpMainListProductList[i].is_turf == 1) || isTurf)
                    if (tmpMainListProductList[i].is_turf == 1) qtyTurfForDTD += tmpMainListProductList[i].qty
                    if (tmpMainListProductList[i] != null && tmpMainListProductList[i].product_redeemable_against_credit) {
                        isAnyRedeemable = true
                        totalQuantity += (tmpMainListProductList[i]).qty
                    }
                    shouldCallGetProducts = false
                    if (tmpMainListProductList[i] != null)
                        cartViewModel?.insertProduct((tmpMainListProductList[i]), true)
                }

                /*Nirali changes*/
                var isDeliveryToDoor = false

                if (isTurf && qtyTurfForDTD < 25) {
                    isDeliveryToDoor = false
                } else if (isTurf && qtyTurfForDTD >= 25) {
                    isDeliveryToDoor = true
                } else {
                    isDeliveryToDoor = true
                }


                // setDeliveryToDoorVisibility(qtyTurfForDTD >= 25)
                setDeliveryToDoorVisibility(isDeliveryToDoor)

                Logger.log("nnn isTurf->" + isTurf)
            }
//            grpAdminFee.visibility = if (isTurf) View.GONE else View.VISIBLE //nnn
            if (cartDetailsResponse!!.coupons != null && cartDetailsResponse!!.coupons.size > 0 && cartDetailsResponse!!.coupons.size > cartCoupons.size) {
                edtCouponCode.setText("")
                for (i in 0 until cartDetailsResponse!!.coupons.size) {
                    if (cartDetailsResponse!!.coupons[i] != null && !containsCoupon(
                            cartDetailsResponse!!.coupons[i]
                        )
                    ) {
                        cartCoupons.add(cartDetailsResponse!!.coupons[i])
                        cartViewModel?.insertCoupon(cartDetailsResponse!!.coupons[i])
                    }
                }
            } else {
                if (!Utility.isValueNull(edtCouponCode.text.toString())) {
                    shortToast(getString(R.string.invalid_coupon_msg))
                    edtCouponCode.setText("")
                }
            }

            setCouponAdapter()
            txtDeliveryDateInfo.text = sharedPrefs.deliveryMessage

            if (sharedPrefs.isLogged && sharedPrefs.userType != Constants.RETAILER && isAnyRedeemable) {
                Logger.log("isAnyRedeemable >>>>>>>")

                edtCredit.visibility = View.VISIBLE
                txtApplyCredit.visibility = View.VISIBLE
                if (fillCredit) {
                    Logger.log("fillCredit >>>>>>>")
                    Logger.log("totalQuantity >>>>" + totalQuantity)
                    Logger.log("availableCredit >>>>" + sharedPrefs.availableCredit)

                    fillCredit = false
                    if (totalQuantity >= sharedPrefs.availableCredit) {

                        edtCredit.setText(sharedPrefs.availableCredit.toString())

                        Logger.log("fill credit if>>>>>>>" + sharedPrefs.availableCredit.toString())
                    } else {
                        edtCredit.setText(totalQuantity.toString())
                        Logger.log("fill credit else >>>>>>>" + totalQuantity.toString())

                    }

                }
/*
                edtCredit.filters = arrayOf<InputFilter>(
                    InputFilterMinMax(
                        0,
                        if (totalQuantity >= sharedPrefs.availableCredit) {
                            sharedPrefs.availableCredit
                        } else {
                            totalQuantity
                        }
                    )
                )
*/
            } else {
                Logger.log("isAnyRedeemable else >>>>>>>")

                edtCredit.visibility = View.GONE
                txtApplyCredit.visibility = View.GONE
            }
            setProductAdapter()
            if (cartDetailsResponse!!.subTotalPrice > 0) {
                txtSubTotal.text =
                    "$${
                        Utility.formatNumber(
                            Utility.roundTwoDecimals(cartDetailsResponse!!.subTotalPrice).toFloat()
                        )
                    }"
            } else {
                txtSubTotal.text = "$0.0"
            }
            if (cartDetailsResponse!!.totalDiscount > 0) {
                txtDiscount.text =
                    "$${
                        Utility.formatNumber(
                            Utility.roundTwoDecimals(cartDetailsResponse!!.totalDiscount).toFloat()

                        )
                    }"
            } else {
                txtDiscount.text = "$0.0"
            }

            if (cartDetailsResponse!!.creditDiscount > 0) {
                txtCredit.text =
                    "$${
                        Utility.formatNumber(
                            Utility.roundTwoDecimals(cartDetailsResponse!!.creditDiscount).toFloat()
                        )
                    }"
            } else {
                txtCredit.text = "$0.0"
            }
            if (cartDetailsResponse!!.shippingPrice > 0) {
                txtShippingCharges.text =
                    "$${
                        Utility.formatNumber(
                            Utility.roundTwoDecimals(cartDetailsResponse!!.shippingPrice).toFloat()
                        )
                    }"
            } else {
                txtShippingCharges.text = "$0.0"
            }

//            test1234@yopmil.com / test@123
            //set Data for Admin fee
            if (cartDetailsResponse!!.totalCartPrice > 0) {
                /* if (!isTurf) {
                     adminFee =(cartDetailsResponse!!.totalCartPrice * (sharedPrefs.adminFeeNonTurf).toFloat())/100
                     txtAdminFee.text = adminFee.toString()
                 }*/  //nnn
                if (isTurf) {
                    txtTotalCost.text =
                        Html.fromHtml(
                            getString(
                                R.string.total_cost,
                                Utility.formatNumber(
                                    Utility.roundTwoDecimals(cartDetailsResponse!!.totalCartPrice)
                                        .toFloat()
                                )
                            )
                        )
                } else {


                    txtTotalCost.text =
                        Html.fromHtml(
                            getString(
                                R.string.total_cost,
                                Utility.formatNumber(
                                    (
                                            Utility.roundTwoDecimals(cartDetailsResponse!!.totalCartPrice)
                                                .toFloat()
                                            )
                                )
                            )
                        )

                }
            } else {
                txtTotalCost.text = Html.fromHtml(getString(R.string.total_cost, "0"))
            }
            setRelatedProductsAdapter()
        }
        if (shippingType.equals(Constants.CLICK_AND_COLLECT)) {
            imgCheckDtD.setImageResource(R.drawable.ic_checkbox)
            imgCheckCnC.setImageResource(R.drawable.ic_checkbox_h)
            imgCheckCnCTorq.setImageResource(R.drawable.ic_checkbox)

        } else if (shippingType.equals(Constants.CLICK_AND_COLLECT_TORQUAY)) {
            imgCheckCnC.setImageResource(R.drawable.ic_checkbox)
            imgCheckDtD.setImageResource(R.drawable.ic_checkbox)
            imgCheckCnCTorq.setImageResource(R.drawable.ic_checkbox_h)
        } else if (shippingType.equals(Constants.DELIVER_TO_DOOR)) {
            imgCheckCnCTorq.setImageResource(R.drawable.ic_checkbox)
            imgCheckCnC.setImageResource(R.drawable.ic_checkbox)
            imgCheckDtD.setImageResource(R.drawable.ic_checkbox_h)
            if (quoteDate != "") { //Dev_N openfromquote to set
                if (!shippingDetailsOpened) {
                    shippingDetailsOpened = !shippingDetailsOpened
                    groupShippingDetails.visibility =
                        if (shippingDetailsOpened) View.VISIBLE else View.GONE
                    cartViewModel?.getCountries()
                }
                txtDeliveryDateLabel.text = getString(R.string.deliverydate)
            }
        }
        /*if (!Utility.isValueNull(deliveryDate) && Utility.isValueNull(txtDeliveryDate.text.toString()))
            txtDeliveryDate.text = deliveryDate*/
        if (isFromCheckoutFragment) {
            isFromCheckoutFragment = false
            deliveryDate = tmpDelDate
            txtDeliveryDate.text = deliveryDate
        }
    }

    private fun setDeliveryToDoorVisibility(isVisible: Boolean) {
        imgCheckDtD.visibility = if (isVisible) View.VISIBLE else View.GONE
        txtDeliverToDoor.visibility = if (isVisible) View.VISIBLE else View.GONE
        txtCalculationLabel.visibility = if (isVisible) View.VISIBLE else View.GONE
        txtCalculateShipping.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun containsCoupon(couponDB: CouponDB): Boolean {
        for (i in 0 until cartCoupons.size) {
            if (cartCoupons[i].id == couponDB.id) {
                return true
            }
        }
        return false
    }

    private fun setProductAdapter() {
        if (cartProducts.size > 0) {
            cartProductsAdapter = CartProductsAdapter(
                requireActivity() as AppCompatActivity,
                cartProducts,
                true,
                object : OnCartProductChangeListener {
                    override fun onItemDelete(product: ProductDB) {
                        cartViewModel?.deleteProduct(product)
                        fillCredit = true

                    }

                    override fun onQuantityChange(product: ProductDB) {
                        Logger.log("onQuantityChange Call >>>>>>>>>>>>")
                        shouldCallGetProducts = true
                        cartViewModel?.insertProduct(product, true)
                        fillCredit = true

                    }
                })
            listProducts.adapter = cartProductsAdapter
            listProducts.visibility = View.VISIBLE
        } else {
            listProducts.visibility = View.GONE
        }
    }

    private fun setCouponAdapter() {
        if (cartCoupons != null) {
            cartCouponsAdapter = CartCouponsAdapter(
                requireActivity() as AppCompatActivity,
                cartCoupons,
                object : OnCartCouponDeleteListener {
                    override fun onItemDelete(couponDB: CouponDB) {
                        cartViewModel?.deleteCoupon(couponDB)
                    }
                })
            listCoupons.adapter = cartCouponsAdapter
        }
    }

    private fun setRelatedProductsAdapter() {
        if (cartDetailsResponse != null && cartDetailsResponse!!.relatedProducts != null && cartDetailsResponse!!.relatedProducts.size > 0) {
            var products: ArrayList<RelatedProductsResponse.Data> = ArrayList()
            products.addAll(cartDetailsResponse!!.relatedProducts)
            listRelatedProducts.visibility = View.VISIBLE
            productsAdapter = activity?.let {
                RelatedProductsAdapter(
                    it as AppCompatActivity,
                    products,
                    2,
                    object : OnProductAddedToCart {
                        override fun OnProductAddedToCart(product: ProductDetailResponse) {
                            addToCartProduct = product
                            Log.d("trace", "====================== ${product}")
                            Utility.showProgress(requireContext(), "", false)
                            cartViewModel?.getProductById(product.id)
                        }
                    }
                )
            }!!
            listRelatedProducts.adapter = productsAdapter
            listRelatedProducts.visibility = View.VISIBLE
        } else {
            listRelatedProducts.visibility = View.GONE
        }
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {
            R.id.imgBell -> {
                openNotificationFragment()
            }
            R.id.txtContinueShopping -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ShopFragment(),
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.txtCalculateShipping -> {
                if (!shippingType.equals(Constants.DELIVER_TO_DOOR)) {
                    shippingType = Constants.DELIVER_TO_DOOR
                    imgCheckDtD.setImageResource(R.drawable.ic_checkbox_h)
                    imgCheckCnC.setImageResource(R.drawable.ic_checkbox)
                    imgCheckCnCTorq.setImageResource(R.drawable.ic_checkbox)
                }
                if (shippingDetailsOpened) {
                    shippingDetailsOpened = !shippingDetailsOpened
                    groupShippingDetails.visibility =
                        if (shippingDetailsOpened) View.VISIBLE else View.GONE
                } else {
                    Utility.showProgress(requireContext(), "", false)
                    cartViewModel?.getCountries()
                }
            }

            R.id.imgLogo -> {
//                (requireActivity() as AppCompatActivity).hideKeyboard()
                if (activity is HomeActivity) {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        HomeFragment(),
                        false,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                } else {
                    requireActivity().startActivity(
                        Intent(
                            requireActivity(),
                            HomeActivity::class.java
                        )
                    )
                    requireActivity().finishAffinity()
                }
            }

            R.id.imgMore -> {
//                (requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    ContactFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.txtDeliveryDate -> {
                openDatePickerDialog()
            }


            R.id.imgSearch -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    SearchFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgCheckDtD -> {
                if (shippingType.equals(Constants.DELIVER_TO_DOOR)) {

                    openClickAndCollectDialog()
                } else {
                    shippingType = Constants.DELIVER_TO_DOOR
                    imgCheckDtD.setImageResource(R.drawable.ic_checkbox_h)
                    imgCheckCnC.setImageResource(R.drawable.ic_checkbox)
                    imgCheckCnCTorq.setImageResource(R.drawable.ic_checkbox)
                    if (!shippingDetailsOpened) {
                        edtSuburb.requestFocus()
                        shippingDetailsOpened = !shippingDetailsOpened
                        groupShippingDetails.visibility =
                            if (shippingDetailsOpened) View.VISIBLE else View.GONE
                        cartViewModel?.getCountries()
                    }
                    txtDeliveryDateLabel.text = getString(R.string.deliverydate)
                    Utility.showProgress(requireContext(), "", false)
                    cartViewModel?.getCartDetails(
                        cartProducts,
                        cartCoupons,
                        "",
                        shippingType,
                        getCountryCode(),
                        getPostalCode(),
                        getCredit()
                    )
                }

            }

            R.id.imgCheckCnC -> {
                if (shippingType.equals(Constants.CLICK_AND_COLLECT)) {
                    if (qtyTurfForDTD >= 25) {
                        shippingType = Constants.DELIVER_TO_DOOR
                        imgCheckCnC.setImageResource(R.drawable.ic_checkbox)
                        imgCheckDtD.setImageResource(R.drawable.ic_checkbox_h)
                        imgCheckCnCTorq.setImageResource(R.drawable.ic_checkbox)
                        if (!shippingDetailsOpened) {
                            shippingDetailsOpened = !shippingDetailsOpened
                            groupShippingDetails.visibility =
                                if (shippingDetailsOpened) View.VISIBLE else View.GONE
                            cartViewModel?.getCountries()
                        }
                        txtDeliveryDateLabel.text = getString(R.string.deliverydate)
                        Utility.showProgress(requireContext(), "", false)
                        cartViewModel?.getCartDetails(
                            cartProducts,
                            cartCoupons,
                            "",
                            shippingType,
                            getCountryCode(),
                            getPostalCode(),
                            getCredit()
                        )
                    } else {
                        shippingType = ""
                        imgCheckCnC.setImageResource(R.drawable.ic_checkbox)
                    }

                } else {

                    openClickAndCollectDialog()
                }


            }
            R.id.imgCheckCnCTorq -> {
                if (shippingType.equals(Constants.CLICK_AND_COLLECT_TORQUAY)) {
                    if (qtyTurfForDTD >= 25) {
                        shippingType = Constants.DELIVER_TO_DOOR
                        imgCheckCnC.setImageResource(R.drawable.ic_checkbox)
                        imgCheckCnCTorq.setImageResource(R.drawable.ic_checkbox)
                        imgCheckDtD.setImageResource(R.drawable.ic_checkbox_h)
                        if (!shippingDetailsOpened) {
                            shippingDetailsOpened = !shippingDetailsOpened
                            groupShippingDetails.visibility =
                                if (shippingDetailsOpened) View.VISIBLE else View.GONE
                            cartViewModel?.getCountries()
                        }
                        txtDeliveryDateLabel.text = getString(R.string.deliverydate)
                        Utility.showProgress(requireContext(), "", false)
                        cartViewModel?.getCartDetails(
                            cartProducts,
                            cartCoupons,
                            "",
                            shippingType,
                            getCountryCode(),
                            getPostalCode(),
                            getCredit()
                        )
                    } else {
                        shippingType = ""
                        imgCheckCnCTorq.setImageResource(R.drawable.ic_checkbox)
                    }

                } else {

                    openClickAndCollectTorqualyDialog()
                }


            }

            R.id.txtUpdateTotal -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                Utility.showProgress(requireContext(), "", false)
                cartViewModel?.getCartDetails(
                    cartProducts,
                    cartCoupons,
                    "",
                    shippingType,
                    getCountryCode(),
                    getPostalCode(),
                    getCredit()
                )
            }

            R.id.txtCheckout, R.id.viewCheckout -> {

                if (cartDetailsResponse != null) {
                    if (cartDetailsResponse!!.user == null || allowedToPlaceOrder == 1) {
                        if (!(cartProducts != null && cartProducts.size > 0)) {
                            shortToast(getString(R.string.no_product_message))
                        } else if (Utility.isValueNull(shippingType)) {
                            shortToast(getString(R.string.no_shipping_type_message))
                        } else if (shippingType.equals(Constants.DELIVER_TO_DOOR) && spinnerCountry.selectedItem == null) {
                            shortToast(getString(R.string.no_country_message))
                        } else if (shippingType.equals(Constants.DELIVER_TO_DOOR) && Utility.isValueNull(
                                edtSuburb.text.toString()
                            ) && spinnerState.selectedItem == null
                        ) {
                            shortToast(getString(R.string.no_state_message))
//                    } else if (shippingType.equals(Constants.DELIVER_TO_DOOR) && Utility.isValueNull(edtSuburb.text.toString())) {
//                        shortToast(getString(R.string.blank_suburb_message))
                        } else if (shippingType.equals(Constants.DELIVER_TO_DOOR) && Utility.isValueNull(
                                edtPostcode.text.toString()
                            )
                        ) {
                            shortToast(getString(R.string.blank_postal_code_message))
                        } else if (Utility.isValueNull(txtDeliveryDate.text.toString())) {
                            if (txtDeliveryDateLabel.text.toString()
                                    .trim() == getString(R.string.pickupdate)
                            ) {
                                shortToast(getString(R.string.no_pickup_date_message))
                                txtDeliveryDateLabel.text.equals("")
                            } else {
                                shortToast(getString(R.string.no_delivery_date_message))
                            }
                        } else {
                            Utility.showProgress(requireContext(), "", false)
                            cartViewModel?.deliveryDAteValidation(
                                Utility.changeDateFormat(
                                    deliveryDate,
                                    Utility.DATE_FORMAT_D_MMMM_YYYY,
                                    Utility.DATE_FORMAT_YYYY_MM_DD
                                )
                            )
                        }
                    } else
                        shortToast(getString(R.string.not_allowed_to_place_order))
                }
            }

            R.id.txtApplyCoupon -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                if (!Utility.isValueNull(edtCouponCode.text.toString())) {
                    if (!isCouponAlreadyAdded(edtCouponCode.text.toString())) {
                        Utility.showProgress(requireContext(), "", false)
                        cartViewModel?.getCartDetails(
                            cartProducts,
                            cartCoupons,
                            getCouponCode(),
                            shippingType,
                            getCountryCode(),
                            getPostalCode(),
                            getCredit()
                        )
                    } else {
                        shortToast("You have already added this offer to your cart.")
                    }
                }
            }

            R.id.txtApplyCredit -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                if (!Utility.isValueNull(edtCredit.text.toString())) {
                    isCreditApplied = true
                    Utility.showProgress(requireContext(), "", false)
                    cartViewModel?.getCartDetails(
                        cartProducts,
                        cartCoupons,
                        "",
                        shippingType,
                        getCountryCode(),
                        getPostalCode(),
                        getCredit()
                    )
                }
            }
        }
    }

    private fun setAfterClickAndCollectDialogAccept() {
        shippingType = Constants.CLICK_AND_COLLECT
        imgCheckCnC.setImageResource(R.drawable.ic_checkbox_h)
        imgCheckDtD.setImageResource(R.drawable.ic_checkbox)
        imgCheckCnCTorq.setImageResource(R.drawable.ic_checkbox)
        if (shippingDetailsOpened) {
            shippingDetailsOpened = !shippingDetailsOpened
            groupShippingDetails.visibility =
                if (shippingDetailsOpened) View.VISIBLE else View.GONE
        }
        txtDeliveryDateLabel.text = getString(R.string.pickupdate)
        Utility.showProgress(requireContext(), "", false)
        cartViewModel?.getCartDetails(
            cartProducts,
            cartCoupons,
            "",
            shippingType,
            getCountryCode(),
            getPostalCode(),
            getCredit()
        )
    }

    private fun setAfterClickAndCollectTorquayDialogAccept() {
        shippingType = Constants.CLICK_AND_COLLECT_TORQUAY
        imgCheckCnCTorq.setImageResource(R.drawable.ic_checkbox_h)
        imgCheckDtD.setImageResource(R.drawable.ic_checkbox)
        imgCheckCnC.setImageResource(R.drawable.ic_checkbox)
        if (shippingDetailsOpened) {
            shippingDetailsOpened = !shippingDetailsOpened
            groupShippingDetails.visibility =
                if (shippingDetailsOpened) View.VISIBLE else View.GONE
        }
        txtDeliveryDateLabel.text = getString(R.string.pickupdate)
        Utility.showProgress(requireContext(), "", false)
        cartViewModel?.getCartDetails(
            cartProducts,
            cartCoupons,
            "",
            shippingType,
            getCountryCode(),
            getPostalCode(),
            getCredit()
        )
    }

    private fun openClickAndCollectDialog() {
        dialogDropTurfOnProperty = Dialog(requireContext())
        dialogDropTurfOnProperty?.setCancelable(false)
        dialogDropTurfOnProperty?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogDropTurfOnProperty?.setContentView(R.layout.dialog_drop_turf_property)
        dialogDropTurfOnProperty?.show()

        val window = dialogDropTurfOnProperty?.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)

        val txtTitle = dialogDropTurfOnProperty?.findViewById(R.id.txtTitle) as AppCompatTextView
        txtTitle.text = sharedPrefs.clickAndCollectCheckboxTitle
        val txtDropTurfDescription =
            dialogDropTurfOnProperty?.findViewById(R.id.txtDropTurfDescription) as AppCompatTextView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtDropTurfDescription.text = Html.fromHtml(
                sharedPrefs.clickAndCollectCheckboxDescription,
                Html.FROM_HTML_MODE_LEGACY
            )
        } else {
            txtDropTurfDescription.text =
                Html.fromHtml(sharedPrefs.clickAndCollectCheckboxDescription)
        }


        val txtConfirm = dialogDropTurfOnProperty?.findViewById(R.id.txtConfirm) as TextView
        txtConfirm.setOnClickListener {
//            chkDropTurfProperty.isChecked = true
            setAfterClickAndCollectDialogAccept()
            dialogDropTurfOnProperty?.dismiss()
        }

        /*    val imgClose = dialogDropTurfOnProperty?.findViewById(R.id.imgClose) as ImageView
            imgClose.setOnClickListener {
                dialogDropTurfOnProperty?.dismiss()
            }
    */
        val txtCancel = dialogDropTurfOnProperty?.findViewById(R.id.txtCancel) as TextView
        txtCancel.setOnClickListener {
//            chkDropTurfProperty.isChecked = false
            dialogDropTurfOnProperty?.dismiss()
        }
    }

    private fun openClickAndCollectTorqualyDialog() {
        dialogDropTurfOnProperty = Dialog(requireContext())
        dialogDropTurfOnProperty?.setCancelable(false)
        dialogDropTurfOnProperty?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogDropTurfOnProperty?.setContentView(R.layout.dialog_drop_turf_property)
        dialogDropTurfOnProperty?.show()

        val window = dialogDropTurfOnProperty?.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)

        val txtTitle = dialogDropTurfOnProperty?.findViewById(R.id.txtTitle) as AppCompatTextView
        txtTitle.text = sharedPrefs.clickAndCollectCheckboxTitle
        val txtDropTurfDescription =
            dialogDropTurfOnProperty?.findViewById(R.id.txtDropTurfDescription) as AppCompatTextView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtDropTurfDescription.text = Html.fromHtml(
                sharedPrefs.clickAndCollectCheckboxTorquayDescription,
                Html.FROM_HTML_MODE_LEGACY
            )
        } else {
            txtDropTurfDescription.text =
                Html.fromHtml(sharedPrefs.clickAndCollectCheckboxTorquayDescription)
        }

        val txtConfirm = dialogDropTurfOnProperty?.findViewById(R.id.txtConfirm) as TextView
        txtConfirm.setOnClickListener {
            setAfterClickAndCollectTorquayDialogAccept()
            dialogDropTurfOnProperty?.dismiss()
        }

        val txtCancel = dialogDropTurfOnProperty?.findViewById(R.id.txtCancel) as TextView
        txtCancel.setOnClickListener {
            dialogDropTurfOnProperty?.dismiss()
        }
    }

    private fun isCouponAlreadyAdded(couponCode: String): Boolean {
        if (cartCoupons != null && cartCoupons.size > 0) {
            for (i in 0 until cartCoupons.size) {
                if (cartCoupons[i].code.equals(couponCode, true))
                    return true
            }
        }
        return false
    }

    private fun openDatePickerDialog() {
        val c = Calendar.getInstance()
        dpd = DatePickerDialog.newInstance({ view, year, monthOfYear, dayOfMonth ->
            deliveryDate =
                Utility.changeDateFormat(
                    "" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year,
                    Utility.DATE_FORMAT_D_M_YYYY,
                    Utility.DATE_FORMAT_D_MMMM_YYYY
                )
//            Utility.showProgress(requireContext(), "", false)
//            cartViewModel?.deliveryDAteValidation(
//                Utility.changeDateFormat(
//                    "" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year,
//                    Utility.DATE_FORMAT_D_M_YYYY,
//                    Utility.DATE_FORMAT_YYYY_MM_DD
//                )
//            )

            txtDeliveryDate.text = deliveryDate
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

        //dpd?.minDate = c
        var calendarMinDate = Calendar.getInstance()
        calendarMinDate.time = SimpleDateFormat("yyyy-MM-dd").parse(deliveryDates?.min_date)

        var calendarMaxDate = Calendar.getInstance()
        calendarMaxDate.time = SimpleDateFormat("yyyy-MM-dd").parse(deliveryDates?.max_date)

        dpd?.minDate = calendarMinDate
        dpd?.maxDate = calendarMaxDate


        //Disable all SUNDAYS and SATURDAYS between Min and Max Dates
        var loopdate = calendarMinDate

        deliveryDates?.disable_date?.size?.let {
            for (value in deliveryDates?.disable_date!!) {
                var calendars = arrayOfNulls<Calendar>(1)
                calendars[0] = Calendar.getInstance()
                calendars[0]?.time = SimpleDateFormat("yyyy-MM-dd").parse(value)
                dpd?.disabledDays = calendars
            }
        }


        dpd?.show(requireActivity().supportFragmentManager, "")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (countries[position].countryCode.equals("AU")) {
            spinnerState.visibility = View.VISIBLE
            edtState.visibility = View.GONE
            if (states == null)
                states = ArrayList()
            states.clear()
            states.addAll(countries[position].states)
            spinnerStateAdapter = SpinnerStateAdapter(requireActivity(), states)
            spinnerState.adapter = spinnerStateAdapter
        } else {
            spinnerState.visibility = View.GONE
            edtState.visibility = View.VISIBLE
        }
        Utility.showProgress(requireContext(), "", false)
        cartViewModel?.getCartDetails(
            cartProducts,
            cartCoupons,
            "",
            shippingType,
            getCountryCode(),
            getPostalCode(),
            getCredit()
        )
    }

    private fun getCountryCode(): String {
        if (spinnerCountry.selectedItemPosition != -1) {
            return (spinnerCountry.selectedItem as CountryResponse).countryCode
        }
        return ""
    }

    private fun getState(): String {
        if (spinnerState.selectedItemPosition != -1) {
            if (countries[spinnerCountry.selectedItemPosition].countryCode.equals("AU")) {
                return (spinnerState.selectedItem as CountryResponse.State).stateName
            } else if (edtState.text != null && !Utility.isValueNull(edtState.text.toString())) {
                return edtState.text.toString()
            }
        }
        return ""
    }

    private fun getSuburb(): String {
        if (edtSuburb.text != null && !Utility.isValueNull(edtSuburb.text.toString())) {
            return edtSuburb.text.toString()
        }
        return ""
    }

    private fun getPostalCode(): String {
        if (edtPostcode.text != null && !Utility.isValueNull(edtPostcode.text.toString())) {
            if (sharedPrefs.userType != Constants.GUEST_USER) {
                sharedPrefs.postalCode = edtPostcode.text.toString().toInt()
            }
            return edtPostcode.text.toString()
        }
        return ""
    }

    private fun getCredit(): String {
        if (isCreditApplied && edtCredit.text != null && !Utility.isValueNull(edtCredit.text.toString())) {
            return edtCredit.text.toString()
        }
        return ""
    }

    private fun getCouponCode(): String {
        if (edtCouponCode.text != null && !Utility.isValueNull(edtCouponCode.text.toString())) {
            return edtCouponCode.text.toString()
        }
        return ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cartViewModel!!._productInsertionLiveData.value = null
    }
}
