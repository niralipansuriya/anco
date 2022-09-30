package com.app.ancoturf.presentation.cart

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.*
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.BuildConfig
import com.app.ancoturf.R
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.cart.FreeProductAPI
import com.app.ancoturf.data.cart.persistance.entity.CouponDB
import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.data.cart.remote.entity.*
import com.app.ancoturf.data.common.ApiCallback
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.common.network.BaseService
import com.app.ancoturf.data.payment.remote.PaymentService
import com.app.ancoturf.extension.*
import com.app.ancoturf.presentation.cart.adapters.CartProductsAdapter
import com.app.ancoturf.presentation.cart.adapters.PaymentMethodsAdapter
import com.app.ancoturf.presentation.cart.adapters.SpinnerCountryAdapter
import com.app.ancoturf.presentation.cart.adapters.SpinnerStateAdapter
import com.app.ancoturf.presentation.cart.interfaces.OnCartProductChangeListener
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.order.OrderDetailsFragment
import com.app.ancoturf.presentation.home.order.OrderFragment
import com.app.ancoturf.presentation.payment.CardNumberValidate
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.payment.westpac.WestpacPayment
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.presentation.signup.SignUpActivity
import com.app.ancoturf.utils.Logger
import com.app.ancoturf.utils.Utility
import com.app.ancoturf.utils.interfces.OnDialogButtonClick
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.whiteelephant.monthpicker.MonthPickerDialog
import kotlinx.android.synthetic.main.fragment_checkout.*
import kotlinx.android.synthetic.main.header.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CheckoutFragment(
    private var couponCode: String?,
    private var shippingType: String,
    private var shippingCountryCode: String?,
    private var shippingState: String?,
    private var shippingCity: String?,
    private var shippingPostalCode: String?,
    private var credit: String?,
    private var deliveryDate: String,
    private var quoteNote: String
) :
    BaseFragment(),
    View.OnClickListener, AdapterView.OnItemSelectedListener, ApiCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var cartViewModel: CartViewModel? = null

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    var agreedToTNC = false
    override fun getContentResource(): Int = R.layout.fragment_checkout
    var cartProductsAdapter: CartProductsAdapter? = null
    var cartProducts = ArrayList<ProductDB>()
    var cartCoupons = ArrayList<CouponDB>()

    private var spinnerCountryAdapter: SpinnerCountryAdapter? = null
    var countries = ArrayList<CountryResponse>()

    private var paymentMethodsAdapter: PaymentMethodsAdapter? = null
    var paymentMethods = ArrayList<PaymentMethodsResponse.PaymentMethod>()

    private var spinnerStateAdapter: SpinnerStateAdapter? = null
    var states = ArrayList<CountryResponse.State>()

    private var cartDetailsResponse: CartDetailsResponse? = null
    private var orderDetails: OrderDetails? = null
    private var paymentMethodsResponse: PaymentMethodsResponse? = null

    private var showEmailDetail = false
    private var showShipping = false
    private var showShippingDetail = false
    private var showReviewNPurchase = false
    private var showReviewNPurchaseDetail = false
    private var showPayment = false
    private var showPaymentDetail = false
    private var isOtherDataAvailable = false
    private var _context: Context? = null
    private var dialogDropTurfOnProperty: Dialog? = null
    private var isTurf: Boolean = false
    private var adminFee: Float = 0f
    private var freeProduct: ArrayList<FreeProductAPI> = ArrayList()
    private var isSetUserNameAsDefault: Boolean = false
    private var mFirebaseAnalytics: FirebaseAnalytics? = null


    override fun onResume() {
        super.onResume()

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        _context = context
    }

    override fun onPause() {
        super.onPause()
    }



    override fun viewModelSetup() {
        cartViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[CartViewModel::class.java]
        initObservers()
    }

    override fun viewSetup() {

        /*val filter = IntentFilter()
        filter.addAction(Constants.GUEST_USER_TO_LOGIN_VIA_CHECKOUT_ACTION)
        _context?.registerReceiver(broadcastReceiver, filter)*/





        Logger.log("Activity name : " + activity?.javaClass?.simpleName)

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

        imgMore.setOnClickListener(this@CheckoutFragment)
        imgLogo.setOnClickListener(this@CheckoutFragment)
        imgBell.setOnClickListener(this@CheckoutFragment)
        imgSearch.setOnClickListener(this)
        imgCart.setOnClickListener(this)

        if(shippingType.equals(Constants.DELIVER_TO_DOOR))
        {
            txtDeliveryDateLabel.text = getString(R.string.delivery_date_label)
        }
        else{
            txtDeliveryDateLabel.text = getString(R.string.pickupdate)

        }

        layoutEmailLabel.setOnClickListener(this@CheckoutFragment)
        layoutShippingLabel.setOnClickListener(this@CheckoutFragment)
        layoutReviewNPurchaseLabel.setOnClickListener(this@CheckoutFragment)
        layoutPaymentLabel.setOnClickListener(this@CheckoutFragment)

        txtContinueEmail.setOnClickListener(this@CheckoutFragment)
        txtContinueShipping.setOnClickListener(this@CheckoutFragment)
        txtContinueRnP.setOnClickListener(this@CheckoutFragment)
        txtProceed.setOnClickListener(this@CheckoutFragment)
        txtPaymentTnC.setOnClickListener(this@CheckoutFragment)
        imgCheckPaymentTnC.setOnClickListener(this@CheckoutFragment)
        imgBack.setOnClickListener(this@CheckoutFragment)

        btnSave.setOnClickListener(this@CheckoutFragment)
        tvExpDate.setOnClickListener(this@CheckoutFragment)

        spinnerCountry.onItemSelectedListener = this

//        setAdapter()
        cartViewModel?.getProducts()
        cartViewModel?.getCoupons()
        cartViewModel?.getCountries()

        // Enable Javascript
        val webSettings = webViewPayment.settings
        webSettings.javaScriptEnabled = true

        val webSettingsWestPack = webViewPaymentWestPack.settings
        webSettingsWestPack.javaScriptEnabled = true

        layoutEmail.visibility = VISIBLE
        showPaymentDetail = true
        layoutEmailDetails.visibility = VISIBLE

        /*if (!sharedPrefs?.isLogged) {
            *//*var intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finishAffinity()
            return*//*
        }*/

        if (!sharedPrefs?.isLogged) {
            cbRegisterAnco.visibility = VISIBLE
        } else
            cbRegisterAnco.visibility = GONE

        setData()

        validCardNumber()

        chkDropTurfProperty.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                openDropTurfOnPropertyDialog()
            }
        }

        /*   broadcastReceiver = object : BroadcastReceiver() {
               override fun onReceive(context: Context?, intent: Intent?) {
                   Logger.log("nnn : In broadcastReceiver in CheckOutFragment")

                   when (intent?.action) {
                       Constants.GUEST_USER_TO_LOGIN_VIA_CHECKOUT_ACTION -> {
                           Logger.log("nnn : In CheckOutFragment GUEST_USER_TO_LOGIN_VIA_CHECKOUT_ACTION")
                           val ft: FragmentTransaction =
                               requireActivity().supportFragmentManager.beginTransaction()

                           if (Build.VERSION.SDK_INT >= 26) {
                               ft.setReorderingAllowed(false)
                           }
                           ft.detach(this@CheckoutFragment).attach(this@CheckoutFragment)
                       }
                   }
               }

           }*/
    }

    /* override fun onDestroy() {
         super.onDestroy()
         try {
             _context?.unregisterReceiver(broadcastReceiver)
         } catch (e: Exception) {
             e.printStackTrace()
         }
     }*/

    var mWebViewClient: WebViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView, url: String?) {
//            view.loadUrl("javascript:window.android.onUrlChange(window.location.href);")
            view.loadUrl(
                "file:///android_asset/" + WestpacPayment.HTTP_FILE_NAME
            )
        }
    }

    private fun openDropTurfOnPropertyDialog() {
        dialogDropTurfOnProperty = Dialog(requireContext())
        dialogDropTurfOnProperty?.setCancelable(false)
        dialogDropTurfOnProperty?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogDropTurfOnProperty?.setContentView(R.layout.dialog_drop_turf_property)
        dialogDropTurfOnProperty?.show()

        val window = dialogDropTurfOnProperty?.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)

        val txtTitle = dialogDropTurfOnProperty?.findViewById(R.id.txtTitle) as AppCompatTextView
        txtTitle.text = sharedPrefs.dropTurfOnPropertyTitle
        val txtDropTurfDescription =
            dialogDropTurfOnProperty?.findViewById(R.id.txtDropTurfDescription) as AppCompatTextView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtDropTurfDescription.text = Html.fromHtml(
                sharedPrefs.dropTurfOnPropertyDescription,
                Html.FROM_HTML_MODE_LEGACY
            )
        } else {
            txtDropTurfDescription.text = Html.fromHtml(sharedPrefs.dropTurfOnPropertyDescription)
        }


        val txtConfirm = dialogDropTurfOnProperty?.findViewById(R.id.txtConfirm) as TextView
        txtConfirm.setOnClickListener {
            chkDropTurfProperty.isChecked = true
            dialogDropTurfOnProperty?.dismiss()
        }

        /*    val imgClose = dialogDropTurfOnProperty?.findViewById(R.id.imgClose) as ImageView
            imgClose.setOnClickListener {
                dialogDropTurfOnProperty?.dismiss()
            }
    */
        val txtCancel = dialogDropTurfOnProperty?.findViewById(R.id.txtCancel) as TextView
        txtCancel.setOnClickListener {
            chkDropTurfProperty.isChecked = false
            dialogDropTurfOnProperty?.dismiss()
        }
    }

    private fun validCardNumber() {
        edtCardNumber.addTextChangedListener(CardNumberValidate(edtCardNumber))
    }

    private fun setData() {
        if (showShipping) {
            layoutShipping.visibility = VISIBLE
            if (showReviewNPurchase) {
                layoutReviewNPurchase.visibility = VISIBLE
                if (showPayment) {
                    layoutPayment.visibility = VISIBLE
                } else {
                    layoutPayment.visibility = View.GONE
                }
            } else {
                layoutReviewNPurchase.visibility = View.GONE
                layoutPayment.visibility = View.GONE
            }
        } else {
            if (shippingType.equals(Constants.CLICK_AND_COLLECT)|| shippingType.equals(Constants.CLICK_AND_COLLECT_TORQUAY)) {
                if (showReviewNPurchase) {
                    layoutReviewNPurchase.visibility = VISIBLE
                    if (showPayment) {
                        layoutPayment.visibility = VISIBLE
                    } else {
                        layoutPayment.visibility = View.GONE
                    }
                } else {
                    layoutReviewNPurchase.visibility = View.GONE
                    layoutPayment.visibility = View.GONE
                }
            }
            layoutShipping.visibility = View.GONE
            layoutReviewNPurchase.visibility = View.GONE
            layoutPayment.visibility = View.GONE
        }

        if (agreedToTNC)
            imgCheckPaymentTnC.setImageResource(R.drawable.ic_checkbox_h)
        else
            imgCheckPaymentTnC.setImageResource(R.drawable.ic_checkbox)

        if (cartDetailsResponse != null) {
            var userInfo = Gson().fromJson(sharedPrefs.userInfo, UserInfo::class.java)
            if (userInfo != null) {
                if (!Utility.isValueNull(userInfo!!.email))
                    edtEmail.setText(userInfo!!.email)

                if (!Utility.isValueNull(userInfo!!.firstName))
                    edtPurchaseFirstName.setText(userInfo!!.firstName)

                if (!Utility.isValueNull(userInfo!!.lastName))
                    edtPurchaseLastName.setText(userInfo!!.lastName)
            }

            if (cartDetailsResponse!!.user != null) {
                if (!Utility.isValueNull(cartDetailsResponse!!.user.email))
                    edtEmail.setText(cartDetailsResponse!!.user.email)
                if (!isSetUserNameAsDefault) { //prevent name issue in shipping address by nnn 07012021
                    isSetUserNameAsDefault = true
                    if (!Utility.isValueNull(cartDetailsResponse!!.user.firstName))
                        edtFirstName.setText(cartDetailsResponse!!.user.firstName)

                    if (!Utility.isValueNull(cartDetailsResponse!!.user.lastName))
                        edtLastName.setText(cartDetailsResponse!!.user.lastName)
                }
            }

            if (cartDetailsResponse!!.products != null && cartDetailsResponse!!.products.size > 0) {
                cartProducts.clear()
                var tmpProductList: ArrayList<ProductDB> = ArrayList()
                var tmpMainListProductList: ArrayList<ProductDB> = ArrayList()
                cartDetailsResponse?.products?.let { tmpMainListProductList.addAll(it) }
                /* for (i in 0 until cartDetailsResponse!!.products.size) {
                     if (cartDetailsResponse!!.products[i].free_product != 0 && (cartDetailsResponse!!.products[i]).free_products.free_product_id != 0 && cartDetailsResponse!!.products[i].qty >= cartDetailsResponse!!.products[i].free_product_qty) {
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
                 }*/
                if (tmpProductList.size > 0) {
                    tmpMainListProductList.addAll(tmpProductList)
                }
                if (freeProduct.size > 0) {
                    freeProduct.clear()
                }
                for (i in 0 until tmpMainListProductList.size) {
                    cartProducts.add(tmpMainListProductList[i])

                    if (tmpMainListProductList[i].is_free_products == 1) {
                        var freeProductAPI: FreeProductAPI = FreeProductAPI()
                        freeProductAPI.free_product_id =
                            tmpMainListProductList[i].free_product_id.toString()
                        freeProductAPI.qty = tmpMainListProductList[i].qty.toString()
                        freeProductAPI.product_id =
                            tmpMainListProductList[i].product_id.toString()
                        /*  freeProductAPI.free_product_id =
                              tmpMainListProductList[i].free_products.product_id.toString()
                          freeProductAPI.qty = tmpMainListProductList[i].free_products.qty.toString()
                          freeProductAPI.product_id =
                              tmpMainListProductList[i].free_products.free_product_id.toString()*/
                        freeProduct.add(freeProductAPI)
                    }
                    isTurf = ((tmpMainListProductList[i]).is_turf == 1) || isTurf
                }/*   for (i in 0 until cartDetailsResponse!!.products.size) {
                    cartProducts.add(cartDetailsResponse!!.products[i] )
                    if (cartDetailsResponse!!.products[i].free_product != 0 ){
                        var freeProductAPI : FreeProductAPI = FreeProductAPI()
                        freeProductAPI.free_product_id = cartDetailsResponse!!.products[i].free_products.product_id.toString()
                        freeProductAPI.qty = cartDetailsResponse!!.products[i].free_products.qty.toString()
                        freeProductAPI.product_id = cartDetailsResponse!!.products[i].free_products.free_product_id.toString()
                        freeProduct.add(freeProductAPI)
                    }
                    isTurf = ((cartDetailsResponse!!.products[i] as ProductDB).is_turf == 1) || isTurf
                }*/
                /* for (i in 0 until cartDetailsResponse!!.products.size) {
                     cartProducts.add(cartDetailsResponse!!.products[i] as ProductDB)
                     if (cartDetailsResponse!!.products[i].free_product != 0 ){
                         var freeProductAPI : FreeProductAPI = FreeProductAPI()
                         freeProductAPI.free_product_id = cartDetailsResponse!!.products[i].free_products.product_id.toString()
                         freeProductAPI.qty = cartDetailsResponse!!.products[i].free_products.qty.toString()
                         freeProductAPI.product_id = cartDetailsResponse!!.products[i].free_products.free_product_id.toString()
                         freeProduct.add(freeProductAPI)
                     }
                     isTurf = ((cartDetailsResponse!!.products[i] as ProductDB).is_turf == 1) || isTurf
                 }*/
//                grpAdminFee.visibility = if (isTurf) View.GONE else View.VISIBLE //nnn
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
                txtSubTotal.text = "$0"
            }
            if (cartDetailsResponse!!.totalDiscount > 0) {
                txtDiscount.text =
                    "$${
                        Utility.formatNumber(
                            Utility.roundTwoDecimals(cartDetailsResponse!!.totalDiscount).toFloat()
                        )
                    }"
            } else {
                txtDiscount.text = "$0"
            }

            if (cartDetailsResponse!!.creditDiscount>0)
            {
                txtCredit.text =
                    "$${
                        Utility.formatNumber(
                            Utility.roundTwoDecimals(cartDetailsResponse!!.creditDiscount).toFloat()
                        )
                    }"

            }
            else{
                txtCredit.text = "$0"
            }

            if (cartDetailsResponse!!.totalCartPrice > 0) {
                /*if (!isTurf) {
                    adminFee =(cartDetailsResponse!!.totalCartPrice * (sharedPrefs.adminFeeNonTurf).toFloat())/100
                    txtAdminFee.text = "$${Utility.formatNumber(
                        Utility.roundTwoDecimals(adminFee).toFloat()
                    )}"
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
                                                .toFloat())
                                )
                            )
                        )

                    /* txtTotalCost.text =
                         Html.fromHtml(
                             getString(
                                 R.string.total_cost,
                                 Utility.formatNumber((
                                         Utility.roundTwoDecimals(cartDetailsResponse!!.totalCartPrice)
                                             .toFloat().plus(Utility.roundTwoDecimals(adminFee).toFloat())
                                         )
                                 )
                             )) //nnn
 */

                }
                944
                /*txtTotalCost.text =
                    Html.fromHtml(
                        getString(
                            R.string.total_cost,
                            Utility.formatNumber(
                                Utility.roundTwoDecimals(cartDetailsResponse!!.totalCartPrice)
                                    .toFloat()
                            )
                        )
                    )*/
            } else {
                txtTotalCost.text = Html.fromHtml(getString(R.string.total_cost, "0"))
            }

            if (cartDetailsResponse!!.shippingPrice > 0) {
                txtShippingCost.text =
                    "$${
                        Utility.formatNumber(
                            Utility.roundTwoDecimals(cartDetailsResponse!!.shippingPrice).toFloat()
                        )
                    }"
            } else {
                txtShippingCost.text = "$0"
            }

            if (shippingType.equals(Constants.CLICK_AND_COLLECT) || shippingType.equals(Constants.CLICK_AND_COLLECT_TORQUAY)) {
                txtReviewNPurchaseNumLabel.text = "2"
                txtPaymentNumLabel.text = "3"
            } else {
                txtReviewNPurchaseNumLabel.text = "3"
                txtPaymentNumLabel.text = "4"
            }

            txtDeliveryDate.text = deliveryDate
            edtInstructionsNotes.setText(quoteNote)
            chkDropTurfProperty.visibility =
                if (isTurf && shippingType == Constants.DELIVER_TO_DOOR) VISIBLE else GONE
            chkDropTurfProperty.text = sharedPrefs.acceptDeclineCheckboxTitle

            if (shippingType == Constants.DELIVER_TO_DOOR) {
                spinnerCountryAdapter = SpinnerCountryAdapter(requireActivity(), countries)
                spinnerCountry.adapter = spinnerCountryAdapter
                spinnerCountry.setSelection(getSelectedCountry())
                if (!shippingCountryCode.equals("AU")) {
                    edtState.setText(shippingState)
                }
                if (Utility.isValueNull(edtCity.text.toString()))
                    edtCity.setText(shippingCity)
                if (Utility.isValueNull(edtZipCode.text.toString())) {
                    edtZipCode.setText(shippingPostalCode)
                    edtPostcode.setText(shippingPostalCode)
                }
            } else {
                if (sharedPrefs.postalCode.toString().isNotEmpty() && sharedPrefs.postalCode != 0) {
                    edtPostcode.setText(sharedPrefs.postalCode.toString())
                }
            }

            if (sharedPrefs.phoneNumber.toString().isNotEmpty() && sharedPrefs.phoneNumber != "") {
                edtPhoneNum.setText(sharedPrefs.phoneNumber.toString())
            }
        }
    }

    private fun getSelectedCountry(): Int {
        if (!Utility.isValueNull(shippingCountryCode)) {
            for (i in 0 until countries.size) {
                if (countries[i].countryCode.equals(shippingCountryCode))
                    return i
            }
        }
        return 0
    }

    private fun getSelectedState(): Int {
        if (!Utility.isValueNull(shippingState)) {
            for (i in 0 until states.size) {
                if (states[i].stateName.equals(shippingState))
                    return i
            }
        }
        return 0
    }

    private fun initObservers() {
        cartViewModel!!.productsLiveData.observe(this, Observer {
            if (it != null) {
                if (cartProducts == null)
                    cartProducts = ArrayList()
                cartProducts.clear()
                cartProducts.addAll(it)
                if (cartProducts.size > 0) {
                    txtCartProducts.text = "${cartProducts.size}"
                    txtCartProducts.visibility = VISIBLE
                } else
                    txtCartProducts.visibility = View.GONE
                setProductAdapter()
                isOtherDataAvailable = !isOtherDataAvailable
                if (!isOtherDataAvailable) {
                    Utility.showProgress(requireContext(), "", false)
                    cartViewModel?.getCartDetails(
                        cartProducts,
                        cartCoupons,
                        couponCode,
                        shippingType,
                        shippingCountryCode,
                        shippingPostalCode,
                        credit
                    )
                }
            }
        })

        cartViewModel!!.couponsLiveData.observe(this, Observer {
            if (it != null) {
                if (cartCoupons == null)
                    cartCoupons = ArrayList()
                cartCoupons.clear()
                cartCoupons.addAll(it)
                isOtherDataAvailable = !isOtherDataAvailable
                if (!isOtherDataAvailable) {
                    Utility.showProgress(requireContext(), "", false)
                    cartViewModel?.getCartDetails(
                        cartProducts,
                        cartCoupons,
                        couponCode,
                        shippingType,
                        shippingCountryCode,
                        shippingPostalCode,
                        credit
                    )
                }
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
                cartViewModel!!._countryListLiveData.value = null
            }
        })

        cartViewModel!!.paymentMethodsLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                paymentMethodsResponse = it
                if (paymentMethods != null)
                    paymentMethods = ArrayList()
                paymentMethods.addAll(it.paymentMethod)
                if (paymentMethods != null && paymentMethods.size > 0) {
                    for (i in 0 until paymentMethods.size) {//here 05012020 paypal disable
//                        if (paymentMethods[i].id == 7) {
                        if (paymentMethods[i].id == 3 && paymentMethods[i].status == "Active") {
                            paymentMethods[i].checked = true
                        }
                        if (paymentMethods[i].id == 4 && paymentMethods[i].status == "Active") { //Express checkout
                            for (j in 0 until paymentMethods.size) {
                                paymentMethods[j].checked = false
                            }
                            paymentMethods[i].checked = true
                            break
                        }
                    }
                }
                txtProceed.text = getString(R.string.proceed, paymentMethods[0].displayName)
//                setPaymentMethodsAdapter()
                setPaymentMethodsScroll()
                cartViewModel!!._paymentMethodsLiveData.value = null
            }
        })

        cartViewModel!!.cartDetailsLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                if (it.success) {
                    cartDetailsResponse = it.data
                    setData()
                }
                isOtherDataAvailable = true
                cartViewModel!!._cartDetailsLiveData.value = null
            }
        })

        cartViewModel!!.ordersLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                webViewPayment.visibility = VISIBLE
                if (activity is HomeActivity)
                    (activity as HomeActivity).hideShowFooter(false)
                else if (activity is PaymentActivity)
                    (activity as PaymentActivity).hideShowFooter(false)
                else if (activity is ProfileActivity)
                    (activity as ProfileActivity).hideShowFooter(false)
                webViewPayment.settings.javaScriptEnabled = true
                webViewPayment.loadUrl(
                    String.format(BuildConfig.AFTERPAY_CHECKOUT_URL, it.token, "")
                )
                Log.d("AFTER PAY==", String.format(BuildConfig.AFTERPAY_CHECKOUT_URL, it.token, ""))
                webViewPayment.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        Log.d("AFTER Method==", request!!.url.toString())
//                        webViewPayment.loadUrl(request?.url?.toString())
                        if (request!!.url.toString().contains("https://www.google.com")) {
                            Log.d("AFTER PAY su==", request.url.toString())

                            webViewPayment.visibility = View.GONE
                            if (activity is HomeActivity)
                                (activity as HomeActivity).hideShowFooter(true)
                            else if (activity is PaymentActivity)
                                (activity as PaymentActivity).hideShowFooter(true)
                            else if (activity is ProfileActivity)
                                (activity as ProfileActivity).hideShowFooter(true)
                            // webViewPayment.webViewClient = null
                            Utility.showProgress(requireContext(), "", false)
                            cartViewModel?.afterpayCapture(it.token, BuildConfig.AFTERPAY_USER)
                        } else if (request!!.url.toString().contains("https://www.wikipedia.org")) {
                            Log.d("AFTER PAY fail==", request.url.toString())

                            webViewPayment.visibility = View.GONE
                            if (activity is HomeActivity)
                                (activity as HomeActivity).hideShowFooter(true)
                            else if (activity is PaymentActivity)
                                (activity as PaymentActivity).hideShowFooter(true)
                            else if (activity is ProfileActivity)
                                (activity as ProfileActivity).hideShowFooter(true)
                            //  webViewPayment?.webViewClient = null
                            (requireActivity() as AppCompatActivity).showAlert(Constants.AFTERPAY_ERROR_MESSAGE)
                        }
//                        return super.shouldOverrideUrlLoading(view, request)
                        return true
                    }

                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                        webViewPayment.loadUrl(url)
                        if (url!!.contains("https://www.google.com")) {
                            Log.d("AFTER PAY SUC==", url.toString())

                            webViewPayment.visibility = View.GONE
                            if (activity is HomeActivity)
                                (activity as HomeActivity).hideShowFooter(true)
                            else if (activity is PaymentActivity)
                                (activity as PaymentActivity).hideShowFooter(true)
                            else if (activity is ProfileActivity)
                                (activity as ProfileActivity).hideShowFooter(true)
                            // webViewPayment.webViewClient = null
                            Utility.showProgress(requireContext(), "", false)
                            cartViewModel?.afterpayCapture(it.token, BuildConfig.AFTERPAY_USER)
                        } else if (url.contains("https://www.wikipedia.org")) {
                            Log.d("AFTER PAY ERR==", url.toString())

                            (requireActivity() as AppCompatActivity).showAlert(Constants.AFTERPAY_ERROR_MESSAGE)
                        }
//                        return super.shouldOverrideUrlLoading(view, request)
                        return true
                    }
                }
                cartViewModel!!._ordersLiveData.value = null
            }
        })

        cartViewModel!!.captureLiveData.observe(this, Observer {
            if (it != null) {

                Log.d("After pay ====", it.id)
                val items: ArrayList<Bundle> = ArrayList()
                if (cartProducts != null && cartProducts.size > 0) {

                    Log.d("express checkout===", cartProducts.size.toString())
                    for (i in 0 until cartProducts.size) {
                        val price = cartProducts[i]?.price.toString()
                        val QUANTITY = cartProducts[i]?.qty.toString()
                        Log.d("quentity express=", QUANTITY)
                        val bundle = Bundle()
                        bundle.putString(
                            FirebaseAnalytics.Param.ITEM_ID,
                            cartProducts[i]?.product_id.toString()
                        )
                        if (price != null && !price.equals("")) {
                            bundle.putDouble(
                                FirebaseAnalytics.Param.VALUE,
                                cartProducts[i]?.price!!.toDouble()
                            )

                        }
                        if (QUANTITY != null && !QUANTITY.contentEquals("")) {
                            bundle.putLong(
                                FirebaseAnalytics.Param.QUANTITY,
                                cartProducts[i]?.qty.toLong()
                            )

                        }
                        bundle.putString(
                            FirebaseAnalytics.Param.ITEM_CATEGORY,
                            cartProducts[i]?.product_category_id.toString()
                        )
                        bundle.putString(
                            FirebaseAnalytics.Param.ITEM_NAME,
                            cartProducts[i]?.product_name
                        )
                        items.add(bundle)
                    }
                }

                val bundle = Bundle()
                bundle.putDouble(
                    FirebaseAnalytics.Param.VALUE,
                    orderDetails!!.totalCartPrice.toDouble()
                )
                bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                bundle.putDouble(
                    FirebaseAnalytics.Param.SHIPPING,
                    orderDetails!!.shippingCost.toDouble()
                )
                bundle.putString(
                    FirebaseAnalytics.Param.TRANSACTION_ID, it.id
                )
/*
                                        bundle.putString(
                                            FirebaseAnalytics.Param.TRANSACTION_ID,
                                            orderDetails!!.referenceNumber
                                        )
*/

                bundle.putDouble(
                    FirebaseAnalytics.Param.DISCOUNT,
                    orderDetails!!.totalDiscount.toDouble()
                )
                bundle.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, items)

                // bundle.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, items)
                mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle)


                /*val bundle = Bundle()
                bundle.putString(
                    FirebaseAnalytics.Param.VALUE,
                    cartDetailsResponse!!.totalCartPrice.toString()
                )
                bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                bundle.putDouble(
                    FirebaseAnalytics.Param.SHIPPING,
                    cartDetailsResponse!!.shippingPrice.toDouble()
                )
                // bundle.putParcelable(FirebaseAnalytics.Param.COUPON, cartDetailsResponse.coupons.toString())

                bundle.putString(
                    FirebaseAnalytics.Param.DISCOUNT,
                    cartDetailsResponse!!.totalDiscount.toString()
                )
                // bundle.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, items)
                mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle)*/
                Utility.cancelProgress()
//                (requireActivity() as AppCompatActivity).showAlert(
//                    getString(
//                        R.string.transaction_completed_message,
//                        it.id
//                    )
//                )
                (requireActivity() as AppCompatActivity).openAlertDialogWithOneClick(getString(R.string.app_name),
                    getString(
                        R.string.transaction_completed_message
                        /*,it.id*/
                    ),
                    getString(
                        android.R.string.ok
                    ),
                    object : OnDialogButtonClick {
                        override fun onPositiveButtonClick() {
                            Utility.showProgress(requireContext(), "", false)
                            cartViewModel?.updateOrder(
                                orderDetails!!.id,
                                getSelectedItem()!!.displayName,
                                it.toString(),
                                "1",
                                sharedPrefs?.quoteID
                            )
                        }

                        override fun onNegativeButtonClick() {
                        }

                    })
                cartViewModel!!._captureLiveData.value = null
            }
        })

        cartViewModel!!.errorLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                it?.let { it1 ->
                    shortToast(it1)
                }
                cartViewModel!!._errorLiveData.value = null
            }
        })

        cartViewModel!!.orderDetailsLiveData.observe(this, Observer {
            if (it != null) {
                if (it.success) {
                    if (orderDetails != null || cartDetailsResponse!!.totalCartPrice <= 0) {
                        shortToast(it.message)
                        cartViewModel?.deleteProduct(null)
                        cartViewModel?.deleteCoupon(null)
                        cartViewModel?._productLiveData?.value = null
                        cartViewModel?._couponLiveData?.value = null
                    } else {
                        cartViewModel?.getPaymentMethods()
                        if (cartDetailsResponse!!.totalCartPrice > 0) {
                            showReviewNPurchaseDetail = false
                            layoutReviewNPurchaseDetails.visibility =
                                if (showReviewNPurchaseDetail) VISIBLE else View.GONE
                            showPayment = true
                            layoutPayment.visibility = if (showPayment) VISIBLE else View.GONE
                            showPaymentDetail = true
                            layoutPaymentDetails.visibility =
                                if (showPaymentDetail) VISIBLE else View.GONE
                        }
                        orderDetails = it.data
                    }
                } else {
                    shortToast(it.message)
                }
                cartViewModel!!._orderDetailsLiveData.value = null
            }
        })

        cartViewModel!!.productDeletionLiveData.observe(this, Observer {
            if (it != null && it) {
                Handler().postDelayed({
                    Utility.cancelProgress()
                    sharedPrefs.totalProductsInCart = 0
                    requireActivity().supportFragmentManager.popBackStack()
                    if (sharedPrefs.isLogged) {
                        (requireActivity() as AppCompatActivity).pushFragment(
                            OrderFragment(),
                            false,
                            true,
                            false,
                            R.id.flContainerHome
                        )
                    } else {
                        (requireActivity() as AppCompatActivity).pushFragment(
                            OrderDetailsFragment(null, 0),
                            false,
                            true,
                            false,
                            R.id.flContainerHome
                        )
                    }
                }, 1000)
                cartViewModel!!._productDeletionLiveData.value = null
            }
        })
    }

    private fun setProductAdapter() {
        if (cartProducts != null && cartProducts.size > 0) {
            cartProductsAdapter = CartProductsAdapter(
                requireActivity() as AppCompatActivity,
                cartProducts,
                false,
                object : OnCartProductChangeListener {
                    override fun onItemDelete(product: ProductDB) {
                    }

                    override fun onQuantityChange(product: ProductDB) {
                    }
                })
            listProducts.adapter = cartProductsAdapter
        }
    }

//    private fun setPaymentMethodsAdapter() {
//        if (paymentMethods != null && paymentMethods.size > 0) {
//            paymentMethodsAdapter = PaymentMethodsAdapter(
//                requireActivity() as AppCompatActivity,
//                paymentMethods, object : OnPaymentMethodSelectedListener {
//                    override fun onPaymentMethodSelected(paymentMethod: PaymentMethodsResponse.PaymentMethod) {
//                        txtProceed.text = getString(R.string.proceed, paymentMethod.displayName)
//                    }
//                }
//            )
//            listPaymentMethods.adapter = paymentMethodsAdapter
//        }
//    }

    private fun setPaymentMethodsScroll() {
        if (paymentMethods != null && paymentMethods.size > 0) {
            linPaymentMethods.removeAllViews()
            for (i in 0 until paymentMethods.size) {
                val paymentMethodView: View =
                    LayoutInflater.from(activity)
                        .inflate(R.layout.item_scroll_payment_methods, null)
                val txtTitle: TextView = paymentMethodView.findViewById(R.id.txtTitle)
                val view: View = paymentMethodView.findViewById(R.id.view)
                txtTitle.text = paymentMethods[i].displayName
                view.visibility = if (paymentMethods[i].checked) VISIBLE else View.GONE
                if (paymentMethods[i].checked) {
                    if (paymentMethods[i].slug.equals(Constants.EXPRESS_CHECKOUT)) {
                        imgPaymentLogo.visibility = VISIBLE
//                        txtAboveDescription.visibility = View.GONE
//                        txtBelowDescription.visibility = View.VISIBLE
                        westpacLayout.visibility = GONE
                        txtAboveDescription.visibility = VISIBLE
                        txtBelowDescription.visibility = View.GONE

                        imgCheckPaymentTnC.visibility = VISIBLE
                        txtPaymentTnC.visibility = VISIBLE
                        txtProceed.visibility = VISIBLE

                        imgPaymentLogo.setImageResource(R.drawable.img_paypal)
                        txtProceed.text =
                            getString(R.string.continue_to, paymentMethods[i].displayName)

                        if (!Utility.isValueNull(paymentMethods[i].descriptions)) {
//                            txtBelowDescription.loadData(
                            /*txtAboveDescription.loadData(
//                                paymentMethods[i].descriptions.trim(),
                                "Express Checkout Description",
                                "text/html",
                                "UTF-8"
                            )*/
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                txtAboveDescription.text = Html.fromHtml(
                                    paymentMethods[i].descriptions.trim(),
                                    Html.FROM_HTML_MODE_COMPACT
                                )
                            } else {
                                txtAboveDescription.text =
                                    Html.fromHtml(paymentMethods[i].descriptions.trim())
                            }
                        }
                        Logger.log("WebView Desciption express checkout visibility : " + txtAboveDescription.visibility)
                    } else if (paymentMethods[i].slug.equals(Constants.AFTERPAY)) {
                        imgCheckPaymentTnC.visibility = VISIBLE
                        txtPaymentTnC.visibility = VISIBLE
                        txtProceed.visibility = VISIBLE
                        westpacLayout.visibility = GONE

                        imgPaymentLogo.visibility = VISIBLE
//                        txtAboveDescription.visibility = View.GONE
//                        txtBelowDescription.visibility = View.VISIBLE
                        txtAboveDescription.visibility = VISIBLE
                        txtBelowDescription.visibility = View.GONE
                        imgPaymentLogo.setImageResource(R.drawable.img_afterpay)
                        txtProceed.text =
                            getString(R.string.continue_to, paymentMethods[i].displayName)
                        if (!Utility.isValueNull(paymentMethods[i].descriptions)) {
//                            txtBelowDescription.loadData(
                            /*txtAboveDescription.loadData(
//                                paymentMethods[i].descriptions.trim(),
                                "AfterPay Description",
                                "text/html",
                                "UTF-8"
                            )*/
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                txtAboveDescription.text = Html.fromHtml(
                                    paymentMethods[i].descriptions.trim(),
                                    Html.FROM_HTML_MODE_COMPACT
                                )
                            } else {
                                txtAboveDescription.text =
                                    Html.fromHtml(paymentMethods[i].descriptions.trim())
                            }
                        }
                        Logger.log("WebView Desciption afterpay visibility : " + txtAboveDescription.visibility)
                    } else if (paymentMethods[i].slug.equals(Constants.COD) || paymentMethods[i].slug.equals(
                            Constants.BANK_TRANSFER
                        ) || paymentMethods[i].slug.equals(Constants.PAY_LATER)
                    ) {
                        imgPaymentLogo.visibility = View.GONE
                        txtAboveDescription.visibility = VISIBLE
                        txtProceed.visibility = VISIBLE
                        txtBelowDescription.visibility = View.GONE

                        imgCheckPaymentTnC.visibility = VISIBLE
                        txtPaymentTnC.visibility = VISIBLE
                        westpacLayout.visibility = GONE

                        txtProceed.text =
                            getString(R.string.continue_to, paymentMethods[i].displayName)
                        if (!Utility.isValueNull(paymentMethods[i].descriptions)) {
                            /*txtAboveDescription.loadData(
//                                paymentMethods[i].descriptions.trim(),
                                "Bank Transfer Description",
                                "text/html",
                                "UTF-8"
                            )*/
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                txtAboveDescription.text = Html.fromHtml(
                                    paymentMethods[i].descriptions.trim(),
                                    Html.FROM_HTML_MODE_COMPACT
                                )
                            } else {
                                txtAboveDescription.text =
                                    Html.fromHtml(paymentMethods[i].descriptions.trim())
                            }
                        }
                        Logger.log("WebView Desciption bank transfer visibility : " + txtAboveDescription.visibility)
                    } else if (paymentMethods[i].slug == Constants.WESTPEC_PAY
                    ) {
                        //imgPaymentLogo.setImageResource(R.drawable.westpac)
                        /* westpacLayout.visibility = VISIBLE
                         txtProceed.visibility = GONE
                         imgPaymentLogo.visibility = GONE
                         txtAboveDescription.visibility = GONE
                         txtBelowDescription.visibility = GONE

                         imgCheckPaymentTnC.visibility = GONE
                         txtPaymentTnC.visibility = GONE

                         txtProceed.text =
                             getString(R.string.continue_to, paymentMethods[i].displayName)
                         if (!Utility.isValueNull(paymentMethods[i].descriptions)) {
                             *//*txtAboveDescription.loadData(
//                                paymentMethods[i].descriptions.trim(),
                                "Westpec Pay Description",
                                "text/html",
                                "UTF-8"
                            )*//*
                        }
                        Logger.log("WebView Desciption westpec visibility : " + txtAboveDescription.visibility)*/

                        //nnn here
                        imgPaymentLogo.setImageResource(R.drawable.westpac) //nnn
                        westpacLayout.visibility = GONE  //nnn
                        txtProceed.visibility = VISIBLE //nnn

                        imgCheckPaymentTnC.visibility = VISIBLE //nnn
                        txtPaymentTnC.visibility = VISIBLE  //nnn

                        txtBelowDescription.visibility = GONE
                        txtAboveDescription.visibility = VISIBLE
                        imgPaymentLogo.visibility = VISIBLE
                        txtProceed.text =
                            getString(R.string.continue_to, paymentMethods[i].displayName)
                        if (!Utility.isValueNull(paymentMethods[i].descriptions)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                txtAboveDescription.text = Html.fromHtml(
                                    paymentMethods[i].descriptions.trim(),
                                    Html.FROM_HTML_MODE_COMPACT
                                )
                            }
                        }
                        Logger.log("WebView Desciption westpec visibility : " + txtAboveDescription.visibility)


                    }
                }

                if (paymentMethods[i].slug.equals(Constants.COD) || paymentMethods[i].slug.equals(
                        Constants.BANK_TRANSFER
                    ) || paymentMethods[i].slug.equals(Constants.PAY_LATER) || paymentMethods[i].slug.equals(
                        Constants.AFTERPAY
                    ) || paymentMethods[i].slug.equals(Constants.EXPRESS_CHECKOUT)
                    || paymentMethods[i].slug.equals(Constants.WESTPEC_PAY)
                )
                    linPaymentMethods.addView(paymentMethodView)
                paymentMethodView.setOnClickListener {
                    if (!paymentMethods[i].checked) {
                        for (j in 0 until paymentMethods.size) {
                            paymentMethods[j].checked = false
                        }
                        paymentMethods[i].checked = true
                        setPaymentMethodsScroll()
                        /*  scrollPayment.smoothScrollTo(
                              linPaymentMethods.left + linPaymentMethods.getChildAt(i).right,
                              linPaymentMethods.top
                          )*/
                    }
                }
            }
        }
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {
            R.id.imgLogo -> {
//                (requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    HomeFragment(),
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )
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

            R.id.imgCart -> {
//                (requireActivity() as AppCompatActivity).hideKeyboard()
                if (sharedPrefs.totalProductsInCart > 0) {
//                    (requireActivity() as AppCompatActivity).pushFragment(
//                        CartFragment(),
//                        true,
//                        true,
//                        false,
//                        R.id.flContainerHome
//                    )
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    shortToast(getString(R.string.no_product_in_cart_message))
                }
            }

            R.id.imgBack -> {
//                (requireActivity() as AppCompatActivity).hideKeyboard()
                if (sharedPrefs.totalProductsInCart > 0) {
//                    (requireActivity() as AppCompatActivity).pushFragment(
//                        CartFragment(),
//                        true,
//                        true,
//                        false,
//                        R.id.flContainerHome
//                    )
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    shortToast(getString(R.string.no_product_in_cart_message))
                }
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

            R.id.layoutEmailLabel -> {
                showEmailDetail = !showEmailDetail
                layoutEmailDetails.visibility = if (showEmailDetail) VISIBLE else View.GONE
                if (showEmailDetail) {
//                    showShipping = false
//                    layoutShipping.visibility = if (showShipping) View.VISIBLE else View.GONE
                    showShippingDetail = false
                    layoutShippingDetails.visibility =
                        if (showShippingDetail) VISIBLE else View.GONE

//                    showReviewNPurchase = false
//                    layoutReviewNPurchase.visibility = if (showReviewNPurchase) View.VISIBLE else View.GONE
                    showReviewNPurchaseDetail = false
                    layoutReviewNPurchaseDetails.visibility =
                        if (showReviewNPurchaseDetail) VISIBLE else View.GONE

//                    showPayment = false
//                    layoutPayment.visibility = if (showPayment) View.VISIBLE else View.GONE
                    showPaymentDetail = false
                    layoutPaymentDetails.visibility =
                        if (showPaymentDetail) VISIBLE else View.GONE
                }
            }

            R.id.layoutShippingLabel -> {
                showShippingDetail = !showShippingDetail
                layoutShippingDetails.visibility =
                    if (showShippingDetail) VISIBLE else View.GONE

                if (showShippingDetail) {
                    showEmailDetail = false
                    layoutEmailDetails.visibility = if (showEmailDetail) VISIBLE else View.GONE

//                    showReviewNPurchase = false
//                    layoutReviewNPurchase.visibility = if (showReviewNPurchase) View.VISIBLE else View.GONE
                    showReviewNPurchaseDetail = false
                    layoutReviewNPurchaseDetails.visibility =
                        if (showReviewNPurchaseDetail) VISIBLE else View.GONE

//                    showPayment = false
//                    layoutPayment.visibility = if (showPayment) View.VISIBLE else View.GONE
                    showPaymentDetail = false
                    layoutPaymentDetails.visibility =
                        if (showPaymentDetail) VISIBLE else View.GONE
                }
            }

            R.id.layoutReviewNPurchaseLabel -> {
                showReviewNPurchaseDetail = !showReviewNPurchaseDetail
                layoutReviewNPurchaseDetails.visibility =
                    if (showReviewNPurchaseDetail) VISIBLE else View.GONE
                if (showReviewNPurchaseDetail) {
                    showEmailDetail = false
                    layoutEmailDetails.visibility = if (showEmailDetail) VISIBLE else View.GONE

                    showShippingDetail = false
                    layoutShippingDetails.visibility =
                        if (showShippingDetail) VISIBLE else View.GONE

//                    showPayment = false
//                    layoutPayment.visibility = if (showPayment) View.VISIBLE else View.GONE
                    showPaymentDetail = false
                    layoutPaymentDetails.visibility =
                        if (showPaymentDetail) VISIBLE else View.GONE
                }
            }

            R.id.layoutPaymentLabel -> {
                cartViewModel?.getPaymentMethods()
                showPaymentDetail = !showPaymentDetail
                layoutPaymentDetails.visibility = if (showPaymentDetail) VISIBLE else View.GONE
                if (showPaymentDetail) {
                    showEmailDetail = false
                    layoutEmailDetails.visibility = if (showEmailDetail) VISIBLE else View.GONE

                    showShippingDetail = false
                    layoutShippingDetails.visibility =
                        if (showShippingDetail) VISIBLE else View.GONE

                    showReviewNPurchaseDetail = false
                    layoutReviewNPurchaseDetails.visibility =
                        if (showReviewNPurchaseDetail) VISIBLE else View.GONE
                }
            }


            R.id.txtContinueEmail -> {
                try {
//                    sharedPrefs.phoneNumber =  Integer.parseInt(edtPhoneNum.text.toString())
                    sharedPrefs.phoneNumber = edtPhoneNum.text.toString()
                    sharedPrefs.postalCode = edtPostcode.text.toString().toInt()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (Utility.isValueNull(edtPurchaseFirstName.text.toString())) {
                    shortToast(getString(R.string.black_firstname_message))
                } else if (Utility.isValueNull(edtPurchaseLastName.text.toString())) {
                    shortToast(getString(R.string.black_lastname_message))
                } else if (Utility.isValueNull(edtEmail.text.toString())) {
                    shortToast(getString(R.string.blank_email_message))
                } else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.text.toString()).matches()) {
                    shortToast(getString(R.string.invalid_email_message))
                } else if (Utility.isValueNull(edtPostcode.text.toString())) {
                    shortToast(getString(R.string.blank_postal_code_message))
                } else if (Utility.isValueNull(edtPhoneNum.text.toString())) {
                    shortToast(getString(R.string.blank_phone_number_message))
                } else {
                    (requireActivity() as AppCompatActivity).hideKeyboard()

                    if (!sharedPrefs?.isLogged && cbRegisterAnco.isChecked) {
                        var intent = Intent(requireActivity(), SignUpActivity::class.java)
                        intent.putExtra(Constants.FROM_GUEST_USER_REGISTRATION, true)
                        intent.putExtra("firstName", edtPurchaseFirstName.text.toString())
                        intent.putExtra("lastName", edtPurchaseLastName.text.toString())
                        intent.putExtra("email", edtEmail.text.toString())
                        startActivityForResult(intent, 101)
                        // nnn 19/11/2020
                        /*  if (cbRegisterAnco.isChecked) {
                              var intent = Intent(requireActivity(), SignUpActivity::class.java)
                              intent.putExtra(Constants.FROM_GUEST_USER_REGISTRATION, true)
                              intent.putExtra("firstName", edtPurchaseFirstName.text.toString())
                              intent.putExtra("lastName", edtPurchaseLastName.text.toString())
                              intent.putExtra("email", edtEmail.text.toString())
                              startActivityForResult(intent, 101)
                          }   // nnn 19/11/2020
                          else {
                              shortToast(getString(R.string.selectCheckBoxToRegister))
                              return
                          }*/
                    } else {
                        showEmailDetail = false
                        layoutEmailDetails.visibility =
                            if (showEmailDetail) VISIBLE else View.GONE
                        if (shippingType.equals(Constants.CLICK_AND_COLLECT)|| shippingType.equals(Constants.CLICK_AND_COLLECT_TORQUAY)) {
                            txtReviewNPurchaseNumLabel.text = "2"
                            txtPaymentNumLabel.text = "3"
                            showReviewNPurchase = true
                            layoutReviewNPurchase.visibility =
                                if (showReviewNPurchase) VISIBLE else View.GONE
                            showReviewNPurchaseDetail = true
                            layoutReviewNPurchaseDetails.visibility =
                                if (showReviewNPurchaseDetail) VISIBLE else View.GONE
                        } else {
                            txtShippingNumLabel.text = "2"
                            txtReviewNPurchaseNumLabel.text = "3"
                            txtPaymentNumLabel.text = "4"
                            showShipping = true
                            layoutShipping.visibility =
                                if (showShipping) VISIBLE else View.GONE
                            showShippingDetail = true
                            layoutShippingDetails.visibility =
                                if (showShippingDetail) VISIBLE else View.GONE
                        }
                    }
                }
            }

            R.id.txtContinueShipping -> {
                if (Utility.isValueNull(edtFirstName.text.toString())) {
                    shortToast(getString(R.string.black_firstname_message))
                } else if (Utility.isValueNull(edtLastName.text.toString())) {
                    shortToast(getString(R.string.black_lastname_message))
                } else if (Utility.isValueNull(edtAddressOne.text.toString())) {
                    shortToast(getString(R.string.black_address_message))
                } else if (spinnerCountry.selectedItemPosition == -1) {
                    shortToast(getString(R.string.black_country_message))
                } else if (Utility.isValueNull(edtZipCode.text.toString())) {
                    shortToast(getString(R.string.black_zipcode_message))
                } else if (Utility.isValueNull(edtCity.text.toString())) {
                    shortToast(getString(R.string.black_city_message))
                } else if (spinnerState.selectedItemPosition == -1 && Utility.isValueNull(edtState.text.toString())) {
                    shortToast(getString(R.string.black_state_message))
                } else {
                    (requireActivity() as AppCompatActivity).hideKeyboard()
                    Utility.showProgress(requireContext(), "", false)
                    cartViewModel?.getCartDetails(
                        cartProducts,
                        cartCoupons,
                        couponCode,
                        shippingType,
                        (spinnerCountry.selectedItem as CountryResponse).countryCode,
                        edtZipCode.text.toString(),
                        credit
                    )
                    showShippingDetail = false
                    layoutShippingDetails.visibility =
                        if (showShippingDetail) VISIBLE else View.GONE
                    showReviewNPurchase = true
                    layoutReviewNPurchase.visibility =
                        if (showReviewNPurchase) VISIBLE else View.GONE
                    showReviewNPurchaseDetail = true
                    layoutReviewNPurchaseDetails.visibility =
                        if (showReviewNPurchaseDetail) VISIBLE else View.GONE
                }
            }

            R.id.txtContinueRnP -> {
                if (Utility.isValueNull(edtEmail.text.toString())) {
                    shortToast(getString(R.string.blank_email_message))
                } else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.text.toString()).matches()) {
                    shortToast(getString(R.string.invalid_email_message))
                } else if (Utility.isValueNull(edtPurchaseFirstName.text.toString())) {
                    shortToast(getString(R.string.black_firstname_message))
                } else if (Utility.isValueNull(edtPurchaseLastName.text.toString())) {
                    shortToast(getString(R.string.black_lastname_message))
                } else if (shippingType.equals(Constants.DELIVER_TO_DOOR) && Utility.isValueNull(
                        edtFirstName.text.toString()
                    )
                ) {
                    shortToast(getString(R.string.black_firstname_message))
                } else if (shippingType.equals(Constants.DELIVER_TO_DOOR) && Utility.isValueNull(
                        edtLastName.text.toString()
                    )
                ) {
                    shortToast(getString(R.string.black_lastname_message))
                } else if (shippingType.equals(Constants.DELIVER_TO_DOOR) && Utility.isValueNull(
                        edtAddressOne.text.toString()
                    )
                ) {
                    shortToast(getString(R.string.black_address_message))
                } else if (shippingType.equals(Constants.DELIVER_TO_DOOR) && spinnerCountry.selectedItemPosition == -1) {
                    shortToast(getString(R.string.black_country_message))
                } else if (shippingType.equals(Constants.DELIVER_TO_DOOR) && Utility.isValueNull(
                        edtZipCode.text.toString()
                    )
                ) {
                    shortToast(getString(R.string.black_zipcode_message))
                } else if (shippingType.equals(Constants.DELIVER_TO_DOOR) && Utility.isValueNull(
                        edtCity.text.toString()
                    )
                ) {
                    shortToast(getString(R.string.black_city_message))
                } else if (shippingType.equals(Constants.DELIVER_TO_DOOR) && spinnerState.selectedItemPosition == -1 && Utility.isValueNull(
                        edtState.text.toString()
                    )
                ) {
                    shortToast(getString(R.string.black_state_message))
                } else {
                    var firstName: String? = null
                    var lastName: String? = null
                    var addressOne: String? = null
                    var addressTwo: String? = null
                    var countryCode: String? = null
                    var state: String? = null
                    var city: String? = null
                    var postCode: Int = 0
                    if (shippingType.equals(Constants.DELIVER_TO_DOOR)) {
                        firstName = edtFirstName.text.toString()
                        lastName = edtLastName.text.toString()
                        addressOne = edtAddressOne.text.toString()
                        addressTwo = edtAddressTwo.text.toString()
                        countryCode = (spinnerCountry.selectedItem as CountryResponse).countryCode
                        if (countryCode.equals("AU"))
                            state = (spinnerState.selectedItem as CountryResponse.State).stateName
                        else
                            state = edtState.text.toString()
                        city = edtCity.text.toString()
                        postCode = edtZipCode.text.toString().toInt()
                    } else {
                        firstName = edtFirstName.text.toString()//nnn try for c2c 07012021
                        lastName = edtLastName.text.toString()//nnn try for c2c 07012021
                        postCode = edtPostcode.text.toString().toInt()
                    }

                    //HERE
                    var cartProductstmp = ArrayList<ProductDB>()
                    cartProductstmp.addAll(cartProducts)
                    cartProducts.clear()
                    for (i in 0 until cartProductstmp.size) {
                        if (cartProductstmp[i].price != 0.0f) {
                            cartProducts.add(cartProductstmp[i])
                        }
                    }

                    Utility.showProgress(requireContext(), "", false)
                    if (orderDetails == null) {
                        cartViewModel?.createOrder(
                            edtEmail.text.toString(),
                            edtPurchaseFirstName.text.toString(),
                            edtPurchaseLastName.text.toString(),
                            firstName,
                            lastName,
                            addressOne,
                            addressTwo,
                            countryCode,
                            state,
                            city,
                            edtPhoneNum.text.toString(),
                            postCode,
                            shippingType,
                            cartProducts,
                            Utility.changeDateFormat(
                                deliveryDate,
                                Utility.DATE_FORMAT_D_MMMM_YYYY,
                                Utility.DATE_FORMAT_YYYY_MM_DD
                            ),
                            credit,
                            cartDetailsResponse?.subTotalPrice.toString(),
                            cartDetailsResponse?.totalDiscount.toString(),
                            cartDetailsResponse?.creditDiscount.toString(),
                            cartDetailsResponse?.shippingPrice.toString(),
                            if (isTurf) cartDetailsResponse?.totalCartPrice.toString() else Utility.formatNumber(
                                (
                                        Utility.roundTwoDecimals(cartDetailsResponse!!.totalCartPrice)
                                            .toFloat()
                                            .plus(Utility.roundTwoDecimals(adminFee).toFloat())
                                        )
                            ).toString(),
                            cartCoupons,
                            couponCode,
                            edtInstructionsNotes.text.toString(),
                            if (chkDropTurfProperty.isChecked) 1 else 0,
                            adminFee.toString(),
                            freeProduct
                        )
                    } else {
                        cartViewModel?.getPaymentMethods()
                        if (cartDetailsResponse!!.totalCartPrice > 0) {
                            showReviewNPurchaseDetail = false
                            layoutReviewNPurchaseDetails.visibility =
                                if (showReviewNPurchaseDetail) VISIBLE else View.GONE
                            showPayment = true
                            layoutPayment.visibility = if (showPayment) VISIBLE else View.GONE
                            showPaymentDetail = true
                            layoutPaymentDetails.visibility =
                                if (showPaymentDetail) VISIBLE else View.GONE
                        }
                    }
                }
            }

            R.id.imgCheckPaymentTnC -> {
                agreedToTNC = !agreedToTNC
                if (agreedToTNC)
                    imgCheckPaymentTnC.setImageResource(R.drawable.ic_checkbox_h)
                else
                    imgCheckPaymentTnC.setImageResource(R.drawable.ic_checkbox)
            }

            R.id.txtPaymentTnC -> {
                openBrowser(getString(R.string.payment_terms_conditions))
            }

            R.id.txtProceed -> {
                if (agreedToTNC) {
                    if (getSelectedItem() != null) {
                        if (getSelectedItem()!!.slug == Constants.EXPRESS_CHECKOUT) {
                            webViewPayment.visibility = VISIBLE
                            if (activity is HomeActivity)
                                (activity as HomeActivity).hideShowFooter(false)
                            else if (activity is PaymentActivity)
                                (activity as PaymentActivity).hideShowFooter(false)
                            else if (activity is ProfileActivity)
                                (activity as ProfileActivity).hideShowFooter(false)
                            webViewPayment.webChromeClient = object : WebChromeClient() {
                                override fun onJsAlert(
                                    view: WebView?,
                                    url: String?,
                                    message: String?,
                                    result: JsResult?
                                ): Boolean {

                                    result?.confirm()
                                    // Log.e("Express Checkout", message)
                                    webViewPayment.visibility = View.GONE
                                    if (activity is HomeActivity)
                                        (activity as HomeActivity).hideShowFooter(true)
                                    else if (activity is PaymentActivity)
                                        (activity as PaymentActivity).hideShowFooter(true)
                                    else if (activity is ProfileActivity)
                                        (activity as ProfileActivity).hideShowFooter(true)
                                    webViewPayment.webChromeClient = null
                                    var expressCheckoutResponse = Gson().fromJson(
                                        message!!.replace("Transaction completed by ", "").replace(
                                            "!",
                                            ""
                                        ), ExpressCheckoutResponse::class.java
                                    )
                                    // condition for test paypal status 08012021 by nnn
                                    if (expressCheckoutResponse.purchaseUnits[0].payments.captures[0].status == "COMPLETED") {
                                        val items: ArrayList<Bundle> = ArrayList()
                                        if (cartProducts != null && cartProducts.size > 0) {

                                            Log.d(
                                                "express checkout===",
                                                cartProducts.size.toString()
                                            )
                                            for (i in 0 until cartProducts.size) {
                                                val price = cartProducts[i]?.price.toString()
                                                val QUANTITY = cartProducts[i]?.qty.toString()
                                                Log.d("quentity express=", QUANTITY)
                                                val bundle = Bundle()
                                                bundle.putString(
                                                    FirebaseAnalytics.Param.ITEM_ID,
                                                    cartProducts[i]?.product_id.toString()
                                                )
                                                if (price != null && !price.equals("")) {
                                                    bundle.putDouble(
                                                        FirebaseAnalytics.Param.VALUE,
                                                        cartProducts[i]?.price!!.toDouble()
                                                    )

                                                }
                                                if (QUANTITY != null && !QUANTITY.contentEquals("")) {
                                                    bundle.putLong(
                                                        FirebaseAnalytics.Param.QUANTITY,
                                                        cartProducts[i]?.qty.toLong()
                                                    )

                                                }
                                                bundle.putString(
                                                    FirebaseAnalytics.Param.ITEM_CATEGORY,
                                                    cartProducts[i]?.product_category_id.toString()
                                                )
                                                bundle.putString(
                                                    FirebaseAnalytics.Param.ITEM_NAME,
                                                    cartProducts[i]?.product_name
                                                )
                                                items.add(bundle)
                                            }
                                        }

                                        val bundle = Bundle()
                                        bundle.putDouble(
                                            FirebaseAnalytics.Param.VALUE,
                                            orderDetails!!.totalCartPrice.toDouble()
                                        )
                                        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                                        bundle.putDouble(
                                            FirebaseAnalytics.Param.SHIPPING,
                                            orderDetails!!.shippingCost.toDouble()
                                        )
                                        bundle.putString(
                                            FirebaseAnalytics.Param.TRANSACTION_ID,
                                            expressCheckoutResponse.purchaseUnits[0].payments.captures[0].id
                                        )
/*
                                        bundle.putString(
                                            FirebaseAnalytics.Param.TRANSACTION_ID,
                                            orderDetails!!.referenceNumber
                                        )
*/

                                        bundle.putDouble(
                                            FirebaseAnalytics.Param.DISCOUNT,
                                            orderDetails!!.totalDiscount.toDouble()
                                        )
                                        bundle.putParcelableArrayList(
                                            FirebaseAnalytics.Param.ITEMS,
                                            items
                                        )

                                        // bundle.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, items)
                                        mFirebaseAnalytics!!.logEvent(
                                            FirebaseAnalytics.Event.PURCHASE,
                                            bundle
                                        )

                                        (requireActivity() as AppCompatActivity).openAlertDialogWithOneClick(
                                            getString(R.string.app_name),
                                            getString(
                                                R.string.transaction_completed_message
                                                /*,expressCheckoutResponse.purchaseUnits[0].payments.captures[0].id*/
                                            ),
                                            getString(
                                                android.R.string.ok
                                            ),
                                            object : OnDialogButtonClick {
                                                override fun onPositiveButtonClick() {
                                                    Utility.showProgress(
                                                        requireContext(),
                                                        "",
                                                        false
                                                    )
                                                    cartViewModel?.updateOrder(
                                                        orderDetails!!.id,
                                                        getSelectedItem()!!.displayName,
                                                        message!!.replace(
                                                            "Transaction completed by ",
                                                            ""
                                                        ).replace("!", ""),
                                                        "1",
                                                        sharedPrefs?.quoteID
                                                    )
                                                }

                                                override fun onNegativeButtonClick() {
                                                }

                                            })

                                    } else {
                                        (requireActivity() as AppCompatActivity).openAlertDialogWithOneClick(
                                            getString(R.string.app_name),
                                            expressCheckoutResponse.status,
                                            getString(
                                                android.R.string.ok
                                            ),
                                            object : OnDialogButtonClick {
                                                override fun onPositiveButtonClick() {
                                                }

                                                override fun onNegativeButtonClick() {
                                                }

                                            })
                                    }

                                    return true
                                }
                            }
                            webViewPayment.loadUrl(
                                String.format(
                                    Constants.EXPRESS_CHECKOUT_LINK,
                                    Utility.roundTwoDecimals(cartDetailsResponse!!.totalCartPrice)
                                )
                            )
                        } else if (getSelectedItem()!!.slug == Constants.AFTERPAY) {
                            Utility.showProgress(requireContext(), "", false)
                            var curreancy: String? = null
                            if (spinnerCountry.selectedItemPosition == -1)
                                curreancy =
                                    (spinnerCountry.selectedItem as CountryResponse).countryCode
                            var phoneNumber: String = ""
                            if (cartDetailsResponse!!.user != null)
                                phoneNumber = cartDetailsResponse!!.user.phoneNumber
                            cartViewModel?.afterpayOrders(
                                cartDetailsResponse!!.totalCartPrice.toString(),
                                "AUD",
                                phoneNumber,
                                edtPurchaseFirstName.text.toString(),
                                edtPurchaseLastName.text.toString(),
                                edtEmail.text.toString()
                            )


                        } else if (getSelectedItem()!!.slug == Constants.WESTPEC_PAY) {

                            /*webViewPaymentWestPack.webViewClient = WebViewClient()
                            webViewPaymentWestPack.settings.javaScriptEnabled = true*/
                            webViewPaymentWestPack.visibility = VISIBLE
                            if (activity is HomeActivity)
                                (activity as HomeActivity).hideShowFooter(false)
                            else if (activity is PaymentActivity)
                                (activity as PaymentActivity).hideShowFooter(false)
                            else if (activity is ProfileActivity)
                                (activity as ProfileActivity).hideShowFooter(false)
                            webViewPaymentWestPack.webChromeClient = object : WebChromeClient() {

                                override fun onJsAlert(
                                    view: WebView?,
                                    url: String?,
                                    message: String?,
                                    result: JsResult?
                                ): Boolean {
                                    Utility.showProgress(requireContext(), "", false)
                                    result?.confirm()
                                    //Log.e("Westpack", message)
                                    webViewPaymentWestPack.clearFormData()
                                    webViewPaymentWestPack.clearHistory()
                                    webViewPaymentWestPack.settings.cacheMode =
                                        WebSettings.LOAD_NO_CACHE
//                                    webViewPaymentWestPack.webChromeClient = null
                                    webViewPaymentWestPack.clearCache(true)
                                    webViewPaymentWestPack.reload()
                                    webViewPaymentWestPack.visibility = View.GONE
                                    if (activity is HomeActivity)
                                        (activity as HomeActivity).hideShowFooter(true)
                                    else if (activity is PaymentActivity)
                                        (activity as PaymentActivity).hideShowFooter(true)
                                    else if (activity is ProfileActivity)
                                        (activity as ProfileActivity).hideShowFooter(true)


                                    var tokenId = message!!.replace("Token: ", "").replace("\"", "")
                                    Logger.log("tokenId ->" + tokenId)
                                    callTransactionApi(tokenId)

                                    return true
                                }
                            }

                            webViewPaymentWestPack.loadUrl(
                                "file:///android_asset/" + WestpacPayment.HTTP_FILE_NAME
                            )
                            /*var addCardIntent: Intent = Intent(context, AddCardActivity::class.java)
                            addCardIntent.putExtra("orderId", orderDetails!!.id)
                            addCardIntent.putExtra("selectedItem", getSelectedItem()!!.displayName)
                            addCardIntent.putExtra("amount", orderDetails?.totalCartPrice)
                            startActivityForResult(addCardIntent, ADD_CARD_REQUEST_CODE)*/
                        } else {
                            Log.d("PAYMENT METHOD====", getSelectedItem()!!.slug)//BANK TRANSFER
                            Utility.showProgress(requireContext(), "", false)
                            cartViewModel?.updateOrder(
                                orderDetails!!.id,
                                getSelectedItem()!!.displayName,
                                "",
                                "1",
                                sharedPrefs?.quoteID
                            )
                            val items: ArrayList<Bundle> = ArrayList()
                            if (cartProducts != null && cartProducts.size > 0) {

                                Log.d("cartProducts===", cartProducts.size.toString())
                                for (i in 0 until cartProducts.size) {
                                    val price = cartProducts[i]?.price.toString()
                                    val QUANTITY = cartProducts[i]?.qty.toString()
                                    Log.d("quentity cart=", QUANTITY)
                                    val bundle = Bundle()
                                    bundle.putString(
                                        FirebaseAnalytics.Param.ITEM_ID,
                                        cartProducts[i]?.product_id.toString()
                                    )
                                    if (price != null && !price.equals("")) {
                                        bundle.putDouble(
                                            FirebaseAnalytics.Param.VALUE,
                                            cartProducts[i]?.price!!.toDouble()
                                        )

                                    }
                                    if (QUANTITY != null && !QUANTITY.contentEquals("")) {
                                        bundle.putLong(
                                            FirebaseAnalytics.Param.QUANTITY,
                                            cartProducts[i]?.qty.toLong()
                                        )

                                    }
                                    bundle.putString(
                                        FirebaseAnalytics.Param.ITEM_CATEGORY,
                                        cartProducts[i]?.product_category_id.toString()
                                    )
                                    bundle.putString(
                                        FirebaseAnalytics.Param.ITEM_NAME,
                                        cartProducts[i]?.product_name
                                    )
                                    items.add(bundle)
                                }
                            }

                            val bundle = Bundle()
                            bundle.putDouble(
                                FirebaseAnalytics.Param.VALUE,
                                orderDetails!!.totalCartPrice.toDouble()
                            )
                            bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                            bundle.putDouble(
                                FirebaseAnalytics.Param.SHIPPING,
                                orderDetails!!.shippingCost.toDouble()
                            )
                            bundle.putString(
                                FirebaseAnalytics.Param.TRANSACTION_ID,
                                "COD/BANK TRANSFER"
                            )
                            bundle.putDouble(
                                FirebaseAnalytics.Param.DISCOUNT,
                                orderDetails!!.totalDiscount.toDouble()
                            )
                            bundle.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, items)

                            // bundle.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, items)
                            mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle)

                        }
                    }
                } else {
                    shortToast(getString(R.string.terms_condition_not_selected_message))
                }
            }
            R.id.btnSave -> {
                if (isCardValid())
                    getSingleUseTokenApi()
            }
            R.id.tvExpDate -> {
                showMonthYearPicker()
            }
        }
    }


    private fun showMonthYearPicker() {
        var monthYearPicker: MonthPickerDialog.Builder = MonthPickerDialog.Builder(
            activity,
            object : MonthPickerDialog.OnDateSetListener {
                override fun onDateSet(selectedMonth: Int, selectedYear: Int) {

                    var month: Int = selectedMonth.plus(1)
                    var modifiedMonth =
                        if (month.toString().length < 2) "0${month}" else month
                    tvExpDate.text = Editable.Factory.getInstance()
                        .newEditable("${modifiedMonth}/${selectedYear % 100}")
                }
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH)
        )
        monthYearPicker.setActivatedMonth(Calendar.getInstance().get(Calendar.MONTH))
            .setActivatedYear(Calendar.getInstance().get(Calendar.YEAR))
            .setMaxYear(Calendar.getInstance().get(Calendar.YEAR) + 30)
            .setMinYear(Calendar.getInstance().get(Calendar.YEAR))

            .build().show()
        //.setMinMonth(Calendar.getInstance().get(Calendar.MONTH))
    }

    private fun isCardValid(): Boolean {
        if (TextUtils.isEmpty(edtCardNumber.text.toString().trim())) {
            (requireActivity() as AppCompatActivity).showAlert(getString(R.string.pleaseEnterCardNumber))
            return false
        } else if (edtCardNumber.toString().trim().length < 17) {
            (requireActivity() as AppCompatActivity).showAlert(getString(R.string.pleaseEnterValidCardNumber))
            return false
        } else if (TextUtils.isEmpty(edtCardHolderName.text.toString().trim())) {
            (requireActivity() as AppCompatActivity).showAlert(getString(R.string.pleaseEnterCardHolderName))
            return false
        } else if (TextUtils.isEmpty(tvExpDate.text.toString().trim())) {
            (requireActivity() as AppCompatActivity).showAlert(getString(R.string.pleaseEnterExpDate))
            return false

//        } else if (edtCvv.text.toString().trim().length > 4 && edtCvv.text.toString().trim().length < 3) {
        } else if (edtCvv.text.toString().trim().length > 4 || edtCvv.text.toString()
                .trim().length < 3
        ) {//change by nikita
            (requireActivity() as AppCompatActivity).showAlert(getString(R.string.pleaseEnterValidCvvNumber))
            return false
        }
        return true
    }

    var expDate: String? = null
    var month: String? = null
    var year: String? = null
    var cardNumber: String? = null
    var cvn: String? = null
    var cardHolderName: String? = null
    var singleUseToken: String? = null
    var merchantId: String? = null
    var customerNumber: String? = null

    private fun getSingleUseTokenApi() {
        expDate = tvExpDate.text.toString().trim()
        var list: List<String> = expDate?.split("/")!!
        month = list[0]
        year = list[1]
        cardNumber = edtCardNumber.text.toString().trim().replace(" ", "")
        cvn = edtCvv.text.toString().trim()
        cardHolderName = edtCardHolderName.text.toString().trim()

        month = if (month?.length!! < 1) "0${month}" else month


        var hashMap: HashMap<String, String> =
            getRequestBodyForSingleUseToken()

        var base64Credentials = getBase64CredentialKey(WestpacPayment.publicKey)

        var apiService: PaymentService =
            BaseService.retrofitWestpackInstance()?.create(PaymentService::class.java)!!

        var call: Call<String> =
            apiService.singleUseTokens("Basic ${base64Credentials}", hashMap)

        Utility.showProgress(requireActivity(), "", false)
        callApi(SINGLE_USE_TOKEN_API, call, this)

        //SingleUserTokenAsync(base64Credentials, postData).execute()
    }

    private fun getRequestBodyForSingleUseToken(): java.util.HashMap<String, String> {
        var map: HashMap<String, String> = HashMap<String, String>()
        map.put("paymentMethod", "creditCard")
        cardNumber?.let { it -> map.put("cardNumber", it) }
        month?.let { map.put("expiryDateMonth", it) }
        year?.let { map.put("expiryDateYear", it) }
        cvn?.let { map.put("cvn", it) }
        cardHolderName?.let { map.put("cardholderName", it) }
        return map
    }

    private fun getBase64CredentialKey(key: String): String {
        var user = key
        var password = ""
        var credentialData = "${user}:${password}".toByteArray(Charsets.UTF_8)
        var base64Credentials = Base64.encodeToString(credentialData, Base64.NO_WRAP)
        return base64Credentials
    }

    override fun onSuccess(type: String, response: String) {
        Log.e("AddCard", "res : " + response)
        when (type) {
            SINGLE_USE_TOKEN_API -> {
                var jsonResponse = JSONObject(response)
                if (jsonResponse?.has("singleUseTokenId")) {
                    singleUseToken = jsonResponse.optString("singleUseTokenId")
                    callMerchantApiCall()
                } else
                    Utility.cancelProgress()
            }
            MERCHANT_CALL_API -> {
                var jsonResponse = JSONObject(response)
                if (jsonResponse?.has("data")) {
                    var jsonArray: JSONArray = jsonResponse.getJSONArray("data")
                    var merchantObject: JSONObject = jsonArray?.getJSONObject(0)
                    if (merchantObject.has("merchantId")) {
                        merchantId = merchantObject.optString("merchantId")
                        createCustomerApiCall()
                    }

                } else
                    Utility.cancelProgress()
            }
            CREATE_CUSTOMER_API -> {
                var jsonObject = JSONObject(response)
                if (jsonObject?.has("customerNumber")) {
                    customerNumber = jsonObject.optString("customerNumber")
                    callTransactionApi("")
                } else
                    Utility.cancelProgress()
            }
            TRANSACTION_API -> {
                Utility.cancelProgress()
                var jsonObject = JSONObject(response)
                if (jsonObject.has("status") && jsonObject.optString("status").equals("approved")) {
                    Utility.showProgress(requireContext(), "", false)
                    cartViewModel?.updateOrder(
                        orderDetails!!.id,
                        getSelectedItem()!!.displayName,
                        jsonObject.toString(),
                        "1",
                        sharedPrefs?.quoteID
                    )
                    val items: ArrayList<Bundle> = ArrayList()
                    if (cartProducts != null && cartProducts.size > 0) {

                        Log.d("westpack checkout===", cartProducts.size.toString())
                        for (i in 0 until cartProducts.size) {
                            val price = cartProducts[i]?.price.toString()
                            val QUANTITY = cartProducts[i]?.qty.toString()
                            Log.d("quentity westpack=", QUANTITY)
                            val bundle = Bundle()
                            bundle.putString(
                                FirebaseAnalytics.Param.ITEM_ID,
                                cartProducts[i]?.product_id.toString()
                            )
                            if (price != null && !price.equals("")) {
                                bundle.putDouble(
                                    FirebaseAnalytics.Param.VALUE,
                                    cartProducts[i]?.price!!.toDouble()
                                )

                            }
                            if (QUANTITY != null && !QUANTITY.contentEquals("")) {
                                bundle.putLong(
                                    FirebaseAnalytics.Param.QUANTITY,
                                    cartProducts[i]?.qty.toLong()
                                )

                            }
                            bundle.putString(
                                FirebaseAnalytics.Param.ITEM_CATEGORY,
                                cartProducts[i]?.product_category_id.toString()
                            )
                            bundle.putString(
                                FirebaseAnalytics.Param.ITEM_NAME,
                                cartProducts[i]?.product_name
                            )
                            items.add(bundle)
                        }
                    }

                    val bundle = Bundle()
                    bundle.putDouble(
                        FirebaseAnalytics.Param.VALUE,
                        orderDetails!!.totalCartPrice.toDouble()
                    )
                    bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                    bundle.putDouble(
                        FirebaseAnalytics.Param.SHIPPING,
                        orderDetails!!.shippingCost.toDouble()
                    )
                    if (jsonObject.has("transactionId")) {
                        Log.d("transaction ID ==", jsonObject.optString("transactionId").toString())
                        bundle.putString(
                            FirebaseAnalytics.Param.TRANSACTION_ID,
                            jsonObject.optString("transactionId").toString()
                        )

                    }

                    bundle.putDouble(
                        FirebaseAnalytics.Param.DISCOUNT,
                        orderDetails!!.totalDiscount.toDouble()
                    )
                    bundle.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, items)

                    // bundle.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, items)
                    mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.PURCHASE, bundle)

                    /* (requireActivity() as AppCompatActivity).openAlertDialogWithOneClick(getString(R.string.app_name),
                         getString(
                             R.string.transaction_completed_message
                         ),
                         getString(
                             android.R.string.ok
                         ),
                         object : OnDialogButtonClick {
                             override fun onPositiveButtonClick() {
                                 Utility.showProgress(requireContext(), "", false)
                                 cartViewModel?.updateOrder(
                                     orderDetails!!.id,
                                     getSelectedItem()!!.displayName,
                                     jsonObject.toString(),
                                     "1",
                                     sharedPrefs?.quoteID
                                 )
                             }

                             override fun onNegativeButtonClick() {
                             }

                         })*/
                    cartViewModel!!._captureLiveData.value = null

                } else {
                    (requireActivity() as AppCompatActivity).showAlert(getString(R.string.transaction_declined))
                }
            }
        }
    }

    override fun onError(type: String, errorMessage: String) {
        Utility.cancelProgress()

        (requireActivity() as AppCompatActivity).showAlert(getString(R.string.error))
    }

    private fun callTransactionApi(tokenId: String) {
        var base64Credentials: String = getBase64CredentialKey(WestpacPayment.secretKey)
        var map: HashMap<String, String> = getRequestBodyForTransactionApi(tokenId)

        var apiService: PaymentService =
            BaseService.retrofitWestpackInstance()?.create(PaymentService::class.java)!!
        var call: Call<String> = apiService.transactionApi("Basic ${base64Credentials}", map)
        callApi(TRANSACTION_API, call, this)
    }

    private fun getRequestBodyForTransactionApi(tokenId: String): java.util.HashMap<String, String> {
        var map: HashMap<String, String> = HashMap<String, String>()
//        customerNumber?.let { map.put("customerNumber", it) }
        map.put(
            "customerNumber",
            orderDetails?.user?.firstName.toString() + "_" + orderDetails?.user?.lastName
        )
        orderDetails?.totalCartPrice?.let { map.put("principalAmount", it) }
//        merchantId?.let { map.put("merchantId", it) }
        map.put("merchantId", WestpacPayment.MERCHANT_ID)
        map.put("transactionType", "payment")
        map.put("currency", "aud")
        map.put("singleUseTokenId", tokenId) //dwi
        return map
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Logger.log("nnn: Request in Checkout:" + requestCode)
        /*   val ft : FragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()

           if (Build.VERSION.SDK_INT >= 26) {
               ft.setReorderingAllowed(false)
           }
           ft.detach(this@CheckoutFragment).attach(this@CheckoutFragment)*/
        viewSetup()
        (requireActivity() as HomeActivity).updateBottomTab()
        Logger.log("Got the checkout")
    }

    private fun createCustomerApiCall() {
        var base64Credentials: String = getBase64CredentialKey(WestpacPayment.secretKey)
        var map: HashMap<String, String> = getRequestBodyForCreateCustomer(merchantId!!)

        var apiService = BaseService.retrofitWestpackInstance()?.create(PaymentService::class.java)
        var call: Call<String> = apiService?.createCustomer("Basic ${base64Credentials}", map)!!
        callApi(CREATE_CUSTOMER_API, call, this)
    }

    private fun getRequestBodyForCreateCustomer(merchantId: String): java.util.HashMap<String, String> {
        var hashMap: HashMap<String, String> = HashMap<String, String>()
        hashMap.put("merchantId", merchantId)
        singleUseToken?.let { hashMap.put("singleUseTokenId", it) }
        return hashMap
    }

    private fun callMerchantApiCall() {
        var base64Credentials = getBase64CredentialKey(WestpacPayment.secretKey)

        var apiService: PaymentService =
            BaseService.retrofitWestpackInstance()?.create(PaymentService::class.java)!!


        var call: Call<String> =
            apiService.merchantCall("Basic ${base64Credentials}")

        callApi(MERCHANT_CALL_API, call, this)
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_CARD_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data?.hasExtra("transactionResponse")!!) {
                var transactionResponse: String = data?.extras?.getString("transactionResponse")!!
                (requireActivity() as AppCompatActivity).openAlertDialogWithOneClick(getString(R.string.app_name),
                    getString(
                        R.string.transaction_completed_message
                        *//*,it.id*//*
                    ),
                    getString(
                        android.R.string.ok
                    ),
                    object : OnDialogButtonClick {
                        override fun onPositiveButtonClick() {
                            Utility.showProgress(requireContext(), "", false)
                            cartViewModel?.updateOrder(
                                orderDetails!!.id,
                                getSelectedItem()!!.displayName,
                                transactionResponse,
                                "1"
                            )
                        }

                        override fun onNegativeButtonClick() {
                        }

                    })
                cartViewModel!!._captureLiveData.value = null
            }
        }
    }*/

    fun getSelectedItem(): PaymentMethodsResponse.PaymentMethod? {
        if (paymentMethods != null && paymentMethods.size > 0) {
            for (i in 0 until paymentMethods.size) {
                if (paymentMethods[i].checked)
                    return paymentMethods[i]
            }
        }
        return null
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (countries[position].countryCode.equals("AU")) {
            spinnerState.visibility = VISIBLE
            edtState.visibility = View.GONE
            if (states == null)
                states = ArrayList()
            states.clear()
            states.addAll(countries[position].states)
            spinnerStateAdapter = SpinnerStateAdapter(requireActivity(), states)
            spinnerState.adapter = spinnerStateAdapter
            if (shippingCountryCode.equals("AU")) {
                spinnerState.setSelection(getSelectedState())
            }
        } else {
            spinnerState.visibility = View.GONE
            edtState.visibility = VISIBLE
        }
    }
}
