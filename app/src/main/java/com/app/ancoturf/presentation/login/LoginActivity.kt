package com.app.ancoturf.presentation.login

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.*
import android.text.method.HideReturnsTransformationMethod
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.databinding.ActivityLoginBinding
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartViewModel
import com.app.ancoturf.presentation.common.base.BaseActivity
import com.app.ancoturf.presentation.forgotpassword.ForgotPasswordActivity
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.signup.SignUpActivity
import com.app.ancoturf.presentation.splash.SplashViewModel
import com.app.ancoturf.presentation.welcomeOnBoard.WelcomeOnBoardActivity
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : BaseActivity(), View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var loginBinding: ActivityLoginBinding? = null
    private var loginViewModel: LoginViewModel? = null
    private var splashViewModel: SplashViewModel? = null
    private var cartViewModel: CartViewModel? = null
    override fun getContentResource(): Int = R.layout.activity_login
    private var mFirebaseAnalytics: FirebaseAnalytics? = null


    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun viewModelSetup() {
        loginViewModel = ViewModelProviders.of(this, viewModelFactory)[LoginViewModel::class.java]
        splashViewModel = ViewModelProviders.of(this, viewModelFactory)[SplashViewModel::class.java]
        cartViewModel = ViewModelProviders.of(this, viewModelFactory)[CartViewModel::class.java]
        initObservers()
    }

    override fun viewSetup() {
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)
        /*loginViewModel?.userEmail = "sicomau01@gmail.com"
        loginViewModel?.password = "anco001"*/
        loginBinding?.viewModel = loginViewModel
        setSpannable()
        val isLoggedIN = sharedPrefs.isLogged
        Log.e("TAG", "" + isLoggedIN)
        btnLogin.setOnClickListener(this)
        viewLogin.setOnClickListener(this)
        btnSignup.setOnClickListener(this)
        viewSignUp.setOnClickListener(this)
        txtSkip.setOnClickListener(this)
        imgPasswordShowHide.setOnClickListener(this)
        edtLoginPassword.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            source.toString().filterNot { it.isWhitespace() }
        })
        var signinInfo: SettingsResponse.Data.SigninScreenSetting? =
            Gson().fromJson(
                sharedPrefs.signinInfo,
                SettingsResponse.Data.SigninScreenSetting::class.java
            )
       /* if (signinInfo != null) {
//        GlideApp.with(this).load(signinInfo.signinLogo).into(imgLogo)
            if (Utility.isTablet(this)) {
                Glide.with(this).load(signinInfo.signinBackgroundImageTablet)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                mainLayout.background = resource
                            } else {
                                mainLayout.setBackgroundDrawable(resource)
                            }
                        }
                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
            } else {
                Glide.with(this).load(signinInfo.signinBackgroundImage)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drnawable,
                            transition: Transition<in Drawable>?
                        ) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                mainLayout.background = resource
                            } else {
                                mainLayout.setBackgroundDrawable(resource)
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                        }
                    })
            }
        }*/
    }

    private fun initObservers() {
        loginViewModel!!.loginLiveData.observe(this, Observer {
            //            sharedPrefs.isLogged = true
//            sharedPrefs.accessToken = "Bearer " + it.token
//            sharedPrefs.userType = it.idCmsPrivileges
//            sharedPrefs.userId = it.id
//            sharedPrefs.userEmail = it.email
//            if (it.credit != null)
//                sharedPrefs.availableCredit = it.credit.available_credit.toInt()
//            sharedPrefs.userName = it.firstName + " " + it.lastName
//            sharedPrefs.userInfo =
//                Gson().toJson(it, UserInfo::class.java)

            //commented by mayur patel
            /*cartViewModel?.deleteProduct(null)
            cartViewModel?.deleteCoupon(null)
            sharedPrefs.totalProductsInCart = 0*/
            splashViewModel?.callGetSettings()
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.METHOD, "Email")
            mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

        })

        splashViewModel!!.settingsLiveData.observe(this, Observer {
            addToPref(it.data)
            var intent = Intent(this, HomeActivity::class.java)
            if (getIntent().hasExtra(Constants.FROM_GUEST_USER_REGISTRATION))
                intent.putExtra(Constants.FROM_GUEST_USER_REGISTRATION,getIntent().extras!!.getBoolean(Constants.FROM_GUEST_USER_REGISTRATION))
            startActivity(intent)
            finishAffinity()
        })
        loginViewModel!!.errorLiveData.observe(this, Observer {
            if (!Utility.isValueNull(loginViewModel!!.errorLiveData.value)) {
                it?.let { it1 -> shortToast(it1) }
                loginViewModel!!._errorLiveData.value = null
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgPasswordShowHide -> {
                if (imgPasswordShowHide.isSelected) {
                    imgPasswordShowHide.isSelected = false
                    edtLoginPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                    if (!TextUtils.isEmpty(edtLoginPassword.text.toString().trim()))
                        edtLoginPassword.setSelection(edtLoginPassword.text.toString().length)
                } else {
                    imgPasswordShowHide.isSelected = true
                    edtLoginPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    if (!TextUtils.isEmpty(edtLoginPassword.text.toString().trim()))
                        edtLoginPassword.setSelection(edtLoginPassword.text.toString().length)
                }
            }
            R.id.btnLogin, R.id.viewLogin -> {
                if (loginViewModel!!.checkValidation().equals(ErrorConstants.NO_ERROR)) {
                    hideKeyboard()
                    Utility.showProgress(
                        context = this@LoginActivity,
                        message = "",
                        cancellable = false
                    )
                    loginViewModel?.callLogin(sharedPrefs?.fcmToken!!)
                } else
                    showToast(loginViewModel!!.checkValidation())
            }
            R.id.btnSignup, R.id.viewSignUp -> {
                var intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
            R.id.txtSkip -> {
                splashViewModel?.callGetSettings()
                sharedPrefs.accessToken = ""
                sharedPrefs.userType = 1
            }
        }
    }

    private fun showToast(errorCode: Int) {
        if (errorCode == ErrorConstants.BLANK_EMAIL) {
            shortToast(getString(R.string.blank_email_message))
            edtEmail.requestFocus()
        } else if (errorCode == ErrorConstants.INVALID_EMAIL) {
            shortToast(getString(R.string.invalid_email_message))
            edtEmail.requestFocus()
        } else if (errorCode == ErrorConstants.BLANK_PASSWORD) {
            shortToast(getString(R.string.blank_password_message))
            edtLoginPassword.requestFocus()
        }
    }

    private fun setSpannable() {
        val ss = SpannableString(Html.fromHtml(getString(R.string.forgot_password_text)))
        val clickableSpanForForgotPassword = object : ClickableSpan() {
            override fun onClick(textView: View) {
                var intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isFakeBoldText = true
                ds.color = ContextCompat.getColor(this@LoginActivity, android.R.color.white)
                ds.isUnderlineText = false
            }
        }
        ss.setSpan(clickableSpanForForgotPassword, 22, 34, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtForgotPassword.setText(ss)
        txtForgotPassword.setMovementMethod(LinkMovementMethod.getInstance())
        txtForgotPassword.setHighlightColor(Color.TRANSPARENT)
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
        sharedPrefs.clickAndCollectCheckboxDescription = data?.ancoSettings?.clickCollectCheckboxDescription
        sharedPrefs1.clickAndCollectCheckboxTorquayDescription = data?.ancoSettings?.torquy_click_collect_checkbox_description

        sharedPrefs.clickAndCollectCheckboxTitle = data?.ancoSettings?.clickCollectCheckboxTitle
        sharedPrefs.adminFeeNonTurf = data?.ancoSettings?.adminFee
    }
}
