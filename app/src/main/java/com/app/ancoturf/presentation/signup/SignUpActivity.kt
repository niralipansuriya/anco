package com.app.ancoturf.presentation.signup

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.databinding.ActivitySignUpBinding
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.openAlertDialogWithOneClick
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.common.base.BaseActivity
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.login.LoginActivity
import com.app.ancoturf.utils.Utility
import com.app.ancoturf.utils.interfces.OnDialogButtonClick
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_sign_up.*
import javax.inject.Inject
import android.text.Spannable
import android.text.style.RelativeSizeSpan
import android.text.Spanned
import android.graphics.Typeface
import android.os.Bundle
import android.text.style.StyleSpan
import android.text.SpannableString
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.PasswordTransformationMethod.*
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.app.ancoturf.R
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.manageLawn.WebViewFragment
import com.app.ancoturf.presentation.welcomeOnBoard.WelcomeOnBoardActivity
import com.google.firebase.analytics.FirebaseAnalytics


class SignUpActivity : BaseActivity(), View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private var signUpBinding: ActivitySignUpBinding? = null
    private var signUpViewModel: SignupViewModel? = null
    private var isFromCheckOut : Boolean = false
    private var mFirebaseAnalytics: FirebaseAnalytics? = null


    override fun getContentResource(): Int = R.layout.activity_sign_up

    override fun viewSetup() {
        signUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)

        if (intent.hasExtra(Constants.IS_SIGN_UP_LANDSCAPER) && intent.getBooleanExtra(Constants.IS_SIGN_UP_LANDSCAPER,false)){
            signUpViewModel?.landscaper = true
        }
        signUpBinding?.viewModel = signUpViewModel
        setDataFromCheckOut()
        signUpBinding?.variableViewModel = signUpViewModel

        edtPassword.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            source.toString().filterNot { it.isWhitespace() }
        }
        )
        setSpannable()

        var signupInfo: SettingsResponse.Data.SignupScreenSetting? =
            Gson().fromJson(sharedPrefs.signupInfo, SettingsResponse.Data.SignupScreenSetting::class.java)

        /*if (signupInfo != null) {
//        GlideApp.with(this).load(signupInfo.signupLogo).into(imgLogo)
            if (Utility.isTablet(this)) {
                Glide.with(this).load(signupInfo.signupBackgroundImageTablet).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
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
                Glide.with(this).load(signupInfo.signupBackgroundImage).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
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

        btnLogin.setOnClickListener(this)
        viewLogin.setOnClickListener(this)
        btnSignup.setOnClickListener(this)
        viewSignup.setOnClickListener(this)
        ivPswEye.setOnClickListener(this)
        txtCheckLandscaper.setOnClickListener(this)


    }

    private fun setDataFromCheckOut(){
        if (intent.hasExtra(Constants.FROM_GUEST_USER_REGISTRATION) && intent.getBooleanExtra(
                Constants.FROM_GUEST_USER_REGISTRATION,false)){
            isFromCheckOut = true
            signUpViewModel?.firstName = intent.getStringExtra("firstName")!!
            signUpViewModel?.lastName = intent.getStringExtra("lastName")!!
            signUpViewModel?.email = intent.getStringExtra("email")!!
        }
    }

    override fun viewModelSetup() {
        signUpViewModel = ViewModelProviders.of(this, viewModelFactory)[SignupViewModel::class.java]
        initObservers()
    }

    fun hideAndShowPassword(editText: EditText, indicator: ImageView) {

        if (editText.transformationMethod == getInstance()) {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            indicator.setImageDrawable(
                ContextCompat.getDrawable(
                    editText.context,
                    R.drawable.ic_password_show
                )
            )
            /*indicator.imageTintList =
                ContextCompat.getColorStateList(editText.context, R.color.colorTintIcons)*/
        } else {
            editText.transformationMethod = getInstance()
            indicator.setImageDrawable(
                ContextCompat.getDrawable(
                    editText.context,
                    R.drawable.ic_password_hide
                )
            )
           /* indicator.imageTintList =
                ContextCompat.getColorStateList(editText.context, R.color.colorTintIcons)*/
        }

        editText.setSelection(editText.text.length)
    }
    private fun initObservers() {
        signUpViewModel!!.signupLiveData.observe(this, Observer {
            if (it != null) {
                if (it.success) {
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.METHOD, "Google")
                    mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle)

                    if (signUpViewModel!!.landscaper) {
                        openAlertDialogWithOneClick(
                            title = getString(R.string.app_name),
                            message = it.message,
                            positiveButton = getString(android.R.string.ok),
                            onDialogButtonClick = object : OnDialogButtonClick {
                                override fun onPositiveButtonClick() {
                                    sharedPrefs.isLogged = true
                                    sharedPrefs.accessToken = "Bearer " + it.data?.token
                                    sharedPrefs.userType = it.data!!.idCmsPrivileges
                                    sharedPrefs.userId = it.data!!.id
                                    if (it.data!!.credit != null)
                                        sharedPrefs.availableCredit = it.data!!.credit!!.available_credit.toInt()
                                    sharedPrefs.userEmail = it.data!!.email
                                    sharedPrefs.userName = it.data!!.firstName + " " + it.data!!.lastName
                                    sharedPrefs.userInfo = Gson().toJson(it.data, UserInfo::class.java)
                                    sharedPrefs.quote_max_price = it.data!!.quote_max_price
                                    sharedPrefs.order_max_price = it.data!!.order_max_price
                                    if (isFromCheckOut){
                                        setFromCheckOutScreens()
                                    }
                                    else{
                                        var intent = Intent(this@SignUpActivity, HomeActivity::class.java)
                                        startActivity(intent)
                                        finishAffinity()
                                    }

                                }
                                override fun onNegativeButtonClick() {
                                }
                            })
                    } else {
                        shortToast(it.message)
                        sharedPrefs.isLogged = true
                        sharedPrefs.accessToken = "Bearer " + it.data?.token
                        sharedPrefs.userType = it.data!!.idCmsPrivileges
                        sharedPrefs.userId = it.data!!.id
                        if (it.data!!.credit != null)
                            sharedPrefs.availableCredit = it.data!!.credit!!.available_credit.toInt()
                        sharedPrefs.userEmail = it.data!!.email
                        sharedPrefs.userName = it.data!!.firstName + " " + it.data!!.lastName
                        sharedPrefs.userInfo = Gson().toJson(it.data, UserInfo::class.java)
                        sharedPrefs.quote_max_price = it.data!!.quote_max_price
                        sharedPrefs.order_max_price = it.data!!.order_max_price
                        signUpViewModel?._signupLiveData?.value = null
                        if (isFromCheckOut){
                            setFromCheckOutScreens()

                        }else {
                            var intent = Intent(this@SignUpActivity, HomeActivity::class.java)

                            startActivity(intent)
                            finishAffinity()
                        }
                    }
                } else {
                    shortToast(it.message)
                    signUpViewModel?._signupLiveData?.value = null
                }
            }
        })

        signUpViewModel!!.errorLiveData.observe(this, Observer {
            if (!Utility.isValueNull(signUpViewModel!!.errorLiveData.value)) {
                it?.let { it1 -> shortToast(it1) }
                signUpViewModel!!._errorLiveData.value = null
            }
        })
    }

    private fun setFromCheckOutScreens() {
      /*  val updateViewAsLoginUserIntent = Intent()
        updateViewAsLoginUserIntent.action = Constants.GUEST_USER_TO_LOGIN_VIA_CHECKOUT_ACTION
        sendBroadcast(updateViewAsLoginUserIntent)*/
        var intent = Intent()
        intent.putExtra(Constants.FROM_GUEST_USER_REGISTRATION,true)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnLogin, R.id.viewLogin -> {
                var intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            R.id.btnSignup, R.id.viewSignup -> {
                if (signUpViewModel!!.checkValidation().equals(ErrorConstants.NO_ERROR)) {
                    hideKeyboard()
                    Utility.showProgress(context = this@SignUpActivity, message = "", cancellable = false)
                    signUpViewModel!!.callRegister()
                } else
                    showToast(signUpViewModel!!.checkValidation())
            }
            /*  R.id.txtForgotPassword -> {
                  signUpViewModel!!.onClickOfLandscaper()
              }*/
            R.id.ivPswEye ->{
                hideAndShowPassword(edtPassword,ivPswEye)
            }
        }
    }

    private fun showToast(errorCode: Int) {
        if (errorCode == ErrorConstants.BLANK_FIRST_NAME) {
            shortToast(getString(R.string.black_firstname_message))
            edtFirstName.requestFocus()
        } else if (errorCode == ErrorConstants.BLANK_LAST_NAME) {
            shortToast(getString(R.string.black_lastname_message))
            edtLastName.requestFocus()
        } else if (errorCode == ErrorConstants.BLANK_EMAIL) {
            shortToast(getString(R.string.blank_email_message))
            edtEmail.requestFocus()
        } else if (errorCode == ErrorConstants.INVALID_EMAIL) {
            shortToast(getString(R.string.invalid_email_message))
            edtEmail.requestFocus()
        } else if (errorCode == ErrorConstants.BLANK_PASSWORD) {
            shortToast(getString(R.string.blank_password_message))
            edtPassword.requestFocus()
        } else if (errorCode == ErrorConstants.INVALID_PASSWORD) {
            shortToast(getString(R.string.invalid_password_message))
            edtPassword.requestFocus()
        } else if (errorCode == ErrorConstants.BLANK_BUSINESS_NAME) {
            shortToast(getString(R.string.blank_business_name_message))
            edtBusinessName.requestFocus()
        } else if (errorCode == ErrorConstants.BLANK_ABN) {
            shortToast(getString(R.string.blank_abn_message))
            edtABN.requestFocus()
        } else if (errorCode == ErrorConstants.INVALID_ABN) {
            shortToast(getString(R.string.invalid_abn_messgae))
            edtABN.requestFocus()
        } else if (errorCode == ErrorConstants.BLANK_PHONE_NUMBER) {
            shortToast(getString(R.string.blank_phone_number_message))
            edtPhoneNumber.requestFocus()
        } else if (errorCode == ErrorConstants.INVALID_PHONE_NUMBER) {
            shortToast(getString(R.string.invalid_phone_number_message))
            edtPhoneNumber.requestFocus()
        }
    }

    private fun setSpannable() {
        var ss = SpannableString(getString(R.string.tnc_text))
        val clickableSpanForTNC = object : ClickableSpan() {
            override fun onClick(textView: View) {
//                shortToast("Terms and Conditions clicked")
                var intent = Intent(this@SignUpActivity, OpenPDFActivity::class.java)
                intent.putExtra("from",1)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isFakeBoldText = true
                ds.color = ContextCompat.getColor(this@SignUpActivity, android.R.color.white)
                ds.isUnderlineText = false
            }
        }

        val clickableSpanForPNP = object : ClickableSpan() {
            override fun onClick(textView: View) {
                shortToast("Privacy Policy clicked")
               /* val bundle = Bundle()
                bundle.putString("url", "")
                bundle.putInt("from", 2)
                bundle.putString("title", "Privacy Policy")
              pushFragment(
                    WebViewFragment().apply { arguments = bundle },
                    true,
                    false,
                    false,
                    R.id.flContainerHome
                )*/

                var intent = Intent(this@SignUpActivity, OpenPDFActivity::class.java)
                intent.putExtra("from",2)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isFakeBoldText = true
                ds.color = ContextCompat.getColor(this@SignUpActivity, android.R.color.white)
                ds.isUnderlineText = false
            }
        }

        ss.setSpan(clickableSpanForTNC, 35, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(clickableSpanForPNP, 60, 74, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        txtTnC.text = ss
        txtTnC.movementMethod = LinkMovementMethod.getInstance()
        txtTnC.highlightColor = Color.TRANSPARENT
    }

    fun createTwoLineTextViewContent(): SpannableString {

        val inputTextLines: Array<String> = getString(R.string.tnc_text).split("\n").toTypedArray()

        var outputFloatTextReplacement = SpannableString(inputTextLines[0])

        if (inputTextLines.size > 1) {
            val outputPart1Part2 = inputTextLines[0] + "\n" + inputTextLines[1]
            // ToDo: loop and allow for even more items in array / lines.
            outputFloatTextReplacement = SpannableString(outputPart1Part2)

            val secondLineStartAfterNewline = outputPart1Part2.length - 1 - inputTextLines[1].length
            // Bold it
            outputFloatTextReplacement.setSpan(
                StyleSpan(Typeface.BOLD),
                secondLineStartAfterNewline,
                outputPart1Part2.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // Boost font size
            outputFloatTextReplacement.setSpan(
                RelativeSizeSpan(1.25f),
                secondLineStartAfterNewline,
                outputPart1Part2.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return outputFloatTextReplacement
    }

}

