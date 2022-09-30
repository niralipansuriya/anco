package com.app.ancoturf.presentation.payment.westpac

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.app.ancoturf.R
import com.app.ancoturf.data.common.ApiCallback
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.common.network.BaseService
import com.app.ancoturf.data.payment.remote.PaymentService
import com.app.ancoturf.extension.showAlert
import com.app.ancoturf.presentation.cart.CartViewModel
import com.app.ancoturf.presentation.common.base.BaseActivity
import com.app.ancoturf.presentation.payment.CardNumberValidate
import com.app.ancoturf.utils.Utility
import com.whiteelephant.monthpicker.MonthPickerDialog
import kotlinx.android.synthetic.main.add_card_activity.*
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap


class AddCardActivity : BaseActivity(){
    override fun getContentResource(): Int = R.layout.add_card_activity


    override fun viewModelSetup() {
        /*cartViewModel = ViewModelProviders.of(this, viewModelFactory)[CartViewModel::class.java]

        cartViewModel?._orderDetailsLiveData?.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                if (it.success) {

                    shortToast(it.message)
                    cartViewModel?.deleteProduct(null)
                    cartViewModel?.deleteCoupon(null)
                    cartViewModel?._productLiveData?.value = null
                    cartViewModel?._couponLiveData?.value = null

                } else {
                    shortToast(it.message)
                }
                cartViewModel!!._orderDetailsLiveData.value = null
            }
        })*/
        /*cartViewModel!!.productDeletionLiveData.observe(this, androidx.lifecycle.Observer {
            if (it != null && it) {
                Handler().postDelayed({
                    Utility.cancelProgress()
                    sharedPrefs.totalProductsInCart = 0
                    if (sharedPrefs.isLogged) {
                        pushFragment(
                            OrderFragment(),
                            false,
                            true,
                            false,
                            R.id.flContainerHome
                        )
                    } else {
                        pushFragment(
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
        })*/


    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var sharedPrefs: SharedPrefs

    var cartViewModel: CartViewModel? = null
    var orderId: Int? = null
    var displayName: String? = null
    var amount: String? = null


    override fun viewSetup() {

        /*getDataFromIntent()

        validCardNumber()

        clickListener()*/
    }

    /*private fun getDataFromIntent() {
        if (intent.hasExtra("amount")) {
            orderId = intent.extras.getInt("orderId")
            displayName = intent.extras.getString("selectedItem")
            amount = intent.extras.getString("amount")
        }
    }

    private fun clickListener() {
        imgBack.setOnClickListener(this)
        btnSave.setOnClickListener(this)
        tvExpDate.setOnClickListener(this)
    }

    private fun validCardNumber() {
        edtCardNumber.addTextChangedListener(
            CardNumberValidate(
                edtCardNumber
            )
        )

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imgBack -> finish()
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
            this,
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
            .setMinMonth(Calendar.getInstance().get(Calendar.MONTH))
            .build().show()
    }

    private fun isCardValid(): Boolean {
        if (TextUtils.isEmpty(edtCardNumber.text.toString().trim())) {
            showAlert(getString(R.string.pleaseEnterCardNumber))
            return false
        } else if (TextUtils.isEmpty(edtCardHolderName.text.toString().trim())) {
            showAlert(getString(R.string.pleaseEnterCardHolderName))
            return false
        } else if (TextUtils.isEmpty(tvExpDate.text.toString().trim())) {
            showAlert(getString(R.string.pleaseEnterExpDate))
            return false
        } else if (edtCardNumber.toString().trim().length < 17) {
            showAlert(getString(R.string.pleaseEnterValidCardNumber))
            return false
        } else if (edtCvv.text.toString().trim().length > 4 && edtCvv.text.toString().trim().length < 3) {
            showAlert(getString(R.string.pleaseEnterValidCvvNumber))
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

        Utility.showProgress(this, "", false)
        callApi(SINGLE_USE_TOKEN_API, call, this)

        //SingleUserTokenAsync(base64Credentials, postData).execute()
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
                    callTransactionApi()
                } else
                    Utility.cancelProgress()
            }
            TRANSACTION_API -> {
                Utility.cancelProgress()
                var jsonObject = JSONObject(response)
                if (jsonObject.has("status") && jsonObject.optString("status").equals("approved")) {
                    var intentResult: Intent = Intent()
                    intentResult.putExtra("transactionResponse", response)
                    setResult(Activity.RESULT_OK, intentResult)
                    finish()

                    *//*openAlertDialogWithOneClick(getString(R.string.app_name),
                        getString(
                            R.string.transaction_completed_message
                            *//**//*,it.id*//**//*
                        ),
                        getString(
                            android.R.string.ok
                        ),
                        object : OnDialogButtonClick {
                            override fun onPositiveButtonClick() {
                                Utility.showProgress(applicationContext, "", false)
                                cartViewModel?.updateOrder(
                                    orderId!!,
                                    displayName!!,
                                    response,
                                    "1"
                                )
                            }

                            override fun onNegativeButtonClick() {
                            }

                        })*//*

                } else {
                    showAlert(getString(R.string.transaction_declined))
                }
            }
        }
    }

    override fun onError(type: String, errorMessage: String) {
        Utility.cancelProgress()
        showAlert(getString(R.string.error))
    }

    private fun callTransactionApi() {
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
        amount?.let { map.put("principalAmount", it) }
        merchantId?.let { map.put("merchantId", it) }
        map.put("transactionType", "payment")
        map.put("currency", "aud")
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
    }*/
}