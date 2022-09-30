package com.app.ancoturf.presentation.welcome

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.presentation.common.base.BaseActivity
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.signup.SignUpActivity
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_welcome.*
import javax.inject.Inject

class WelcomeActivity : BaseActivity(), View.OnClickListener {

    override fun getContentResource(): Int = R.layout.activity_welcome

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun viewSetup() {
        sharedPrefs.isWelcomeVisited = true
        var onBoardingInfo: SettingsResponse.Data.OnboardingScreenSetting? = Gson().fromJson(sharedPrefs.onboardingInfo, SettingsResponse.Data.OnboardingScreenSetting::class.java)

        if (onBoardingInfo != null) {
//        GlideApp.with(this).load(onBoardingInfo.onboardingLogo).into(imgLogo)

            if (Utility.isTablet(this)) {
                Glide.with(this).load(onBoardingInfo.onboardingBackgroundImageTablet)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
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
                Glide.with(this).load(onBoardingInfo.onboardingBackgroundImage)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
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
        }

        viewSignUp.setOnClickListener(this)
        txtSkip.setOnClickListener(this)
    }

    override fun viewModelSetup() {
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.viewSignUp -> {
                var intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.txtSkip -> {
                var intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
