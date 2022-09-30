package com.app.ancoturf.presentation.payment

import android.content.Intent
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.BuildConfig
import com.app.ancoturf.R
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.cart.remote.entity.ExpressCheckoutResponse
import com.app.ancoturf.data.cart.remote.entity.PaymentMethodsResponse
import com.app.ancoturf.data.common.ApiCallback
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.common.network.BaseService
import com.app.ancoturf.data.payment.remote.PaymentService
import com.app.ancoturf.extension.*
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.cart.CartViewModel
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.payment.westpac.WestpacPayment
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Logger
import com.app.ancoturf.utils.Utility
import com.app.ancoturf.utils.interfces.OnDialogButtonClick
import com.google.gson.Gson
import com.whiteelephant.monthpicker.MonthPickerDialog
import kotlinx.android.synthetic.main.fragment_checkout.*
import kotlinx.android.synthetic.main.fragment_last_payment.*
import kotlinx.android.synthetic.main.fragment_last_payment.btnSave
import kotlinx.android.synthetic.main.fragment_last_payment.edtCardHolderName
import kotlinx.android.synthetic.main.fragment_last_payment.edtCardNumber
import kotlinx.android.synthetic.main.fragment_last_payment.edtCvv
import kotlinx.android.synthetic.main.fragment_last_payment.imgBack
import kotlinx.android.synthetic.main.fragment_last_payment.imgCheckPaymentTnC
import kotlinx.android.synthetic.main.fragment_last_payment.imgPaymentLogo
import kotlinx.android.synthetic.main.fragment_last_payment.linPaymentMethods
import kotlinx.android.synthetic.main.fragment_last_payment.scrollPayment
import kotlinx.android.synthetic.main.fragment_last_payment.tvExpDate
import kotlinx.android.synthetic.main.fragment_last_payment.txtAboveDescription
import kotlinx.android.synthetic.main.fragment_last_payment.txtBelowDescription
import kotlinx.android.synthetic.main.fragment_last_payment.txtPaymentTnC
import kotlinx.android.synthetic.main.fragment_last_payment.txtProceed
import kotlinx.android.synthetic.main.fragment_last_payment.webViewPayment
import kotlinx.android.synthetic.main.fragment_last_payment.webViewPaymentWestPack
import kotlinx.android.synthetic.main.fragment_last_payment.westpacLayout
import kotlinx.android.synthetic.main.header.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class LastPaymentFragment : BaseFragment(),
    View.OnClickListener, ApiCallback {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var paymentViewModel: PaymentViewModel? = null

    private var cartViewModel: CartViewModel? = null

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    var agreedToTNC = false
    var totalPendingAmount = 0.0F

    var paymentMethods = ArrayList<PaymentMethodsResponse.PaymentMethod>()
    private var paymentMethodsResponse: PaymentMethodsResponse? = null
    var orderIds = ""

    override fun getContentResource(): Int = R.layout.fragment_last_payment

    override fun viewModelSetup() {
        paymentViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[PaymentViewModel::class.java]

        cartViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[CartViewModel::class.java]

        initObservers()
        Utility.showProgress(context = requireContext(), message = "", cancellable = false)
//        paymentViewModel!!.callGetPendingPayment()
        paymentViewModel!!.callGetBillHistory("")
        cartViewModel?.getPaymentMethods()
        if (agreedToTNC)
            imgCheckPaymentTnC.setImageResource(R.drawable.ic_checkbox_h)
        else
            imgCheckPaymentTnC.setImageResource(R.drawable.ic_checkbox)
        // Enable Javascript
        val webSettings = webViewPayment.settings
        webSettings.javaScriptEnabled = true

        val webSettingsWestPack = webViewPaymentWestPack.settings
        webSettingsWestPack.javaScriptEnabled = true

    }

    private fun initObservers() {
        var userInfo = Gson().fromJson(sharedPrefs.userInfo, UserInfo::class.java)
        if (userInfo!!.idCmsPrivileges == Constants.WHOLESALER) {
            edtAmt.visibility = View.VISIBLE
            txtEnterAmt.visibility = View.VISIBLE
        } else {
            edtAmt.visibility = View.GONE
            txtEnterAmt.visibility = View.GONE
        }

//        paymentViewModel!!.pendingPaymentLiveData.observe(this, Observer {
        paymentViewModel!!.billHistoryLiveData.observe(this, Observer {
            paymentViewModel!!._errorLiveData.value = null
            if (it != null) {
                Log.e("resp", it.toString())

                for (response in it.data) {
                    orderIds = orderIds + "," + response.id
                }

                totalPendingAmount = it.total_pending_amount
                txtPendingPaymentAmount.text = activity?.getString(
                    R.string.pending_amount_price,
                    Utility.formatNumber(Utility.roundTwoDecimals(it.total_pending_amount).toFloat())
                )
                edtAmt.setText("${Utility.roundTwoDecimals(it.total_pending_amount)}")
            } else {
                txtPendingPaymentAmount.text =
                    activity?.getString(R.string.pending_amount_price, " 0.0")
            }

        })
        paymentViewModel!!.updatePaymentLiveData.observe(this, Observer {
            if (it != null) {
                shortToast(it.message)
//                paymentViewModel!!._billHistoryLiveData.value = null
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }

        })
        paymentViewModel!!.errorLiveData.observe(this, Observer {
            if (it != null) {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }
        })

        cartViewModel!!.paymentMethodsLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                paymentMethodsResponse = it
                if (paymentMethods == null)
                    paymentMethods = ArrayList()

                for (response in it.paymentMethod) {
                    if (response.isOnlinePayment == 1)
                        paymentMethods.add(response)
                }
                paymentMethods[0].checked = true
                txtProceed.text = getString(R.string.proceed, paymentMethods[0].displayName)
                setPaymentMethodsScroll()
                cartViewModel!!._paymentMethodsLiveData.value = null
            }
        })

        cartViewModel!!.ordersLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                webViewPayment.visibility = View.VISIBLE
                (activity as PaymentActivity).hideShowFooter(false)
                webViewPayment.settings.javaScriptEnabled = true
                webViewPayment.loadUrl(String.format(BuildConfig.AFTERPAY_CHECKOUT_URL, it.token, ""))
                webViewPayment.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
//                        webViewPayment.loadUrl(request?.url?.toString())
                        if (request!!.url.toString().contains("https://www.google.com")) {
                            webViewPayment.visibility = View.GONE
                            (activity as PaymentActivity).hideShowFooter(true)
                            val webView = activity!!.findViewById<WebView>(R.id.webViewPayment)

                          //  webViewPayment.webViewClient = null
                            Utility.showProgress(requireContext(), "", false)
                            cartViewModel?.afterpayCapture(it.token, BuildConfig.AFTERPAY_USER)
                        } else if (request.url.toString().contains("https://www.wikipedia.org")) {
                            (requireActivity() as AppCompatActivity).showAlert(Constants.AFTERPAY_ERROR_MESSAGE)
                        }
//                        return super.shouldOverrideUrlLoading(view, request)
                        return true
                    }

                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                        webViewPayment.loadUrl(url)
                        if (url!!.contains("https://www.google.com")) {
                            webViewPayment.visibility = View.GONE
                            (activity as PaymentActivity).hideShowFooter(true)
                          //  webViewPayment.webViewClient = null
                            Utility.showProgress(requireContext(), "", false)
                            cartViewModel?.afterpayCapture(it.token, BuildConfig.AFTERPAY_USER)
                        } else if (url.contains("https://www.wikipedia.org")) {
                            (requireActivity() as AppCompatActivity).showAlert(Constants.AFTERPAY_ERROR_MESSAGE)
                        }
//                        return super.shouldOverrideUrlLoading(view, request)
                        return true
                    }
                }
            }
        })

        cartViewModel!!.captureLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()

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
                            paymentViewModel?.updatePayment(
                                orderIds.substring(1),
                                getSelectedItem()!!.displayName,
                                it.toString()
                            )
                        }

                        override fun onNegativeButtonClick() {
                        }

                    })
            }
        })


        cartViewModel!!.errorLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                shortToast(it)
                cartViewModel!!._errorLiveData.value = null
            }
        })


    }

    override fun viewSetup() {

        if (activity is HomeActivity) {
            (activity as HomeActivity).hideShowFooter(true)
            (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, false)
        } else if (activity is ProfileActivity) {
            (activity as ProfileActivity).hideShowFooter(true)
            (activity as ProfileActivity).showCartDetails(imgCart, txtCartProducts, false)
        } else if (activity is PaymentActivity) {
            (activity as PaymentActivity).hideShowFooter(true)
            (activity as PaymentActivity).showCartDetails(imgCart, txtCartProducts, false)
        }
        edtCardNumber.addTextChangedListener(CardNumberValidate(edtCardNumber))

        imgLogo.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgBack.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgCheckPaymentTnC.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        tvExpDate.setOnClickListener(this)
        txtProceed.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {
            R.id.imgBell -> {
                openNotificationFragment()
            }
            R.id.imgBack -> {
                requireActivity().supportFragmentManager.popBackStack()
            }

            R.id.imgLogo -> {
                requireActivity().startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finishAffinity()
            }


            R.id.imgMore -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ContactFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
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

            R.id.btnSave -> {
                if (isCardValid())
                    getSingleUseTokenApi()
            }


            R.id.tvExpDate -> {
                showMonthYearPicker()
            }

            R.id.imgCart -> {
                if (sharedPrefs.totalProductsInCart > 0) {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        CartFragment(),
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                } else {
                    shortToast(getString(R.string.no_product_in_cart_message))
                }
            }

            R.id.txtProceed -> {
                if (edtAmt.text.toString().toInt() > totalPendingAmount) {
                    (requireActivity() as AppCompatActivity).showAlert(getString(R.string.greater_amount_payment_msg))
                } else if (!agreedToTNC) {
                    shortToast(getString(R.string.terms_condition_not_selected_message))
                } else {
                    if (getSelectedItem() != null) {

                        var userInfo = Gson().fromJson(sharedPrefs.userInfo, UserInfo::class.java)
                        if (getSelectedItem()!!.slug == Constants.EXPRESS_CHECKOUT) {
                            webViewPayment.visibility = View.VISIBLE
                            (activity as PaymentActivity).hideShowFooter(false)
                            webViewPayment.webChromeClient = object : WebChromeClient() {
                                override fun onJsAlert(
                                    view: WebView?,
                                    url: String?,
                                    message: String?,
                                    result: JsResult?
                                ): Boolean {
                                    //Log.e("Express Checkout", message)
                                    result?.confirm()
                                    webViewPayment.visibility = View.GONE
                                    (activity as PaymentActivity).hideShowFooter(true)
                                    webViewPayment.webChromeClient = null
                                    var expressCheckoutResponse = Gson().fromJson(
                                        message!!.replace("Transaction completed by ", "").replace(
                                            "!",
                                            ""
                                        ), ExpressCheckoutResponse::class.java
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
                                                Utility.showProgress(requireContext(), "", false)
                                                paymentViewModel?.updatePayment(
                                                    orderIds.substring(1),
                                                    getSelectedItem()!!.displayName,
                                                    message.replace(
                                                        "Transaction completed by ",
                                                        ""
                                                    ).replace("!", "")
                                                )
                                            }

                                            override fun onNegativeButtonClick() {
                                            }

                                        })
                                    return true
                                }
                            }
                            webViewPayment.loadUrl(
                                String.format(
                                    Constants.EXPRESS_CHECKOUT_LINK,
                                    Utility.roundTwoDecimals(totalPendingAmount)
                                )
                            )

                        } else if (getSelectedItem()!!.slug == Constants.AFTERPAY) {
                            Utility.showProgress(requireContext(), "", false)
                            cartViewModel?.afterpayOrders(
                                totalPendingAmount.toString(),
                                "AUD",
                                userInfo.phoneNumber,
                                userInfo.firstName,
                                userInfo.lastName,
                                userInfo.email
                            )
                        }else if (getSelectedItem()!!.slug == Constants.WESTPEC_PAY) {

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
                                  //  Log.e("Westpack", message)
                                    webViewPaymentWestPack.clearFormData()
                                    webViewPaymentWestPack.clearHistory()
                                    webViewPaymentWestPack.settings.cacheMode = WebSettings.LOAD_NO_CACHE
                                    webViewPaymentWestPack.clearCache(true)
                                    webViewPaymentWestPack.reload()
                                    webViewPaymentWestPack.visibility = View.GONE
                                 //   Log.e("Westpack", message)
                                    webViewPaymentWestPack.visibility = View.GONE
                                    if (activity is HomeActivity)
                                        (activity as HomeActivity).hideShowFooter(true)
                                    else if (activity is PaymentActivity)
                                        (activity as PaymentActivity).hideShowFooter(true)
                                    else if (activity is ProfileActivity)
                                        (activity as ProfileActivity).hideShowFooter(true)
                                    webViewPaymentWestPack.webChromeClient = null
                                    webViewPaymentWestPack.clearCache(true)
                                    webViewPaymentWestPack.reload()

                                    var tokenId = message!!.replace("Token: ", "").replace("\"", "")
                                    Logger.log("tokenId ->"+ tokenId)
                                    callTransactionApi(tokenId)

                                    return true
                                }
                            }
                            webViewPaymentWestPack.clearFormData()
                            webViewPaymentWestPack.clearHistory()
                            webViewPaymentWestPack.clearCache(true)
//                            webViewPaymentWestPack.setCacheMode(WebSettings.LOAD_DEFAULT)
                            webViewPaymentWestPack.settings.cacheMode = WebSettings.LOAD_NO_CACHE
                            webViewPaymentWestPack.reload()
                            webViewPaymentWestPack.loadUrl(
                                "file:///android_asset/"+ WestpacPayment.HTTP_FILE_NAME
                            )
                            /*var addCardIntent: Intent = Intent(context, AddCardActivity::class.java)
                            addCardIntent.putExtra("orderId", orderDetails!!.id)
                            addCardIntent.putExtra("selectedItem", getSelectedItem()!!.displayName)
                            addCardIntent.putExtra("amount", orderDetails?.totalCartPrice)
                            startActivityForResult(addCardIntent, ADD_CARD_REQUEST_CODE)*/
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

        }
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
        Utility.showProgress(activity!!, "", false)
        callApi(SINGLE_USE_TOKEN_API, call, this)
        //SingleUserTokenAsync(base64Credentials, postData).execute()
    }

    private fun getBase64CredentialKey(key: String): String {
        var user = key
        var password = ""
        var credentialData = "${user}:${password}".toByteArray(Charsets.UTF_8)
        var base64Credentials = Base64.encodeToString(credentialData, Base64.NO_WRAP)
        return base64Credentials
    }

    private fun getRequestBodyForSingleUseToken(): java.util.HashMap<String, String> {
        var map: HashMap<String, String> = HashMap<String, String>()
        map.put("paymentMethod", "creditCard")
        cardNumber?.let { map.put("cardNumber", it) }
        month?.let { map.put("expiryDateMonth", it) }
        year?.let { map.put("expiryDateYear", it) }
        cvn?.let { map.put("cvn", it) }
        cardHolderName?.let { map.put("cardholderName", it) }
        return map
    }


    fun getSelectedItem(): PaymentMethodsResponse.PaymentMethod? {
        if (paymentMethods != null && paymentMethods.size > 0) {
            for (i in 0 until paymentMethods.size) {
                if (paymentMethods[i].checked)
                    return paymentMethods[i]
            }
        }
        return null
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
//            .setMinMonth(Calendar.getInstance().get(Calendar.MONTH))
            .build().show()
    }


    private fun isCardValid(): Boolean {
        if (edtAmt.text.toString().toInt() > totalPendingAmount) {
            (requireActivity() as AppCompatActivity).showAlert(getString(R.string.greater_amount_payment_msg))
            return false
        } else if (TextUtils.isEmpty(edtCardNumber.text.toString().trim())) {
            (requireActivity() as AppCompatActivity).showAlert(getString(R.string.pleaseEnterCardNumber))
            return false
        } else if (TextUtils.isEmpty(edtCardHolderName.text.toString().trim())) {
            (requireActivity() as AppCompatActivity).showAlert(getString(R.string.pleaseEnterCardHolderName))
            return false
        } else if (TextUtils.isEmpty(tvExpDate.text.toString().trim())) {
            (requireActivity() as AppCompatActivity).showAlert(getString(R.string.pleaseEnterExpDate))
            return false
        } else if (edtCardNumber.toString().trim().length < 17) {
            (requireActivity() as AppCompatActivity).showAlert(getString(R.string.pleaseEnterValidCardNumber))
            return false
        } else if (edtCvv.text.toString().trim().length > 4 && edtCvv.text.toString().trim().length < 3) {
            (requireActivity() as AppCompatActivity).showAlert(getString(R.string.pleaseEnterValidCvvNumber))
            return false
        }
        return true
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
                    /*(requireActivity() as AppCompatActivity).openAlertDialogWithOneClick(getString(R.string.app_name),
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
//                                    orderDetails!!.id,
                                    0,
                                    getSelectedItem()!!.displayName,
                                    jsonObject.toString(),
                                    "1"
                                )
                            }

                            override fun onNegativeButtonClick() {
                            }

                        })
                    cartViewModel!!._captureLiveData.value = null*/
                    Utility.showProgress(requireContext(), "", false)
                    paymentViewModel?.updatePayment(
                        "0",
                        getSelectedItem()!!.displayName,
                        jsonObject.toString()
                    )
                   /* (requireActivity() as AppCompatActivity).openAlertDialogWithOneClick(getString(R.string.app_name),
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
                                paymentViewModel?.updatePayment(
                                    "0",
                                    getSelectedItem()!!.displayName,
                                    jsonObject.toString()
                                )
                            }

                            override fun onNegativeButtonClick() {
                            }

                        })*/

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


 /*   private fun callTransactionApi(tokenId: String) {
        var base64Credentials: String = getBase64CredentialKey(WestpacPayment.secretKey)
        var map: HashMap<String, String> = getRequestBodyForTransactionApi()

        var apiService: PaymentService =
            BaseService.retrofitWestpackInstance()?.create(PaymentService::class.java)!!
        var call: Call<String> = apiService.transactionApi("Basic ${base64Credentials}", map)
        callApi(TRANSACTION_API, call, this)
    }

    private fun getRequestBodyForTransactionApi(): java.util.HashMap<String, String> {
        var map: HashMap<String, String> = HashMap<String, String>()
        customerNumber?.let { map.put("customerNumber", it) }
        map.put("principalAmount", "${edtAmt.text}")
        merchantId?.let { map.put("merchantId", it) }
        map.put("transactionType", "payment")
        map.put("currency", "aud")
        return map
    }
*/

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
        var userInfo = Gson().fromJson(sharedPrefs.userInfo, UserInfo::class.java)

        map["customerNumber"] = userInfo.firstName+"_"+userInfo.lastName
        map["principalAmount"] = "${edtAmt.text}"
//        merchantId?.let { map.put("merchantId", it) }
        map["merchantId"] = WestpacPayment.MERCHANT_ID
        map["transactionType"] = "payment"
        map["currency"] = "aud"
        map["singleUseTokenId"] = tokenId //dwi
        return map
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
                view.visibility = if (paymentMethods[i].checked) View.VISIBLE else View.GONE
                if (paymentMethods[i].checked) {
                    if (paymentMethods[i].slug == Constants.EXPRESS_CHECKOUT) {
                        imgPaymentLogo.visibility = View.VISIBLE
//                        txtAboveDescription.visibility = View.GONE
//                        txtBelowDescription.visibility = View.VISIBLE
                        txtAboveDescription.visibility = View.VISIBLE
                        txtBelowDescription.visibility = View.GONE

                        imgCheckPaymentTnC.visibility = VISIBLE
                        txtPaymentTnC.visibility = VISIBLE
                        txtProceed.visibility = VISIBLE
                        westpacLayout.visibility = GONE

                        imgPaymentLogo.setImageResource(R.drawable.img_paypal)
                        txtProceed.text =
                            getString(R.string.continue_to, paymentMethods[i].displayName)
                        /*if (!Utility.isValueNull(paymentMethods[i].descriptions)) {
//                            txtBelowDescription.loadData(
                            txtAboveDescription.loadData(
                                paymentMethods[i].descriptions.trim(),
                                "text/html",
                                "UTF-8"
                            )
                        }*/

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            txtAboveDescription.text = Html.fromHtml(
                                paymentMethods[i].descriptions.trim(),
                                Html.FROM_HTML_MODE_COMPACT
                            )
                        } else {
                            txtAboveDescription.text =
                                Html.fromHtml(paymentMethods[i].descriptions.trim())
                        }
                    } else if (paymentMethods[i].slug == Constants.AFTERPAY) {
                        imgCheckPaymentTnC.visibility = VISIBLE
                        txtPaymentTnC.visibility = VISIBLE
                        txtProceed.visibility = VISIBLE
                        westpacLayout.visibility = GONE

                        imgPaymentLogo.visibility = View.VISIBLE
//                        txtAboveDescription.visibility = View.GONE
//                        txtBelowDescription.visibility = View.VISIBLE
                        txtAboveDescription.visibility = View.VISIBLE
                        txtBelowDescription.visibility = View.GONE
                        imgPaymentLogo.setImageResource(R.drawable.img_afterpay)
                        txtProceed.text =
                            getString(R.string.continue_to, paymentMethods[i].displayName)
                     /*   if (!Utility.isValueNull(paymentMethods[i].descriptions)) {
//                            txtBelowDescription.loadData(
                            txtAboveDescription.loadData(
                                paymentMethods[i].descriptions.trim(),
                                "text/html",
                                "UTF-8"
                            )
                        }*/
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            txtAboveDescription.text = Html.fromHtml(
                                paymentMethods[i].descriptions.trim(),
                                Html.FROM_HTML_MODE_COMPACT
                            )
                        } else {
                            txtAboveDescription.text =
                                Html.fromHtml(paymentMethods[i].descriptions.trim())
                        }
                    } else if (paymentMethods[i].slug == Constants.COD || paymentMethods[i].slug == Constants.BANK_TRANSFER || paymentMethods[i].slug == Constants.PAY_LATER
                    ) {
                        imgPaymentLogo.visibility = View.GONE
                        txtAboveDescription.visibility = View.VISIBLE
                        txtProceed.visibility = VISIBLE
                        txtBelowDescription.visibility = View.GONE

                        imgCheckPaymentTnC.visibility = VISIBLE
                        txtPaymentTnC.visibility = VISIBLE
                        westpacLayout.visibility = GONE

                        txtProceed.text =
                            getString(R.string.continue_to, paymentMethods[i].displayName)
                      /*  if (!Utility.isValueNull(paymentMethods[i].descriptions)) {
                            txtAboveDescription.loadData(
                                paymentMethods[i].descriptions.trim(),
                                "text/html",
                                "UTF-8"
                            )
                        }*/

                    } else if (paymentMethods[i].slug == Constants.WESTPEC_PAY
                    ) {
                        //imgPaymentLogo.setImageResource(R.drawable.westpac)
                   /*     westpacLayout.visibility = VISIBLE
                        txtProceed.visibility = GONE
                        imgPaymentLogo.visibility = GONE
                        txtAboveDescription.visibility = GONE
                        txtBelowDescription.visibility = GONE

                        imgCheckPaymentTnC.visibility = GONE
                        txtPaymentTnC.visibility = GONE


                        txtProceed.text =
                            getString(R.string.continue_to, paymentMethods[i].displayName)
                        if (!Utility.isValueNull(paymentMethods[i].descriptions)) {
                            txtAboveDescription.loadData(
                                paymentMethods[i].descriptions.trim(),
                                "text/html",
                                "UTF-8"
                            )
                        }*/

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
                        scrollPayment.smoothScrollTo(
                            linPaymentMethods.left + linPaymentMethods.getChildAt(i).right,
                            linPaymentMethods.top
                        )
                    }
                }

            }
        }
    }


    /*private fun setPaymentMethodsScroll() {
        if (paymentMethods != null && paymentMethods.size > 0) {
            linPaymentMethods.removeAllViews()
            for (i in 0 until paymentMethods.size) {
                val paymentMethodView: View =
                    LayoutInflater.from(activity)
                        .inflate(R.layout.item_scroll_payment_methods, null)
                val txtTitle: TextView = paymentMethodView.findViewById(R.id.txtTitle)
                val view: View = paymentMethodView.findViewById(R.id.view)
                txtTitle.text = paymentMethods[i].displayName
                view.visibility = if (paymentMethods[i].checked) View.VISIBLE else View.GONE
                if (paymentMethods[i].checked) {

                    if (paymentMethods[i].slug.equals(Constants.EXPRESS_CHECKOUT)) {
                        imgPaymentLogo.visibility = View.VISIBLE
//                        txtAboveDescription.visibility = View.GONE
//                        txtBelowDescription.visibility = View.VISIBLE
                        txtAboveDescription.visibility = View.VISIBLE
                        txtBelowDescription.visibility = View.GONE

                        imgCheckPaymentTnC.visibility = VISIBLE
                        txtPaymentTnC.visibility = VISIBLE
                        txtProceed.visibility = VISIBLE
                        westpacLayout.visibility = GONE

                        imgPaymentLogo.setImageResource(R.drawable.img_paypal)
                        txtProceed.text =
                            getString(R.string.continue_to, paymentMethods[i].displayName)
                        if (!Utility.isValueNull(paymentMethods[i].descriptions)) {
//                            txtBelowDescription.loadData(
                            txtAboveDescription.loadData(
                                paymentMethods[i].descriptions.trim(),
                                "text/html",
                                "UTF-8"
                            )
                        }
                    } else if (paymentMethods[i].slug.equals(Constants.EXPRESS_CHECKOUT)) {
                        imgPaymentLogo.visibility = View.VISIBLE
//                        txtAboveDescription.visibility = View.GONE
//                        txtBelowDescription.visibility = View.VISIBLE
                        txtAboveDescription.visibility = View.VISIBLE
                        txtBelowDescription.visibility = View.GONE
                        imgPaymentLogo.setImageResource(R.drawable.img_paypal)
                        txtProceed.text =
                            getString(R.string.continue_to, paymentMethods[i].displayName)
                        if (!Utility.isValueNull(paymentMethods[i].descriptions)) {
//                            txtBelowDescription.loadData(
                            txtAboveDescription.loadData(
                                paymentMethods[i].descriptions.trim(),
                                "text/html",
                                "UTF-8"
                            )
                        }
                    } else if (paymentMethods[i].slug.equals(Constants.AFTERPAY)) {
                        imgPaymentLogo.visibility = View.VISIBLE
//                        txtAboveDescription.visibility = View.GONE
//                        txtBelowDescription.visibility = View.VISIBLE
                        txtAboveDescription.visibility = View.VISIBLE
                        txtBelowDescription.visibility = View.GONE
                        imgPaymentLogo.setImageResource(R.drawable.img_afterpay)
                        txtProceed.text =
                            getString(R.string.continue_to, paymentMethods[i].displayName)
                        if (!Utility.isValueNull(paymentMethods[i].descriptions)) {
//                            txtBelowDescription.loadData(
                            txtAboveDescription.loadData(
                                paymentMethods[i].descriptions.trim(),
                                "text/html",
                                "UTF-8"
                            )
                        }
                    } else if (paymentMethods[i].slug.equals(Constants.COD) || paymentMethods[i].slug.equals(
                            Constants.BANK_TRANSFER
                        ) || paymentMethods[i].slug.equals(Constants.PAY_LATER)
                    ) {
                        imgPaymentLogo.visibility = View.GONE
                        txtAboveDescription.visibility = View.VISIBLE
                        txtBelowDescription.visibility = View.GONE
                        txtProceed.text =
                            getString(R.string.continue_to, paymentMethods[i].displayName)
                        if (!Utility.isValueNull(paymentMethods[i].descriptions)) {
                            txtAboveDescription.loadData(
                                paymentMethods[i].descriptions.trim(),
                                "text/html",
                                "UTF-8"
                            )
                        }
                    }
                }
                if (paymentMethods[i].slug.equals(Constants.COD) || paymentMethods[i].slug.equals(
                        Constants.BANK_TRANSFER
                    ) || paymentMethods[i].slug.equals(Constants.PAY_LATER) || paymentMethods[i].slug.equals(
                        Constants.AFTERPAY
                    ) || paymentMethods[i].slug.equals(Constants.EXPRESS_CHECKOUT)
                )
                    linPaymentMethods.addView(paymentMethodView)
                paymentMethodView.setOnClickListener {
                    if (!paymentMethods[i].checked) {
                        for (j in 0 until paymentMethods.size) {
                            paymentMethods[j].checked = false
                        }
                        paymentMethods[i].checked = true
                        setPaymentMethodsScroll()
                        if (linPaymentMethods != null) {

                            scrollPayment.smoothScrollTo(
                                linPaymentMethods.left + linPaymentMethods.getChildAt(i).right,
                                linPaymentMethods.top
                            )
                        }
                    }
                }

            }
        }
    }*/

}
