package com.app.ancoturf.presentation.contactus

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.openAlertDialogWithTwoClick
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.lawntips.LawnTipsFragment
import com.app.ancoturf.presentation.home.order.OrderFragment
import com.app.ancoturf.presentation.home.order.QuickOrderFragment
import com.app.ancoturf.presentation.home.portfolio.PortfoliosFragment
import com.app.ancoturf.presentation.home.quote.QuotesFragment
import com.app.ancoturf.presentation.home.shop.ShopFragment
import com.app.ancoturf.presentation.home.turfcalculator.TurfCalculatorFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.splash.SplashViewModel
import com.app.ancoturf.utils.Utility
import com.app.ancoturf.utils.interfces.OnDialogButtonClick
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_contact.*
import kotlinx.android.synthetic.main.header_filter.*
import javax.inject.Inject

class ContactFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var splashViewModel: SplashViewModel? = null

    var contactInfo: SettingsResponse.Data.ContactScreenSetting? = null

    val REQUEST_CALL_PERMISSION_SETTINGS = 7


    override fun getContentResource(): Int = R.layout.fragment_contact

    override fun viewModelSetup() {
        splashViewModel = ViewModelProviders.of(this, viewModelFactory)[SplashViewModel::class.java]
        initObservers()
    }

    override fun viewSetup() {
        if(activity is HomeActivity) {
            (activity as HomeActivity).hideShowFooter(false)
        }else if(activity is PaymentActivity) {
            (activity as PaymentActivity).hideShowFooter(false)
        } else if(activity is ProfileActivity) {
            (activity as ProfileActivity).hideShowFooter(false)
        }

        txtTitle.visibility = View.VISIBLE

        imgClose.setOnClickListener(this)
        txtEmail.setOnClickListener(this)
        txtPhone.setOnClickListener(this)
        txtAddress.setOnClickListener(this)

        txtOrders.setOnClickListener(this)
        txtShop.setOnClickListener(this)
        txtTracking.setOnClickListener(this)
        txtPortfolio.setOnClickListener(this)
        txtQuote.setOnClickListener(this)
        txtQuickOrder.setOnClickListener(this)
        txtTurfCalculator.setOnClickListener(this)
        txtLawnTips.setOnClickListener(this)
        layoutCallNow.setOnClickListener(this)
        tvCmpName.setOnClickListener(this)
        if (sharedPrefs.isLogged) {
            txtTracking.visibility = View.VISIBLE
            txtQuickOrder.visibility = View.VISIBLE
            if (sharedPrefs.userType == Constants.LANDSCAPER) {
                txtPortfolio.visibility = View.VISIBLE
                txtQuote.visibility = View.VISIBLE
            } else {
                txtPortfolio.visibility = View.GONE
                txtQuote.visibility = View.GONE
            }
        } else {
            txtTracking.visibility = View.GONE
            txtPortfolio.visibility = View.GONE
            txtQuote.visibility = View.GONE
            txtQuickOrder.visibility = View.GONE
        }

        contactInfo =
            Gson().fromJson(sharedPrefs.contactInfo, SettingsResponse.Data.ContactScreenSetting::class.java)

        setData()
    }

    private fun initObservers() {
        Utility.cancelProgress()
        splashViewModel!!.settingsLiveData.observe(this, Observer {
            sharedPrefs.contactInfo =
                Gson().toJson(it.data!!.contactScreenSetting, SettingsResponse.Data.ContactScreenSetting::class.java)
            setData()
        })
    }

    private fun setData() {
        if(contactInfo != null) {

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

            if (!Utility.isValueNull(contactInfo!!.contactAddressLine1) || !Utility.isValueNull(contactInfo!!.contactAddressLine1) || !Utility.isValueNull(contactInfo!!.contactAddressLine1)) {
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
        } else {
            Utility.showProgress(requireActivity() , "" , false)
            splashViewModel!!.callGetSettings()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tvCmpName ->{
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.sharpinstincts_link_url)))
                startActivity(browserIntent)
            }
            R.id.imgClose -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }
            R.id.txtOrders -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    OrderFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
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

            R.id.txtShop -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    ShopFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
            R.id.txtTracking -> {

            }
            R.id.txtPortfolio -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    PortfoliosFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
            R.id.txtQuote -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    QuotesFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
            R.id.txtQuickOrder -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    QuickOrderFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )

            }
            R.id.txtTurfCalculator -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    TurfCalculatorFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )

            }
            R.id.txtLawnTips -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    LawnTipsFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.layoutCallNow -> {
                openCallPermission()
            }
        }
    }
//    private fun startCall() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.CALL_PHONE
//            ) !== PackageManager.PERMISSION_GRANTED
//        ) {
//            openCallPermission()
//        } else
//            call()
//    }

    fun openCallPermission() {
        Dexter.withActivity(requireActivity())
            .withPermission(Manifest.permission.CALL_PHONE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    // permission is granted
                    call()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    // check for permanent denial of permission
                    if (response.isPermanentlyDenied) {
                        (requireActivity() as  AppCompatActivity).openAlertDialogWithTwoClick(
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
                    (requireActivity() as  AppCompatActivity).openAlertDialogWithTwoClick(
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
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivityForResult(intent, REQUEST_CALL_PERMISSION_SETTINGS)
        }
    }


    private fun call() {
        if (!Utility.isValueNull(contactInfo?.contactPhone)) {
            val intent = Intent(Intent.ACTION_CALL)
            val phoneNumber = contactInfo?.contactPhone
            intent.data = Uri.parse("tel:$phoneNumber")
            requireActivity().startActivity(intent)
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

    private fun sendEmail() {
        if (!Utility.isValueNull(contactInfo?.contactEmail)) {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(contactInfo!!.contactEmail))
            startActivity(Intent.createChooser(intent, "Enquire"))
        }
    }
}
