package com.app.ancoturf.presentation.splash

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.extension.openAlertDialogWithOneClick
import com.app.ancoturf.presentation.cart.CartViewModel
import com.app.ancoturf.presentation.common.base.BaseActivity
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.login.LoginActivity
import com.app.ancoturf.presentation.welcome.WelcomeActivity
import com.app.ancoturf.presentation.welcomeOnBoard.WelcomeOnBoardActivity
import com.app.ancoturf.utils.Logger
import com.app.ancoturf.utils.Utility
import com.app.ancoturf.utils.interfces.OnDialogButtonClick
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.gson.Gson
import org.json.JSONObject
import javax.inject.Inject

class SplashActivity : BaseActivity() {

    private var reachedInHandler: Boolean = false
    private var reachedInSuccess: Boolean = false

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var splashViewModel: SplashViewModel? = null

    var cartViewModel: CartViewModel? = null

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    var splashTimeOUt = 2000
    override fun getContentResource(): Int = R.layout.activity_splash
    override fun viewSetup() {
        sharedPrefs.isHomeOpen = false
        splashViewModel!!.callGetSettings()
        //createDynamicUri(createShareUri("276","Sent"))
        if (intent?.hasExtra(Constants.FROM_NOTIFICATION)!! && sharedPrefs?.isLogged) {
            val data = JSONObject(intent.extras!!.getString(Constants.NOTIFICATION_DATA))
            if (data.has("slug") && data.optString("slug") == "order_status_changed" || data.optString(
                    "slug"
                ) == "ready_for_pickup"
                || data.optString("slug") == "order_reschedule_admin" || data.optString("slug") == "order_on_the_way"
            ) {
                Logger.log("nnn line 60")
                var i = Intent(this, HomeActivity::class.java)
                i.putExtra(Constants.ORDER_STATUS_CHANGED, true)
                i.putExtra(Constants.REFERENCE_NUMBER, data.optString("reference_number"))
                startActivity(i)
                finishAffinity()
            } else if (data.has("slug") && data.optString("slug") == "approved_landscaper_role" || data.optString(
                    "slug"
                ) == "user_role_changed"
            ) {
                var fcmToken = sharedPrefs.fcmToken
                sharedPrefs.clear()
                sharedPrefs.isWelcomeVisited = true
                sharedPrefs.fcmToken = fcmToken
                Log.e("accessToken", sharedPrefs.accessToken)
                cartViewModel?.deleteProduct(null)
                cartViewModel?.deleteCoupon(null)
//            Handler().postDelayed({
                var intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finishAffinity()
            } else if (data.has("slug") && data.optString("slug") == "payment_due_today" || data.optString(
                    "slug"
                ) == "payment_due"
                || data.optString("slug") == "payment_due_after"
            ) {

                var i = Intent(this, HomeActivity::class.java)
                i.putExtra(Constants.PAYMENT_DUE_TYPE, true)
                startActivity(i)
                finishAffinity()
            } else {
                startActivity(Intent(this, HomeActivity::class.java))
                finishAffinity()
            }/*Bundle[{fromNotification=true, NOTIFICATION_DATA={"content":"hi test","title":"Hi test","device_type":"Android","to":"Sharp"}}]*/
        } else if(intent?.hasExtra(Constants.FROM_NOTIFICATION)!!){
            startActivity(Intent(this, HomeActivity::class.java))
            finishAffinity()
        }
        else {
            handleFirebaseDeepLink()
        }
    }

    var isFromDeepLink = false
    private fun handleFirebaseDeepLink() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(this, OnSuccessListener {
                var deepLink: Uri? = null
                if (it != null) {
                    isFromDeepLink = true
                    deepLink = it.link
                    handleDeepLinkUrl(deepLink)
                } else
                    isFromDeepLink = false
            }).addOnFailureListener(this, OnFailureListener {
                isFromDeepLink = false
                Handler().postDelayed({
                    if (!reachedInSuccess) {
                        reachedInHandler = true
                        Log.e("call", "reachedInHandler")
                        moveToNext()
                    }
                }, splashTimeOUt.toLong())
            })
    }

    private fun handleDeepLinkUrl(deepLink: Uri?) {
        if (deepLink?.toString()!!.contains("order")) {
            var orderReferenceNumber =
                deepLink?.getQueryParameter(Constants.ORDER_REFERENCE_NUMBER_PARAM)
            if (orderReferenceNumber != null) {
                var i = Intent(this, HomeActivity::class.java)
                i.putExtra(Constants.ORDER_STATUS_CHANGED, true)
                i.putExtra(Constants.REFERENCE_NUMBER, orderReferenceNumber)
                startActivity(i)
                finishAffinity()
            }else {
                moveToNext()
            }
        }//https://ancoapp.page.link/products?product_id=9
        else if(deepLink?.toString()!!.contains(getString(R.string.config_path_products))){
            var productNumber =
                deepLink?.getQueryParameter(Constants.PRODUCT_ID_PARAM)
            if (productNumber != null) {
                var i = Intent(this, HomeActivity::class.java)
//                i.putExtra(Constants.ORDER_STATUS_CHANGED, true)
                i.putExtra(Constants.KEY_DEEP_LINKING, true)
                i.putExtra(Constants.PRODUCT_ID_PARAM, productNumber)

                startActivity(i)
                finishAffinity()
            }else {
                moveToNext()
            }
        }
        else {
            var quoteId = deepLink?.getQueryParameter(Constants.QUOTE_ID_PARAM)
            var quoteStatus = deepLink?.getQueryParameter(Constants.QUOTE_STATUS_PARAM)

//        var quoteId = 288
//        var quoteStatus = "Sent"
            if (quoteId != null && quoteStatus != null) {
                if (sharedPrefs?.isLogged && sharedPrefs?.userType != Constants.RETAILER) {
                    var homeIntent = Intent(this, HomeActivity::class.java)
                    homeIntent.putExtra(Constants.KEY_QUOTE_ID, quoteId)
                    homeIntent.putExtra(Constants.KEY_QUOTE_STATUS, quoteStatus)
                    homeIntent.putExtra(Constants.KEY_DEEP_LINKING, true)
                    startActivity(homeIntent)
                    finishAffinity()
                } else {
                    moveToNext()
                }
            }else {
                moveToNext()
            }
        }
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }

    override fun viewModelSetup() {
        splashViewModel = ViewModelProviders.of(this, viewModelFactory)[SplashViewModel::class.java]
        cartViewModel = ViewModelProviders.of(this, viewModelFactory)[CartViewModel::class.java]
        initObservers()
    }

    private fun initObservers() {
        splashViewModel!!.settingsLiveData.observe(this, Observer {
            addToPref(it.data)
            if (!reachedInHandler) {
                reachedInSuccess = true
                Log.e("call", "reachedInSuccess")
                if (!isFromDeepLink && !intent?.hasExtra(Constants.FROM_NOTIFICATION)!!)
                    moveToNext()
                //handleFirebaseDeepLink()
            }
            if (!isFromDeepLink && !intent?.hasExtra(Constants.FROM_NOTIFICATION)!!)
                finish()
        })
        splashViewModel!!.errorLiveData.observe(this, Observer {
            if (!Utility.isValueNull(splashViewModel!!.errorLiveData.value)) {
                it?.let { it1 ->
                    openAlertDialogWithOneClick(
                        title = getString(R.string.app_name),
                        message = it1,
                        positiveButton = getString(android.R.string.ok),
                        onDialogButtonClick = object : OnDialogButtonClick {
                            override fun onPositiveButtonClick() {
                                finish()
                            }
                            override fun onNegativeButtonClick() {
                            }
                        })
                }
                splashViewModel!!._errorLiveData.value = null
            }
        })
    }

    private fun moveToNext() {
        Logger.log("nnn line 229")
        if (sharedPrefs.isLogged) {
            if (sharedPrefs.isHowCanWeHelp) {
                Logger.log("nnn line 232")

                var i = Intent(this, HomeActivity::class.java)
                startActivity(i)
            }else {
                Logger.log("nnn line 236")

                var i = Intent(this, WelcomeOnBoardActivity::class.java)
                startActivity(i)
            }
        } else {
            Logger.log("nnn line 243")

            if (sharedPrefs.isWelcomeVisited) {
                Logger.log("nnn line 246")

                if (sharedPrefs.isHowCanWeHelp) {
                    Logger.log("nnn line 248")

                    var i = Intent(this, LoginActivity::class.java)
                    startActivity(i)
                }else {
                    Logger.log("nnn line 254")

                    var i = Intent(this, WelcomeOnBoardActivity::class.java)
                    startActivity(i)
                }
            /*    var i = Intent(this, LoginActivity::class.java)
                startActivity(i)*/
            } else {
                Logger.log("nnn line 262")

                if (sharedPrefs.isHowCanWeHelp) {
                    Logger.log("nnn line 265")

                    var i = Intent(this, WelcomeActivity::class.java)
                    startActivity(i)
                }else {
                    Logger.log("nnn line 270")

                    var i = Intent(this, WelcomeOnBoardActivity::class.java)
                    startActivity(i)
                }
             /*   var i = Intent(this, WelcomeActivity::class.java)
                startActivity(i)*/
            }
        }
    }

    private fun addToPref(data: SettingsResponse.Data?) {
        sharedPrefs.signupInfo =
            Gson().toJson(
                data!!.signupScreenSetting,
                SettingsResponse.Data.SignupScreenSetting::class.java
            )
        sharedPrefs.signinInfo =
            Gson().toJson(
                data!!.signinScreenSetting,
                SettingsResponse.Data.SigninScreenSetting::class.java
            )
        sharedPrefs.onboardingInfo =
            Gson().toJson(
                data!!.onboardingScreenSetting,
                SettingsResponse.Data.OnboardingScreenSetting::class.java
            )
        sharedPrefs.contactInfo =
            Gson().toJson(
                data!!.contactScreenSetting,
                SettingsResponse.Data.ContactScreenSetting::class.java
            )
        var productMaxPrice = data?.product?.maxPrice
        sharedPrefs.product_max_price = "$productMaxPrice"
        var quoteMaxPrice = data?.quote?.maxPrice
        sharedPrefs.quote_max_price = "$quoteMaxPrice"
        var orderMaxPrice = data?.orders?.maxPrice
        sharedPrefs.order_max_price = "$orderMaxPrice"
        sharedPrefs.max_number_portfolios = data?.ancoSettings?.maxNumberPortfolios
        sharedPrefs.max_images_non_anco_products_quote =
            data?.ancoSettings?.maxImagesNonAncoProductsQuote
        sharedPrefs.max_portfolio_images = data?.ancoSettings?.maxPortfolioImages
        sharedPrefs.deliveryMinDays = data?.ancoSettings?.deliveryMinDays
        sharedPrefs.deliveryMaxDays = data?.ancoSettings?.deliveryMaxDays
        sharedPrefs.deliveryMessage = data?.ancoSettings?.deliveryMessage
        sharedPrefs.tags = Gson().toJson(data!!.tags)
        sharedPrefs.productUnitsInfo = Gson().toJson(data!!.productUnits)
        sharedPrefs.orderStatues = Gson().toJson(data!!.orderStatuses)
        sharedPrefs.deliveryStatues = Gson().toJson(data!!.deliveryStatuses)
        sharedPrefs.quoteToOrderMessage = data?.ancoSettings?.quoteToOrderMessage
        sharedPrefs.dropTurfOnPropertyTitle = data?.ancoSettings?.dropTurfOnPropertyTitle
        sharedPrefs.dropTurfOnPropertyDescription = data?.ancoSettings?.dropTurfOnPropertyDescription
        sharedPrefs.acceptDeclineCheckboxTitle = data?.ancoSettings?.acceptDeclineCheckboxTitle
        sharedPrefs.clickAndCollectCheckboxTitle = data?.ancoSettings?.clickCollectCheckboxTitle
        sharedPrefs.clickAndCollectCheckboxDescription = data?.ancoSettings?.clickCollectCheckboxDescription
        sharedPrefs1.clickAndCollectCheckboxTorquayDescription = data?.ancoSettings?.torquy_click_collect_checkbox_description

        sharedPrefs.adminFeeNonTurf = data?.ancoSettings?.adminFee
    }
}

