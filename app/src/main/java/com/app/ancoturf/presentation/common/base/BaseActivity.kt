package com.app.ancoturf.presentation.common.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.presentation.app.AncoTurfApp
import com.app.ancoturf.presentation.profile.ProfileViewModel
import com.app.ancoturf.presentation.splash.SplashViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.gson.Gson
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity() {

    @LayoutRes
    protected abstract fun getContentResource(): Int

    @Inject
    lateinit var viewModelFactory1: ViewModelProvider.Factory

    @Inject
    lateinit var sharedPrefs1: SharedPrefs

    @Inject
    lateinit var ancoTurfApp: AncoTurfApp

    private var splashViewModel: SplashViewModel? = null
    private var profileViewModel: ProfileViewModel? = null

    var savedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.decorView.systemUiVisibility =
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            window.statusBarColor = Color.TRANSPARENT
//        }
        getContentResource().apply {
            setContentView(this)
        }
        if (savedInstanceState != null) {
            this.savedInstanceState = savedInstanceState!!
        }
        splashViewModel =
            ViewModelProviders.of(this, viewModelFactory1)[SplashViewModel::class.java]
        profileViewModel =
            ViewModelProviders.of(this, viewModelFactory1)[ProfileViewModel::class.java]
        initObserver()
        viewModelSetup()
        viewSetup()
    }

    override fun onResume() {
        super.onResume()
        ancoTurfApp.setCurrentActivity(this)
    }

    private fun initObserver() {
        splashViewModel!!.settingsLiveData.observe(this, Observer {
            if (it?.data != null) {
                addToPref(it.data)
            }
        })

        profileViewModel!!.userInfoLiveData.observe(this, Observer {
            if (it != null) {
//               sharedPrefs1 .accessToken = "Bearer " + it.token
                sharedPrefs1.userType = it.idCmsPrivileges
                sharedPrefs1.userId = it.id
                if (it.credit != null)
                    sharedPrefs1.availableCredit = it.credit!!.available_credit.toInt()
                sharedPrefs1.userEmail = it.email
                sharedPrefs1.userName = it.firstName + " " + it.lastName
                sharedPrefs1.userInfo = Gson().toJson(it, UserInfo::class.java)
                sharedPrefs1.quote_max_price = it.quote_max_price
                sharedPrefs1.order_max_price = it.order_max_price
            }
        })

    }

    abstract fun viewModelSetup()

    abstract fun viewSetup()


    fun callBackGroundAPIs() {
        splashViewModel?.callGetSettings()
        if (sharedPrefs1.isLogged)
            profileViewModel?.callGetUserDetails()
    }

//    fun hideShowFooter(shouldVisible: Boolean) {
//        layoutFooterView.visibility = if (shouldVisible) View.VISIBLE else View.GONE
//    }
//
//    fun showCartDetails(imgCart: ImageView, txtCartProducts: TextView, inCart: Boolean) {
//        if (inCart) {
//            imgCart.setColorFilter(ContextCompat.getColor(this, R.color.theme_green))
//            txtCartProducts.visibility = View.VISIBLE
//        } else {
//            imgCart.setColorFilter(null)
//            txtCartProducts.visibility = View.GONE
//        }
//    }

    private fun addToPref(data: SettingsResponse.Data?) {
        sharedPrefs1.signupInfo =
            Gson().toJson(
                data!!.signupScreenSetting,
                SettingsResponse.Data.SignupScreenSetting::class.java
            )
        sharedPrefs1.signinInfo =
            Gson().toJson(
                data!!.signinScreenSetting,
                SettingsResponse.Data.SigninScreenSetting::class.java
            )
        sharedPrefs1.onboardingInfo =
            Gson().toJson(
                data!!.onboardingScreenSetting,
                SettingsResponse.Data.OnboardingScreenSetting::class.java
            )
        sharedPrefs1.contactInfo =
            Gson().toJson(
                data!!.contactScreenSetting,
                SettingsResponse.Data.ContactScreenSetting::class.java
            )
        var productMaxPrice = data?.product?.maxPrice
        sharedPrefs1.product_max_price = "$productMaxPrice"
        var quoteMaxPrice = data?.quote?.maxPrice
        sharedPrefs1.quote_max_price = "$quoteMaxPrice"
        var orderMaxPrice = data?.orders?.maxPrice
        sharedPrefs1.order_max_price = "$orderMaxPrice"
        sharedPrefs1.max_number_portfolios = data?.ancoSettings?.maxNumberPortfolios
        sharedPrefs1.max_images_non_anco_products_quote =
            data?.ancoSettings?.maxImagesNonAncoProductsQuote
        sharedPrefs1.max_portfolio_images = data?.ancoSettings?.maxPortfolioImages
        sharedPrefs1.deliveryMinDays = data?.ancoSettings?.deliveryMinDays
        sharedPrefs1.deliveryMaxDays = data?.ancoSettings?.deliveryMaxDays
        sharedPrefs1.deliveryMessage = data?.ancoSettings?.deliveryMessage
        sharedPrefs1.tags = Gson().toJson(data!!.tags)
        sharedPrefs1.productUnitsInfo = Gson().toJson(data!!.productUnits)
        sharedPrefs1.orderStatues = Gson().toJson(data!!.orderStatuses)
        sharedPrefs1.deliveryStatues = Gson().toJson(data!!.deliveryStatuses)
        sharedPrefs1.quoteToOrderMessage = data?.ancoSettings?.quoteToOrderMessage
        sharedPrefs1.dropTurfOnPropertyTitle = data?.ancoSettings?.dropTurfOnPropertyTitle
        sharedPrefs1.dropTurfOnPropertyDescription =
            data?.ancoSettings?.dropTurfOnPropertyDescription
        sharedPrefs1.acceptDeclineCheckboxTitle = data?.ancoSettings?.acceptDeclineCheckboxTitle
        sharedPrefs1.clickAndCollectCheckboxTitle = data?.ancoSettings?.clickCollectCheckboxTitle
        sharedPrefs1.clickAndCollectCheckboxDescription = data?.ancoSettings?.clickCollectCheckboxDescription
        sharedPrefs1.clickAndCollectCheckboxTorquayDescription = data?.ancoSettings?.torquy_click_collect_checkbox_description
        sharedPrefs1.adminFeeNonTurf = data?.ancoSettings?.adminFee
    }

    fun createShareUri(quoteId: String?, status: String?): Uri {
        val builder = Uri.Builder()
        builder.scheme(getString(R.string.config_scheme)) // "https"
            .authority(getString(R.string.config_host)) // "ancoapp.page.link"
            .appendPath(getString(R.string.config_path_quote)) // "quote"  https://ancoapp.page.link/quote
            .appendQueryParameter(Constants.QUOTE_ID_PARAM, quoteId)
            .appendQueryParameter(Constants.QUOTE_STATUS_PARAM, status)

        return builder.build()
    }

    fun createShareProductUri(productId: String?): Uri {
        val builder = Uri.Builder()
        builder.scheme(getString(R.string.config_scheme)) // "https"
            .authority(getString(R.string.config_host)) // "ancoapp.page.link"
            .appendPath(getString(R.string.config_path_products)) // "products"  https://ancoapp.page.link/products
            .appendQueryParameter(Constants.PRODUCT_ID_PARAM, productId)
//            .appendQueryParameter(Constants.QUOTE_STATUS_PARAM, status)

        return builder.build()
    }

    fun createDynamicUri(myUri: Uri) {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(myUri)
            .setDomainUriPrefix("https://ancoapp.page.link")
            .setAndroidParameters(DynamicLink.AndroidParameters.Builder("com.app.ancoturf").build())
            .setIosParameters(DynamicLink.IosParameters.Builder("com.ancoturf").build())
            .buildShortDynamicLink()
            .addOnCompleteListener(this, OnCompleteListener {
                if (it?.isSuccessful) {
                    val shortenUrl = it.result?.shortLink
                    com.app.ancoturf.utils.Logger.log("shortenURL: $shortenUrl")
                }
            })
    }

}