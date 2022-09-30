package com.app.ancoturf.presentation.contact

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.extension.openAlertDialogWithTwoClick
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.common.base.BaseActivity
import com.app.ancoturf.presentation.splash.SplashViewModel
import com.app.ancoturf.utils.Utility
import com.app.ancoturf.utils.interfces.OnDialogButtonClick
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_contacts.*
import javax.inject.Inject


class ContactActivity : BaseActivity(), View.OnClickListener {

    val REQUEST_CALL_PERMISSION_SETTINGS = 7

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var splashViewModel: SplashViewModel? = null

    var contactInfo: SettingsResponse.Data.ContactScreenSetting? = null

    override fun getContentResource(): Int = R.layout.activity_contacts

    override fun viewModelSetup() {
        splashViewModel = ViewModelProviders.of(this, viewModelFactory)[SplashViewModel::class.java]
        initObservers()
    }

    override fun viewSetup() {
        layoutFooterView.initFooter(this, ContactActivity::class.java.simpleName)

//        imgCart.setColorFilter(null)
//        txtCartProducts.visibility = View.GONE

        isNeedToRecreateActivity(false)
        contactInfo =
            Gson().fromJson(sharedPrefs.contactInfo, SettingsResponse.Data.ContactScreenSetting::class.java)

        layoutCallNow.setOnClickListener(this)
        txtEmail.setOnClickListener(this)
        txtPhone.setOnClickListener(this)
        txtAddress.setOnClickListener(this)

        setData()
    }

    /***
     * @param isNeedToRecreateActivity = need to recreate activity or not
     */
    private fun isNeedToRecreateActivity(isNeedToRecreateActivity: Boolean) {
        if (layoutFooterView != null) {
            layoutFooterView.setNeedToRecreateActivity(isNeedToRecreateActivity)
        }
    }

    private fun initObservers() {
        Utility.cancelProgress()
        splashViewModel!!.settingsLiveData.observe(this, Observer {
            Utility.cancelProgress()
            sharedPrefs.contactInfo =
                Gson().toJson(it.data!!.contactScreenSetting, SettingsResponse.Data.ContactScreenSetting::class.java)
            contactInfo =
                Gson().fromJson(sharedPrefs.contactInfo, SettingsResponse.Data.ContactScreenSetting::class.java)
            setData()
        })
    }

    private fun setData() {

        if(contactInfo != null) {

            if (Utility.isTablet(this)) {
                Glide.with(this).load(contactInfo!!.contactBackgroundImageTablet).into(imageViewBg)
            } else {
                Glide.with(this).load(contactInfo!!.contactBackgroundImage).into(imageViewBg)
            }


//            if (Utility.isTablet(this)) {
//                Glide.with(this).load(contactInfo!!.contactBackgroundImageTablet).diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true).into(object : CustomTarget<Drawable>() {
//                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                                mainLayout.background = resource
//                            } else {
//                                mainLayout.setBackgroundDrawable(resource)
//                            }
//                        }
//
//                        override fun onLoadCleared(placeholder: Drawable?) {
//                        }
//                    })
//            } else {
//                Glide.with(this).load(contactInfo!!.contactBackgroundImage).diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .skipMemoryCache(true).into(object : CustomTarget<Drawable>() {
//                        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                                mainLayout.background = resource
//                            } else {
//                                mainLayout.setBackgroundDrawable(resource)
//                            }
//                        }
//
//                        override fun onLoadCleared(placeholder: Drawable?) {
//                        }
//
//                    })
//            }

            if (!Utility.isValueNull(contactInfo!!.contactLabel)) {
                txtContactLabel.text = contactInfo!!.contactLabel
            } else {
                txtContactLabel.text = getString(R.string.contact_label)
            }

            if (!Utility.isValueNull(contactInfo!!.contactEmail)) {
                txtEmail.text = contactInfo!!.contactEmail
                txtEmail.visibility = View.VISIBLE
            }
            else {
                txtEmail.visibility = View.GONE
            }

            if (!Utility.isValueNull(contactInfo!!.contactPhone)) {
                txtPhone.text = contactInfo!!.contactPhone
                txtPhone.visibility = View.VISIBLE
            }
            else {
                txtPhone.visibility = View.GONE
            }

            if (!Utility.isValueNull(contactInfo!!.contactAddressLine1) || !Utility.isValueNull(contactInfo!!.contactAddressLine2) || !Utility.isValueNull(contactInfo!!.contactAddressLine3)) {
                txtAddress.visibility = View.VISIBLE
                val address = StringBuilder()
                if (!Utility.isValueNull(contactInfo!!.contactAddressLine1))
                    address.append(contactInfo!!.contactAddressLine1+"\n")
                if (!Utility.isValueNull(contactInfo!!.contactAddressLine2))
                    address.append(contactInfo!!.contactAddressLine2+"\n")
                if (!Utility.isValueNull(contactInfo!!.contactAddressLine3))
                    address.append(contactInfo!!.contactAddressLine3)
                txtAddress.text = address.toString()
            } else {
                txtAddress.visibility = View.GONE
            }

            if (!Utility.isValueNull(contactInfo!!.contactOperatingHoursLabel)) {
                txtHoursLabel.text = contactInfo!!.contactOperatingHoursLabel
            } else {
                txtHoursLabel.text = getString(R.string.operating_hours_label)
            }

            if (!Utility.isValueNull(contactInfo!!.contactOperatingHoursLine1) || !Utility.isValueNull(contactInfo!!.contactOperatingHoursLine2) || !Utility.isValueNull(contactInfo!!.contactOperatingHoursLine3) || !Utility.isValueNull(contactInfo!!.contactOperatingHoursLine4)) {
                txtOperatingHours.visibility = View.VISIBLE
                val operatingHours = StringBuilder()
                if (!Utility.isValueNull(contactInfo!!.contactOperatingHoursLine1))
                    operatingHours.append(contactInfo!!.contactOperatingHoursLine1+"\n")
                if (!Utility.isValueNull(contactInfo!!.contactOperatingHoursLine2))
                    operatingHours.append(contactInfo!!.contactOperatingHoursLine2+"\n")
                if (!Utility.isValueNull(contactInfo!!.contactOperatingHoursLine3))
                    operatingHours.append(contactInfo!!.contactOperatingHoursLine3+"\n")
                if (!Utility.isValueNull(contactInfo!!.contactOperatingHoursLine4))
                    operatingHours.append(contactInfo!!.contactOperatingHoursLine4)
                txtOperatingHours.text = operatingHours.toString()
            } else {
                txtOperatingHours.visibility = View.GONE
            }

        } else {
            Utility.showProgress(this , "" , false)
            splashViewModel!!.callGetSettings()
        }
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.layoutCallNow -> {
                openCallPermission()
            }
            R.id.txtEmail -> {
                sendEmail()
            }
            R.id.txtPhone -> {
                openCallPermission()
            }
            R.id.txtAddress -> {
                openGoogleMap()
            }
        }
    }

    fun openCallPermission() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CALL_PHONE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    // permission is granted
                    call()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    // check for permanent denial of permission
                    if (response.isPermanentlyDenied) {
                        openAlertDialogWithTwoClick(
                            title = getString(R.string.missing_permission_title)
                            , message = getString(R.string.missing_permission_message)
                            , positiveButton = "SETTING"
                            , nagetiveButton = getString(android.R.string.cancel)
                            ,onDialogButtonClick = object : OnDialogButtonClick {
                                override fun onPositiveButtonClick() {
                                    openSettings()
                                }

                                override fun onNegativeButtonClick() {
                                }

                            })
                    }
                    shortToast("Call permission denied")
                }

                override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                    openAlertDialogWithTwoClick(
                    title = getString(R.string.missing_permission_title)
                    , message = getString(R.string.missing_permission_message)
                    , positiveButton = "Grant"
                    , nagetiveButton = getString(android.R.string.cancel)
                    ,onDialogButtonClick = object : OnDialogButtonClick {
                        override fun onPositiveButtonClick() {
                            token.continuePermissionRequest()
                        }

                        override fun onNegativeButtonClick() {
                            token.cancelPermissionRequest()
                        }

                    })
                }
            }).check()
    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CALL_PERMISSION_SETTINGS)
        }
    }

    private fun call() {
        if (!Utility.isValueNull(contactInfo?.contactPhone)) {
            val intent = Intent(Intent.ACTION_CALL)
            val phoneNumber = contactInfo?.contactPhone
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        }
    }

    private fun sendEmail() {
        if (!Utility.isValueNull(contactInfo?.contactEmail)) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(contactInfo!!.contactEmail))
            startActivity(Intent.createChooser(intent, "Enquire"))
        }
    }

    private fun openGoogleMap() {
        if (!Utility.isValueNull(contactInfo?.contactEmail)) {
            val address = StringBuilder()
            if (!Utility.isValueNull(contactInfo!!.contactAddressLine1) || !Utility.isValueNull(contactInfo!!.contactAddressLine2) || !Utility.isValueNull(contactInfo!!.contactAddressLine3)) {
                address.append("google.navigation:q=")
                if (!Utility.isValueNull(contactInfo!!.contactAddressLine1))
                    address.append(contactInfo!!.contactAddressLine1)
                if (!Utility.isValueNull(contactInfo!!.contactAddressLine2))
                    address.append("+"+contactInfo!!.contactAddressLine2)
                if (!Utility.isValueNull(contactInfo!!.contactAddressLine3))
                    address.append("+"+contactInfo!!.contactAddressLine3)
            }
            val gmmIntentUri = Uri.parse(address.toString())
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CALL_PERMISSION_SETTINGS) {
                openCallPermission()
            }
        }
    }

    override fun onBackPressed() {
//        when (currentTopFragment) {
//            AppConstants.ALERT_FRAGMENT -> {
        finishAffinity()
//            }
//            else -> {
//                super.onBackPressed()
//            }
    }
}
