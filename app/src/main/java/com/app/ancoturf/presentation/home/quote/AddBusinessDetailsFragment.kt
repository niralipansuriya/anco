package com.app.ancoturf.presentation.home.quote

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.BuildConfig
import com.app.ancoturf.R
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.portfolio.remote.entity.QuoteAncoProductRequest
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.extension.*
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.CheckImageOrientation
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
import kotlinx.android.synthetic.main.fragment_add_business_details.*
import kotlinx.android.synthetic.main.header.*
import java.io.File
import java.io.IOException
import javax.inject.Inject

class AddBusinessDetailsFragment(private var quoteAncoProductRequest: QuoteAncoProductRequest?) :
    BaseFragment(),
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var quoteViewModel: QuoteViewModel? = null

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private var cameraUri: Uri? = null
    private var selectedPhotoUri: Uri? = null
    private var selectedFilePath: String? = null

    private var userInfo: UserInfo? = null

    override fun getContentResource(): Int = R.layout.fragment_add_business_details

    override fun viewModelSetup() {
        quoteViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[QuoteViewModel::class.java]
        initObservers()
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
        txtSubmitBusinessDetail.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        txtAddLogo.setOnClickListener(this)
        imgBack.setOnClickListener(this)
        imgCheckGST.setOnClickListener(this)

        Utility.showProgress(requireContext(), "", false)
        quoteViewModel?.callGetUserDetails()
        setData()
    }

    private fun setData() {
        if (userInfo == null)
            userInfo = Gson().fromJson(sharedPrefs.userInfo, UserInfo::class.java)

        if (userInfo != null && userInfo?.business != null) {
            if (!Utility.isValueNull(userInfo?.business?.contactName))
                edtContactName.setText(userInfo?.business?.contactName)

            if (!Utility.isValueNull(userInfo?.business?.businessName))
                edtBusinessName.setText(userInfo?.business?.businessName)

            if (!Utility.isValueNull(userInfo?.business?.address))
                edtAddress.setText(userInfo?.business?.address)

            if (!Utility.isValueNull(userInfo?.business?.email))
                edtEmail.setText(userInfo?.business?.email)

            if (!Utility.isValueNull(userInfo?.business?.mobileNumber))
                edtMobileNum.setText(userInfo?.business?.mobileNumber)

            if (!Utility.isValueNull(userInfo?.business?.phoneNumber))
                edtPhone.setText(userInfo?.business?.phoneNumber)

            if (!Utility.isValueNull(userInfo?.business?.abn))
                edtAbn.setText(userInfo?.business?.abn)

            if (!Utility.isValueNull(userInfo?.business?.webUrl))
                edtWeb.setText(userInfo?.business?.webUrl)

            if (!Utility.isValueNull(userInfo?.business?.paymentTerms))
                edtPaymentTerms.setText(userInfo?.business?.paymentTerms)

            if (!Utility.isValueNull(userInfo?.business?.disclaimer))
                edtDisclaimer.setText(userInfo?.business?.disclaimer)

            if (!Utility.isValueNull(userInfo?.business?.logoUrl)) {
                Glide.with(requireContext())
                    .load(BuildConfig.API_BASE_URL + userInfo?.business?.logoUrl)
                    .into(imgBusinessLogo)
            }

            imgCheckGST.setImageResource(if (userInfo?.business?.registeredForGst.equals("1")) R.drawable.ic_checkbox_h else R.drawable.ic_checkbox)
        }

        if (userInfo != null && userInfo?.business == null) {
            userInfo?.business = UserInfo.Business()
        }
    }


    private fun initObservers() {

        quoteViewModel!!.errorLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null) {
                Utility.cancelProgress()
                if (it.equals(ErrorConstants.UNAUTHORIZED_ERROR_CODE)) {
                    if (activity is HomeActivity) {
                        (activity as HomeActivity).cartViewModel?.deleteProduct(null)
                        (activity as HomeActivity).cartViewModel?.deleteCoupon(null)
                    } else if (activity is PaymentActivity) {
                        (activity as PaymentActivity).cartViewModel?.deleteProduct(null)
                        (activity as PaymentActivity).cartViewModel?.deleteCoupon(null)
                    } else if (activity is ProfileActivity) {
                        (activity as ProfileActivity).cartViewModel?.deleteProduct(null)
                        (activity as ProfileActivity).cartViewModel?.deleteCoupon(null)
                    }
                    (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                } else {
                    it?.let { it1 -> shortToast(it1) }
                }
                quoteViewModel!!._errorLiveData.value = null
            }
        })

        quoteViewModel!!.addEditQuotesLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null && it.data != null) {
                //                (requireActivity() as AppCompatActivity).hideKeyboard()
                shortToast(it.message)
                userInfo = Gson().fromJson(sharedPrefs.userInfo, UserInfo::class.java)
                userInfo?.business = UserInfo.Business(
                    abn = it.data!!.users.business.abn,
                    id = it.data!!.users.business.id,
                    contactName = it.data!!.users.business.contactName,
                    registeredForGst = it.data!!.users.business.registeredForGst,
                    logoUrl = it.data!!.users.business.logoUrl,
                    disclaimer = it.data!!.users.business.disclaimer,
                    paymentTerms = it.data!!.users.business.paymentTerms,
                    webUrl = it.data!!.users.business.webUrl,
                    email = it.data!!.users.business.email,
                    phoneNumber = it.data!!.users.business.phoneNumber,
                    mobileNumber = it.data!!.users.business.mobileNumber,
                    address = it.data!!.users.business.address,
                    businessName = it.data!!.users.business.businessName,
                    userId = it.data!!.users.business.businessName
                )
                sharedPrefs.userInfo = Gson().toJson(userInfo)
                (requireActivity() as AppCompatActivity).pushFragment(
                    AddCustomerInfoFragment(null, it.data),
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )
                quoteViewModel!!._addEditQuotesLiveData.value = null
            }

//            quoteViewModel!!.addEditQuotesLiveData == null
        })

        quoteViewModel!!.userInfoLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null) {
                userInfo = it
                setData()
            }
        })
    }


    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {
            R.id.imgBell -> {
                openNotificationFragment()
            }
            R.id.txtSubmitBusinessDetail -> {
                if (Utility.isValueNull(edtContactName.text.toString())) {
                    shortToast(getString(R.string.blank_contact_name_message))
                } else if (Utility.isValueNull(edtBusinessName.text.toString())) {
                    shortToast(getString(R.string.blank_business_name_message))
                } else if (Utility.isValueNull(edtAddress.text.toString())) {
                    shortToast(getString(R.string.blank_address_message))
                } else if (Utility.isValueNull(edtEmail.text.toString())) {
                    shortToast(getString(R.string.blank_email_message))
                } else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.text.toString()).matches()) {
                    shortToast(getString(R.string.invalid_email_message))
                } else if (Utility.isValueNull(edtMobileNum.text.toString())) {
                    shortToast(getString(R.string.blank_phone_number_message))
                } else if (edtMobileNum.text.toString().length < 10) {
                    shortToast(getString(R.string.invalid_phone_number_message))
                } else if (Utility.isValueNull(edtAbn.text.toString())) {
                    shortToast(getString(R.string.blank_abn_message))
                } else if (edtAbn.text.toString().length < 11) {
                    shortToast(getString(R.string.invalid_abn_messgae))
                }
//                else {
//                if(!Utility.isValueNull(edtWeb.text.toString()) && (edtWeb.text.toString().contains("http://") || edtWeb.text.toString().contains("https://"))) {
                else if (!Utility.isValueNull(edtWeb.text.toString()) && !(edtWeb.text.toString()
                        .contains("."))
                ) {
                    shortToast(getString(R.string.invalid_weburl_message))
                } else {
                    Utility.showProgress(requireContext(), "", false)
                    var ancoProducts = ArrayList<QuoteAncoProductRequest>()
                    if (quoteAncoProductRequest != null) {
                        ancoProducts.add(quoteAncoProductRequest!!)
                    }
                    quoteViewModel?.callAddEditBusinessDetails(
                        0,
                        edtContactName.text.toString(),
                        edtBusinessName.text.toString(),
                        edtAbn.text.toString(),
                        edtAddress.text.toString(),
                        edtMobileNum.text.toString(),
                        edtPhone.text.toString(),
                        edtEmail.text.toString(),
                        edtWeb.text.toString(),
                        edtPaymentTerms.text.toString(),
                        edtDisclaimer.text.toString(),
                        userInfo?.business!!.registeredForGst,
                        selectedFilePath,
                        ancoProducts
                    )
//                    }
                }
            }

            R.id.imgBack -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }

            R.id.txtAddLogo -> {
                openImageCaptureDialog()
            }

            R.id.imgCheckGST -> {
                userInfo?.business?.registeredForGst =
                    if (userInfo?.business?.registeredForGst.equals("1")) "0" else "1"
                imgCheckGST.setImageResource(if (userInfo?.business?.registeredForGst.equals("1")) R.drawable.ic_checkbox_h else R.drawable.ic_checkbox)
            }

            R.id.imgLogo -> {
//                (requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    HomeFragment(),
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgBell -> {
                openNotificationFragment()
            }
            R.id.imgMore -> {
//                (requireActivity() as AppCompatActivity).hideKeyboard()
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

            R.id.imgCart -> {
//                (requireActivity() as AppCompatActivity).hideKeyboard()
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
        }
    }


    private fun openImageCaptureDialog() {
        val builder = AlertDialog.Builder(activity as AppCompatActivity)
        builder.setTitle(R.string.dialog_title_choose_image_from)
        builder.setItems(
            resources.getStringArray(R.array.dialog_choose_image)
        ) { _, which ->
            when (which) {
                0 -> checkCameraPermission()
                1 -> checkPermissionForGallery()
            }
        }
        builder.show()
    }

    private fun checkCameraPermission() {
        Log.e("3>", "Inside Camera Permission")
        Dexter.withActivity(activity as AppCompatActivity)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    // permission is granted
                    takePhoto()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    // check for permanent denial of permission
                    if (response.isPermanentlyDenied) {
                        (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                            title = getString(R.string.missing_permission_title)
                            , message = getString(R.string.missing_permission_message)
                            , positiveButton = "SETTING"
                            , nagetiveButton = getString(android.R.string.cancel)
                            , onDialogButtonClick = object : OnDialogButtonClick {
                                override fun onPositiveButtonClick() {
                                    openSettings()
                                }

                                override fun onNegativeButtonClick() {
                                }

                            })
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                        title = getString(R.string.missing_permission_title)
                        , message = getString(R.string.missing_permission_message)
                        , positiveButton = "Grant"
                        , nagetiveButton = getString(android.R.string.cancel)
                        , onDialogButtonClick = object : OnDialogButtonClick {
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

    private fun checkPermissionForGallery() {
        Log.e("3>", "Inside Gallery Permission")
        Dexter.withActivity(activity as AppCompatActivity)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    // permission is granted
                    fromGallery()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    // check for permanent denial of permission
                    if (response.isPermanentlyDenied) {
                        (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                            title = getString(R.string.missing_permission_title)
                            , message = getString(R.string.missing_permission_message)
                            , positiveButton = "SETTING"
                            , nagetiveButton = getString(android.R.string.cancel)
                            , onDialogButtonClick = object : OnDialogButtonClick {
                                override fun onPositiveButtonClick() {
                                    openSettings()
                                }

                                override fun onNegativeButtonClick() {
                                }

                            })
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                        title = getString(R.string.missing_permission_title)
                        , message = getString(R.string.missing_permission_message)
                        , positiveButton = "Grant"
                        , nagetiveButton = getString(android.R.string.cancel)
                        , onDialogButtonClick = object : OnDialogButtonClick {
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

    private fun fromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            this.startActivityForResult(intent, REQUEST_GET_IMAGE_GALLERY)
        } else {
            (requireActivity() as AppCompatActivity).shortToast(
                getString(
                    R.string.msg_app_not_found,
                    getString(R.string.gallery)
                )
            )
        }
    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(intent, REQUEST_STORAGE_PERMISSION_SETTINGS)
        }
    }

    private fun takePhoto() {
        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //Create a file to store the image
        var photoFile: File? = null
        try {
            photoFile = createImageFile()
        } catch (ex: IOException) {
            ex.printStackTrace()
            (requireActivity() as AppCompatActivity).shortToast(getString(R.string.msg_photo_file_create_error))
            // Error occurred while creating the File
        }
        photoFile?.let {
            selectedFilePath = photoFile.absolutePath
            cameraUri =
                FileProvider.getUriForFile(
                    requireActivity(),
                    getString(R.string.file_provider_authorities),
                    photoFile
                )
            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                pictureIntent.clipData = ClipData.newRawUri("", cameraUri)
                pictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            if (pictureIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(pictureIntent, REQUEST_GET_IMAGE_CAMERA)
            } else {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.msg_app_not_found))
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val dirCreated: Boolean
        var storageDir = requireActivity().filesDir
        if (storageDir == null) {
            val externalStorage =
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (externalStorage == null) {
                storageDir = File(requireActivity().cacheDir, Environment.DIRECTORY_PICTURES)
                dirCreated = storageDir.exists() || storageDir.mkdirs()
            } else
                dirCreated = true
        } else {
            storageDir = File(requireActivity().filesDir, Environment.DIRECTORY_PICTURES)
            dirCreated = storageDir.exists() || storageDir.mkdirs()
        }

        return if (dirCreated) {
            File.createTempFile(
                IMG_PROFILE, //prefix
                ".jpg", //suffix
                storageDir   //directory
            )
        } else
            null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GET_IMAGE_CAMERA && cameraUri != null) {
                selectedPhotoUri = cameraUri
            } else if (requestCode == REQUEST_GET_IMAGE_GALLERY && data != null) {
                selectedPhotoUri = data.data
//                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                selectedPhotoUri?.let {
                    selectedFilePath = getPath(it)
                }
//                }
            } else if (requestCode == REQUEST_STORAGE_PERMISSION_SETTINGS) {
                checkPermissionForGallery()
            }
            try {
                CheckImageOrientation.handleSamplingAndRotationBitmap(
                    requireContext(),
                    selectedPhotoUri!!,
                    selectedFilePath
                )?.let { bitmap ->
                    Glide.with(requireContext()).load(selectedFilePath).into(imgBusinessLogo)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getPath(uri: Uri): String {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = requireActivity().contentResolver.query(uri, proj, null, null, null)
        cursor?.let {
            if (cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndexOrThrow(proj[0]))
            }
            cursor.close()
        }
        if (result == null) {
            result = RESULT_NOT_FOUND
        }
        return result as String
    }

    companion object {
        const val REQUEST_GET_IMAGE_GALLERY = 1
        const val REQUEST_GET_IMAGE_CAMERA = 2
        const val REQUEST_STORAGE_PERMISSION_SETTINGS = 7
        const val IMG_PROFILE = "IMG_PROFILE"
        const val RESULT_NOT_FOUND = "Not found"
    }

}
