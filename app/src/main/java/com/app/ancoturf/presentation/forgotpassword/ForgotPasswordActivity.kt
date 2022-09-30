package com.app.ancoturf.presentation.forgotpassword

import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.databinding.ActivityForgotPasswordBinding
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.extension.showAlert
import com.app.ancoturf.presentation.common.base.BaseActivity
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_forgot_password.*
import javax.inject.Inject

class ForgotPasswordActivity : BaseActivity(), View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var forgotPasswordBinding: ActivityForgotPasswordBinding? = null
    private var forgotPasswordViewModel: ForgotPasswordViewModel? = null

    override fun getContentResource(): Int = R.layout.activity_forgot_password

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun viewModelSetup() {
        forgotPasswordViewModel = ViewModelProviders.of(this, viewModelFactory)[ForgotPasswordViewModel::class.java]
        initObservers()
    }

    override fun viewSetup() {
        var signinInfo: SettingsResponse.Data.SigninScreenSetting? =
            Gson().fromJson(sharedPrefs.signinInfo, SettingsResponse.Data.SigninScreenSetting::class.java)

        forgotPasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        forgotPasswordBinding?.viewModel = forgotPasswordViewModel

        btnForgotPassword.setOnClickListener(this)
        viewForgotPassword.setOnClickListener(this)


//        GlideApp.with(this).load(signinInfo.signinLogo).into(imgLogo)

        if(signinInfo != null) {
            if (Utility.isTablet(this)) {
                Glide.with(this).load(signinInfo.signinBackgroundImageTablet).into(imageViewBg)
            } else {
                Glide.with(this).load(signinInfo.signinBackgroundImage).into(imageViewBg)
            }
        }

//        if (Utility.isTablet(this)) {
//            Glide.with(this).load(signinInfo.signinBackgroundImageTablet).diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true).into(object : CustomTarget<Drawable>() {
//                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                        mainLayout.background = resource
//                    } else {
//                        mainLayout.setBackgroundDrawable(resource)
//                    }
//                }
//
//                override fun onLoadCleared(placeholder: Drawable?) {
//                }
//            })
//        } else {
//            Glide.with(this).load(signinInfo.signinBackgroundImage).diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true).into(object : CustomTarget<Drawable>() {
//                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                        mainLayout.background = resource
//                    } else {
//                        mainLayout.setBackgroundDrawable(resource)
//                    }
//                }
//
//                override fun onLoadCleared(placeholder: Drawable?) {
//                }
//            })
//        }
    }

    private fun initObservers() {
        forgotPasswordViewModel!!.forgotPasswordLiveData.observe(this, Observer {
            shortToast(it)
            finish()
        })

        forgotPasswordViewModel!!.errorLiveData.observe(this, Observer {
            if(!Utility.isValueNull(forgotPasswordViewModel!!.errorLiveData.value)) {
                it?.let { it1 -> shortToast(it1) }
                forgotPasswordViewModel!!._errorLiveData.value = null
            }
        })
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnForgotPassword, R.id.viewForgotPassword -> {
                if (forgotPasswordViewModel!!.checkValidation().equals(ErrorConstants.NO_ERROR)) {
                    hideKeyboard()
                    Utility.showProgress(context = this@ForgotPasswordActivity , message = "" , cancellable = false)
                    forgotPasswordViewModel?.callForgotPassword()
                }
                else
                    showToast(forgotPasswordViewModel!!.checkValidation())
            }
        }
    }

    private fun showToast(errorCode : Int) {
        if(errorCode == ErrorConstants.BLANK_EMAIL) {
            showAlert(getString(R.string.blank_email_message))
            edtEmail.requestFocus()
        } else if(errorCode == ErrorConstants.INVALID_EMAIL) {
            showAlert(getString(R.string.invalid_email_message))
            edtEmail.requestFocus()
        }
    }
}
