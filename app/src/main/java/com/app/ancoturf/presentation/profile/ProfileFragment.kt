package com.app.ancoturf.presentation.profile

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.graphics.drawable.Drawable
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.extension.*
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.cart.CartViewModel
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.order.OrderFragment
import com.app.ancoturf.presentation.login.LoginActivity
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.CheckImageOrientation
import com.app.ancoturf.utils.Utility
import com.app.ancoturf.utils.interfces.OnDialogButtonClick
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.header.*
import java.io.File
import java.io.IOException
import javax.inject.Inject

class ProfileFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    var cartViewModel: CartViewModel? = null
    var profileViewModel: ProfileViewModel? = null
    var userInfo: UserInfo? = null
    private var cameraUri: Uri? = null
    private var selectedPhotoUri: Uri? = null
    private var selectedFilePath: String? = null
    private var isEditable: Boolean = false
    override fun getContentResource(): Int = R.layout.fragment_profile

    override fun viewModelSetup() {
        cartViewModel = ViewModelProviders.of(this, viewModelFactory)[CartViewModel::class.java]
        profileViewModel =
            ViewModelProviders.of(this, viewModelFactory)[ProfileViewModel::class.java]
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

        imgLogo.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        txtAccountLabel.setOnClickListener(this)
        txtInfoLabel.setOnClickListener(this)
        txtCreditLabel.setOnClickListener(this)
        txtChangePassword.setOnClickListener(this)
        imgLogout.setOnClickListener(this)
        txtViewBillHistory.setOnClickListener(this)
        imgProfile.setOnClickListener(this)
        txtEditLabel.setOnClickListener(this)
        Utility.showProgress(requireContext(), "", false)
        profileViewModel?.callGetUserDetails()
    }

    private fun initObservers() {
        profileViewModel!!.userInfoLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null) {
                userInfo = it
                if (isEditable) shortToast(getString(R.string.profile_updated_successfully))
                setData()
                profileViewModel!!._userInfoLiveData.value = null
            }
        })
        profileViewModel!!.errorLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                if (it.equals(ErrorConstants.UNAUTHORIZED_ERROR_CODE)) {
                    (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                } else {
                    it?.let { it1 -> shortToast(it1) }
                }
                profileViewModel?._errorLiveData?.value = null
            }
        })
        profileViewModel!!.logoutLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null) {
                var fcmToken = sharedPrefs.fcmToken
                sharedPrefs.clear()
                sharedPrefs.isWelcomeVisited = true
                sharedPrefs.fcmToken = fcmToken
                Log.e("accessToken", sharedPrefs.accessToken)
                cartViewModel?.deleteProduct(null)
                cartViewModel?.deleteCoupon(null)
//            Handler().postDelayed({
                var intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finishAffinity()
            }
        })
    }
    private fun setData() {
        if (userInfo != null) {
            progress.visibility = View.VISIBLE
            if (!Utility.isValueNull(userInfo!!.imageUrl)) {
                Glide.with(requireActivity())
                    .load(userInfo!!.imageUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .error(R.mipmap.ic_launcher_round)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .override(Constants.COMPRESS_IMAGE_OVERRIDE_WIDTH,Constants.COMPRESS_IMAGE_OVERRIDE_HEIGHT)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progress.visibility = View.GONE
                            return false
                        }
                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            progress.visibility = View.GONE
                            return false
                        }
                    })
                    .into(imgProfile)
            } else {
                progress.visibility = View.GONE
            }
            isEditable = false
            setEditable()
            if (!(Utility.isValueNull(userInfo!!.firstName) && Utility.isValueNull(userInfo!!.lastName))) {
                txtUserName.text = userInfo!!.firstName + " " + userInfo!!.lastName
            } else {
                when {
                    !Utility.isValueNull(userInfo!!.firstName) -> txtUserName.text =
                        userInfo!!.firstName
                    !Utility.isValueNull(userInfo!!.lastName) -> txtUserName.text =
                        userInfo!!.lastName
                    else -> txtUserName.text = ""
                }
            }
            if (userInfo!!.privilege != null && !Utility.isValueNull(userInfo!!.privilege.name))
                txtUserType.text = userInfo!!.privilege.name
            else
                txtUserType.text = ""
            txtNumOrders.text = "${userInfo!!.orderCount}"
            txtNumQuotes.text = "${userInfo!!.quoteCount}"
            txtNumCustomers.text = "${userInfo!!.customerCount}"
            if (userInfo!!.lastOrder != null) {
                grpLatestBill.visibility = View.VISIBLE
                if (!Utility.isValueNull(userInfo!!.lastOrder.invoiceDate))
                    txtBillIssued.text = Utility.changeDateFormat(
                        userInfo!!.lastOrder.invoiceDate,
                        Utility.DATE_FORMAT_YYYY_MM_DD,
                        Utility.DATE_FORMAT_D_MMMM_YYYY
                    )
                else
                    txtBillIssued.text = ""
                if (!Utility.isValueNull(userInfo!!.lastOrder.referenceNumber))
                    txtBillNumber.text = "#${userInfo!!.lastOrder.referenceNumber}"
                else
                    txtBillNumber.text = ""

                if (!Utility.isValueNull(userInfo!!.lastOrder.totalCartPrice))
                    txtLastBill.text =
                        "$${Utility.formatNumber(Utility.roundTwoDecimals(userInfo!!.lastOrder.totalCartPrice!!.toDouble()).toFloat())}"
                else
                    txtLastBill.text = ""
            } else {
                grpLatestBill.visibility = View.GONE
            }
            if (!Utility.isValueNull(userInfo!!.firstName))
                edtFirstName.setText(userInfo!!.firstName)
            else
                edtFirstName.setText("")
            if (!Utility.isValueNull(userInfo!!.lastName))
                edtLastName.setText(userInfo!!.lastName)
            else
                edtLastName.setText("")
            if (!Utility.isValueNull(userInfo!!.email))
                edtEmail.setText(userInfo!!.email)
            else
                edtEmail.setText("")
            if (userInfo!!.idCmsPrivileges != Constants.RETAILER) {
//                viewCredit.visibility = View.VISIBLE
                txtCreditLabel.visibility = View.VISIBLE
                if (userInfo!!.business != null) {
                    grpBusinessDetail.visibility = View.VISIBLE
                    if (!Utility.isValueNull(userInfo!!.business.businessName))
                        edtBusinessName.setText(userInfo!!.business.businessName)
                    else
                        edtBusinessName.setText("")

                    if (!Utility.isValueNull(userInfo!!.business.abn))
                        edtAbn.setText(userInfo!!.business.abn)
                    else
                        edtAbn.setText("")

                    if (!Utility.isValueNull(userInfo!!.business.phoneNumber))
                        edtPhone.setText(userInfo!!.business.phoneNumber)
                    else
                        edtPhone.setText("")
                }
            } else {
                grpBusinessDetail.visibility = View.GONE
                viewCredit.visibility = View.GONE
                txtCreditLabel.visibility = View.GONE
//                //ToDO: Digvesh added by digvesh to solve image upload issue for landscaper and not approved from admin
//                if (userInfo!!.business != null) {
//                    if (!Utility.isValueNull(userInfo!!.business.businessName))
//                        edtBusinessName.setText(userInfo!!.business.businessName)
//                    else
//                        edtBusinessName.setText("")
//
//                    if (!Utility.isValueNull(userInfo!!.business.abn))
//                        edtAbn.setText(userInfo!!.business.abn)
//                    else
//                        edtAbn.setText("")
//
//                    if (!Utility.isValueNull(userInfo!!.business.phoneNumber))
//                        edtPhone.setText(userInfo!!.business.phoneNumber)
//                    else
//                        edtPhone.setText("")
//                }
            }

            if (userInfo!!.credit != null) {
                edtEarnedCredit.text = userInfo!!.credit!!.total_credit
                edtAvailableCredit.text = userInfo!!.credit!!.available_credit
            } else {
                edtEarnedCredit.text = "0"
                edtAvailableCredit.text = "0"
            }
        }
    }

    private fun setEditable() {
        txtEditLabel.text = if (isEditable) getString(R.string.update) else getString(R.string.edit)
        edtFirstName.isEnabled = isEditable
        edtLastName.isEnabled = isEditable
        edtEmail.isEnabled = isEditable
        edtBusinessName.isEnabled = isEditable
        edtAbn.isEnabled = isEditable
        edtPhone.isEnabled = isEditable
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
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

            R.id.imgCart -> {
//                (requireActivity() as AppCompatActivity).hideKeyboard()
                Log.d("trace ", "======================  ${sharedPrefs.totalProductsInCart}")
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

            R.id.imgSearch -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    SearchFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgLogo -> {
                requireActivity().startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finishAffinity()
            }

            R.id.txtAccountLabel -> {
                viewAccount.visibility = View.VISIBLE
                txtAccountLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.black
                    )
                )
                layoutAccount.visibility = View.VISIBLE
                viewInfo.visibility = View.GONE
                txtInfoLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.light_grey
                    )
                )
                layoutInfo.visibility = View.GONE
                viewCredit.visibility = View.GONE
                txtCreditLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.light_grey
                    )
                )
                layoutCredit.visibility = View.GONE
            }

            R.id.txtInfoLabel -> {
                viewAccount.visibility = View.GONE
                txtAccountLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.light_grey
                    )
                )
                layoutAccount.visibility = View.GONE
                viewInfo.visibility = View.VISIBLE
                txtInfoLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.black
                    )
                )
                layoutInfo.visibility = View.VISIBLE
                viewCredit.visibility = View.GONE
                txtCreditLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.light_grey
                    )
                )
                layoutCredit.visibility = View.GONE
            }

            R.id.txtCreditLabel -> {
                viewAccount.visibility = View.GONE
                txtAccountLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.light_grey
                    )
                )
                layoutAccount.visibility = View.GONE
                viewInfo.visibility = View.GONE
                txtInfoLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.light_grey
                    )
                )
                layoutInfo.visibility = View.GONE
                viewCredit.visibility = View.VISIBLE
                txtCreditLabel.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.black
                    )
                )
                layoutCredit.visibility = View.VISIBLE
            }

            R.id.imgLogout -> {
                (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                    getString(R.string.app_name),
                    getString(R.string.logout_confirmation_msg),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel),
                    object : OnDialogButtonClick {
                        override fun onPositiveButtonClick() {
                            var fcmToken = sharedPrefs.fcmToken
                            Utility.showProgress(requireContext(), "", false)
                            profileViewModel?.logout(fcmToken)

                            /*var fcmToken = sharedPrefs.fcmToken
                            sharedPrefs.clear()
                            sharedPrefs.isWelcomeVisited = true
                            sharedPrefs.fcmToken = fcmToken
                            Log.e("accessToken", sharedPrefs.accessToken)

                            cartViewModel?.deleteProduct(null)
                            cartViewModel?.deleteCoupon(null)
//            Handler().postDelayed({
                            var intent = Intent(requireActivity(), LoginActivity::class.java)
                            startActivity(intent)
                            requireActivity().finishAffinity()*/

//            sharedPrefs.isLogged = false
//            sharedPrefs.accessToken = ""
//            sharedPrefs.userId = 0
//            sharedPrefs.availableCredit = 0
//            sharedPrefs.userEmail = ""
//            sharedPrefs.userName = ""
//            sharedPrefs.totalProductsInCart = 0

//            },1000)
                        }

                        override fun onNegativeButtonClick() {
                        }

                    })
            }

            R.id.txtChangePassword -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ChangePasswordFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.txtViewBillHistory -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    OrderFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgProfile -> {
                openImageCaptureDialog()
            }

            R.id.txtEditLabel -> {
                if (isEditable) {
                    if (edtFirstName.text.toString().isEmpty()) {
                        shortToast(getString(R.string.black_firstname_message))
                    } else if (edtLastName.text.toString().isEmpty()) {
                        shortToast(getString(R.string.black_lastname_message))
                    } else if (edtEmail.text.toString().isEmpty()) {
                        shortToast(getString(R.string.blank_email_message))
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.text.toString()).matches()) {
                        shortToast(getString(R.string.invalid_email_message))
                    } else if (userInfo!!.idCmsPrivileges != Constants.RETAILER && edtBusinessName.text.toString().isEmpty()) {
                        shortToast(getString(R.string.blank_business_name_message))
                    } else if (userInfo!!.idCmsPrivileges != Constants.RETAILER && edtAbn.text.toString().isEmpty()) {
                        shortToast(getString(R.string.blank_abn_message))
                    } else if (userInfo!!.idCmsPrivileges != Constants.RETAILER && edtAbn.text.toString().length < 11) {
                        shortToast(getString(R.string.invalid_abn_messgae))
                    } /*else if (userInfo!!.idCmsPrivileges != Constants.RETAILER && edtPhone.text.toString().isEmpty()) {
                        shortToast(getString(R.string.invalid_phone_number_message))
                    }*/
                    else if (edtPhone.text.toString().isEmpty()) { //nnn done 27/11/2020
                        shortToast(getString(R.string.invalid_phone_number_message))
                    } else {
                        (requireActivity() as AppCompatActivity).hideKeyboard()
                        Utility.showProgress(requireContext(), "", false)
                        profileViewModel?.callUpdateProfile(
                            userInfo!!.idCmsPrivileges,
                            edtFirstName.text.toString(),
                            edtLastName.text.toString(),
                            edtEmail.text.toString(),
                            selectedFilePath,
                            edtBusinessName.text.toString(),
                            edtAbn.text.toString(),
                            edtPhone.text.toString()
                        )
                    }
                } else {
                    isEditable = true
                    setEditable()
                    edtFirstName.requestFocus()
                    edtFirstName.setSelection(edtFirstName.text.toString().length)
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
                    Glide.with(requireActivity())
                        .load(selectedFilePath)
                        .apply(RequestOptions.circleCropTransform())
                        .error(R.mipmap.ic_launcher_round)
                        .placeholder(R.mipmap.ic_launcher_round)
                        .into(imgProfile)

                    Utility.showProgress(requireContext(), "", false)
                    profileViewModel?.callUpdateProfile(
                        userInfo!!.idCmsPrivileges,
                        edtFirstName.text.toString(),
                        edtLastName.text.toString(),
                        edtEmail.text.toString(),
                        selectedFilePath,
                        edtBusinessName.text.toString(),
                        edtAbn.text.toString(),
                        edtPhone.text.toString()
                    )
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
