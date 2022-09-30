package com.app.ancoturf.presentation.home.quote

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.BuildConfig
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.portfolio.remote.entity.QuoteAncoProductRequest
import com.app.ancoturf.data.portfolio.remote.entity.QuoteNonAncoProductRequest
import com.app.ancoturf.data.product.remote.entity.response.ProductCategoryData
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.data.quote.remote.entity.QuoteProducts
import com.app.ancoturf.data.quote.remote.entity.response.QuoteDetailsResponse
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.openAlertDialogWithTwoClick
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.cart.CartViewModel
import com.app.ancoturf.presentation.cart.DeliveryDatesResponseModel
import com.app.ancoturf.presentation.cart.DeliveryDatesViewModel
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.portfolio.interfaces.OnProductSelectedListener
import com.app.ancoturf.presentation.home.quote.adapters.AncoQuoteProductsAdapter
import com.app.ancoturf.presentation.home.quote.adapters.QuoteProductsAdapter
import com.app.ancoturf.presentation.home.quote.interfaces.OnProductCategorySelected
import com.app.ancoturf.presentation.home.quote.interfaces.OnProductChangeListener
import com.app.ancoturf.presentation.home.shop.adapters.ProductCategoryAdapter
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.CheckImageOrientation
import com.app.ancoturf.utils.InputFilterMinMax
import com.app.ancoturf.utils.Logger
import com.app.ancoturf.utils.Utility
import com.app.ancoturf.utils.interfces.OnDialogButtonClick
import com.bumptech.glide.Glide
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.fragment_add_edit_quote.*
import kotlinx.android.synthetic.main.header.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class AddEditQuoteFragment() :
    BaseFragment(),
    View.OnClickListener {

    private var products: ArrayList<ProductsResponse.Data> = ArrayList()
    private var quoteId: Int = Constants.VIEW_QUOTE
    private var quoteMode: Int = 0

    private var dialogAddCustomProduct: Dialog? = null
    private var dialogAddAncoProducts: Dialog? = null
    private var dialogSendQuote: Dialog? = null
    private lateinit var imgProductLogo: ImageView
    private lateinit var txtAddProductLogo: TextView
    private lateinit var imgDeleteImage: ImageView
    private lateinit var edtProductQuantity: EditText

    private var productCategoryName = "All"

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var quoteViewModel: QuoteViewModel? = null

    private var productCategories = ArrayList<ProductCategoryData>()

    private var quoteDetailsResponse: QuoteDetailsResponse? = null

    private var quoteProducts: ArrayList<QuoteProducts>? = null
    private var ancoQuoteProductsAdapter: AncoQuoteProductsAdapter? = null

    private val deletedCustomProductIds = ArrayList<Int>()
    private val deletedProductIds = ArrayList<Int>()
    private var sendQuoteTo = ArrayList<String>()
    private var cartViewModel: CartViewModel? = null
    var addToCartProduct: ProductDetailResponse? = null

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    //    private var quoteMode = Constants.VIEW_QUOTE
    private var businessInfoEditMode = false
    private var customerInfoEditMode = false
    private var quoteSummaryEditMode = false
    private var addProductEditMode = false

    private var cameraUriForBusiness: Uri? = null
    private var selectedPhotoUriForBusiness: Uri? = null
    private var selectedFilePathForBusiness: String? = null
    private var cameraUriForProduct: Uri? = null
    private var selectedPhotoUriForProduct: Uri? = null
    private var selectedFilePathForProduct: String? = null
    private var totalQuotedPrice: Float = 0.0f
    private var getDeliveryDatesViewModel: DeliveryDatesViewModel? = null //Dev_N
    var deliveryDates: DeliveryDatesResponseModel? = null //Dev_N
    private var deliveryDate = "" //Dev_N
    var pageNo = 1
    var isLoad = false
    var itemCount = 0

    override fun getContentResource(): Int = R.layout.fragment_add_edit_quote

    override fun viewModelSetup() {
        quoteViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[QuoteViewModel::class.java]

        cartViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[CartViewModel::class.java]
        //Dev_N
        getDeliveryDatesViewModel =
            ViewModelProviders.of(
                requireActivity(),
                viewModelFactory
            )[DeliveryDatesViewModel::class.java]
        initObservers()
        quoteViewModel?.callGetProductCategories()
    }

    override fun viewSetup() {

        arguments?.let {
            quoteMode = it.getInt("quoteMode")
            quoteId = it.getInt("quoteId")
            if (quoteMode == Constants.EDIT_QUOTE) {
                businessInfoEditMode = true
                customerInfoEditMode = true
                quoteSummaryEditMode = true
                addProductEditMode = true
            } else if (quoteMode == Constants.DRAFT_QUOTE) {
                businessInfoEditMode = true;
                customerInfoEditMode = true;
                quoteSummaryEditMode = true;
                addProductEditMode = true;
            }
        }

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

        Utility.showProgress(requireContext(), "", false)
        quoteViewModel?.callGetQuoteDetails(quoteId)

        setData()
        ivClose.setOnClickListener(this)
        ivOpenPDF.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgBusinessDropdown.setOnClickListener(this)
        imgCustomerDropdown.setOnClickListener(this)
        imgQuoteSummaryDropdown.setOnClickListener(this)
        imgEditQuote.setOnClickListener(this)
        imgEditBusinessInfo.setOnClickListener(this)
        imgEditCustomerInfo.setOnClickListener(this)
        imgEditQuoteSummary.setOnClickListener(this)
        imgEditAddProduct.setOnClickListener(this)
        imgAddProductDropdown.setOnClickListener(this)
        txtAddNonAncoProduct.setOnClickListener(this)
        txtConfirmQuote.setOnClickListener(this)
        imgCheckGST.setOnClickListener(this)
        txtAddLogo.setOnClickListener(this)
        txtSave.setOnClickListener(this)
        txtCancel.setOnClickListener(this)
        txtCreateOrder.setOnClickListener(this)
//        txtNextAddProducts.setOnClickListener(this)

        edtDelivery.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        edtDelivery.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (Utility.isValueNull(edtDelivery.text.toString()) || Utility.isValueNull(
                        s.toString().replace(
                            "$",
                            ""
                        ).replace(",", "")
                    )
                ) {
                    txtTotalCost.setText(
                        getString(
                            R.string.quote_total_cost,
                            Utility.formatNumber(Utility.roundTwoDecimals(totalQuotedPrice).toFloat())
                        )
                    )
                } else {
                    if (before != count)
                        edtDelivery.setText(
                            "$" + (Utility.formatNumber(
                                s.toString().replace(
                                    "$",
                                    ""
                                ).replace(",", "").toLong()
                            ))
                        )
                    edtDelivery.text?.length?.let { edtDelivery.setSelection(it) }

                    txtTotalCost.setText(
                        getString(
                            R.string.quote_total_cost,
                            (Utility.formatNumber(
                                Utility.roundTwoDecimals(
                                    totalQuotedPrice + edtDelivery.text.toString().replace(
                                        "$",
                                        ""
                                    ).replace(",", "").toFloat()
                                ).toFloat()
                            ))
                        )
                    )
                }
            }
        })
    }

    private fun setData() {
        when (quoteMode) {
            Constants.ADD_QUOTE -> {
                imgEditBusinessInfo.visibility = VISIBLE
                imgEditCustomerInfo.visibility = VISIBLE
                imgEditQuoteSummary.visibility = VISIBLE
                imgEditAddProduct.visibility = VISIBLE
                layoutAddProducts.visibility = VISIBLE
                txtAddLogo.visibility = VISIBLE
                txtImgName.visibility = VISIBLE
                quoteSummaryEditMode = true
                imgEditQuoteSummary.setColorFilter(
                    if (quoteSummaryEditMode) ContextCompat.getColor(
                        activity as AppCompatActivity,
                        R.color.theme_green
                    ) else ContextCompat.getColor(
                        activity as AppCompatActivity,
                        android.R.color.black
                    )
                )
                layoutInnerQuoteSummary.visibility =
                    if (quoteSummaryEditMode) VISIBLE else GONE
                imgQuoteSummaryDropdown.rotation =
                    if (layoutInnerQuoteSummary.visibility == VISIBLE) 180f else 0f
                addProductEditMode = false
                expandAddProductAndEnableEdit()
            }
            Constants.EDIT_QUOTE -> {
                imgEditBusinessInfo.visibility = VISIBLE
                imgEditCustomerInfo.visibility = VISIBLE
                imgEditQuoteSummary.visibility = VISIBLE
                imgEditAddProduct.visibility = VISIBLE
                layoutQuoteSummary.visibility = VISIBLE
                layoutAddProducts.visibility = VISIBLE
                txtAddLogo.visibility = VISIBLE
                txtImgName.visibility = VISIBLE
//                txtNextAddProducts.visibility = VISIBLE
                imgEditQuote.setColorFilter(
                    ContextCompat.getColor(
                        activity as AppCompatActivity,
                        R.color.theme_green
                    )
                )
                imgEditBusinessInfo.setColorFilter(
                    if (businessInfoEditMode) ContextCompat.getColor(
                        activity as AppCompatActivity,
                        R.color.theme_green
                    ) else ContextCompat.getColor(
                        activity as AppCompatActivity,
                        android.R.color.black
                    )
                )
                imgEditCustomerInfo.setColorFilter(
                    if (customerInfoEditMode) ContextCompat.getColor(
                        activity as AppCompatActivity,
                        R.color.theme_green
                    ) else ContextCompat.getColor(
                        activity as AppCompatActivity,
                        android.R.color.black
                    )
                )
                imgEditQuoteSummary.setColorFilter(
                    if (quoteSummaryEditMode) ContextCompat.getColor(
                        activity as AppCompatActivity,
                        R.color.theme_green
                    ) else ContextCompat.getColor(
                        activity as AppCompatActivity,
                        android.R.color.black
                    )
                )
            }

            Constants.DRAFT_QUOTE -> {
                imgEditBusinessInfo.visibility = VISIBLE
                imgEditCustomerInfo.visibility = VISIBLE
                imgEditQuoteSummary.visibility = VISIBLE
                imgEditAddProduct.visibility = VISIBLE
                layoutAddProducts.visibility = VISIBLE
                layoutQuoteSummary.visibility = VISIBLE
                txtAddLogo.visibility = VISIBLE
                txtImgName.visibility = VISIBLE
                quoteSummaryEditMode = true
                imgEditQuoteSummary.setColorFilter(
                    if (quoteSummaryEditMode) ContextCompat.getColor(
                        activity as AppCompatActivity,
                        R.color.theme_green
                    ) else ContextCompat.getColor(
                        activity as AppCompatActivity,
                        android.R.color.black
                    )
                )
                layoutInnerQuoteSummary.visibility =
                    if (quoteSummaryEditMode) VISIBLE else GONE
                imgQuoteSummaryDropdown.rotation =
                    if (layoutInnerQuoteSummary.visibility == VISIBLE) 180f else 0f
                addProductEditMode = false
                businessInfoEditMode = false
                customerInfoEditMode = false

                expandAddProductAndEnableEdit()
                imgEditQuote.setColorFilter(
                    ContextCompat.getColor(
                        activity as AppCompatActivity,
                        R.color.theme_green
                    )
                )
                businessInfoEditMode = !businessInfoEditMode
                customerInfoEditMode = !customerInfoEditMode
                imgEditBusinessInfo.setColorFilter(
                    if (businessInfoEditMode)
                        ContextCompat.getColor(
                            activity as AppCompatActivity,
                            R.color.theme_green
                        ) else ContextCompat.getColor(
                        activity as AppCompatActivity,
                        android.R.color.black
                    )
                )
                imgEditCustomerInfo.setColorFilter(
                    if (businessInfoEditMode)
                        ContextCompat.getColor(
                            activity as AppCompatActivity,
                            R.color.theme_green
                        ) else ContextCompat.getColor(
                        activity as AppCompatActivity,
                        android.R.color.black
                    )
                )
            }

            Constants.VIEW_QUOTE -> {
                imgEditBusinessInfo.visibility = GONE
                imgEditCustomerInfo.visibility = GONE
                imgEditQuoteSummary.visibility = GONE
                layoutQuoteSummary.visibility = VISIBLE
                imgEditAddProduct.visibility = GONE
                layoutAddProducts.visibility = View.VISIBLE
                txtAddLogo.visibility = GONE
                txtImgName.visibility = GONE
//                txtNextAddProducts.visibility= GONE
                imgEditQuote.setColorFilter(
                    ContextCompat.getColor(
                        activity as AppCompatActivity,
                        android.R.color.black
                    )
                )
            }
        }

        getDeliveryDatesViewModel?.getDeliveryDates(sharedPrefs.userType) //Dev_N
        if (quoteDetailsResponse != null) {


            ivClose.visibility =
                if (quoteDetailsResponse!!.status.equals("Sent")) View.VISIBLE else GONE

            ivOpenPDF.visibility =
                if (quoteDetailsResponse!!.status.equals("Sent")) View.VISIBLE else GONE

            imgEditQuote.visibility =
                if (quoteDetailsResponse!!.status.equals("Sent")) GONE else VISIBLE

            txtCreateOrder.visibility =
                if (quoteDetailsResponse!!.status.equals("Sent")) View.VISIBLE else GONE

            if (quoteDetailsResponse?.products?.size == 0) {
                txtCreateOrder.visibility = View.GONE
            } else if (quoteDetailsResponse?.quoteID!!) {
                txtCreateOrder.visibility = View.GONE
            }

            txtSave.visibility =
                if (quoteDetailsResponse!!.status.equals("Sent")) GONE else VISIBLE

            txtCancel.visibility =
                if (quoteDetailsResponse!!.status.equals("Sent")) GONE else VISIBLE

            txtSave.text =
                if (quoteDetailsResponse!!.status.equals("Sent")) getString(R.string.back) else getString(
                    R.string.save
                )
            val params = viewUnderLable?.layoutParams as ConstraintLayout.LayoutParams
            if (quoteDetailsResponse!!.status == "Sent") {
                params.topToBottom = txtHeaderLabel.id
            } else {
                params.topToBottom = txtCancel.id
            }
            viewUnderLable.requestLayout()
            txtQuoteNumber.text =
                getString(R.string.quote_number, quoteDetailsResponse!!.id.toString())

            if (quoteDetailsResponse!!.users != null && quoteDetailsResponse!!.users?.business != null) {
                if (!Utility.isValueNull(quoteDetailsResponse!!.users?.business?.contactName))
                    edtContactName.setText(quoteDetailsResponse!!.users?.business?.contactName)

                if (!Utility.isValueNull(quoteDetailsResponse!!.users?.business?.businessName))
                    edtBusinessName.setText(quoteDetailsResponse!!.users?.business?.businessName)

                if (!Utility.isValueNull(quoteDetailsResponse!!.users?.business?.address))
                    edtAddress.setText(quoteDetailsResponse!!.users?.business?.address)

                if (!Utility.isValueNull(quoteDetailsResponse!!.users?.business?.email))
                    edtEmail.setText(quoteDetailsResponse!!.users?.business?.email)

                if (!Utility.isValueNull(quoteDetailsResponse!!.users?.business?.mobileNumber))
                    edtMobileNum.setText(quoteDetailsResponse!!.users?.business?.mobileNumber)

                if (!Utility.isValueNull(quoteDetailsResponse!!.users?.business?.phoneNumber))
                    edtPhone.setText(quoteDetailsResponse!!.users?.business?.phoneNumber)

                if (!Utility.isValueNull(quoteDetailsResponse!!.users?.business?.abn))
                    edtAbn.setText(quoteDetailsResponse!!.users?.business?.abn)

                if (!Utility.isValueNull(quoteDetailsResponse!!.users?.business?.webUrl))
                    edtWeb.setText(quoteDetailsResponse!!.users?.business?.webUrl)

                if (!Utility.isValueNull(quoteDetailsResponse!!.users?.business?.paymentTerms))
                    edtPaymentTerms.setText(quoteDetailsResponse!!.users?.business?.paymentTerms)

                if (!Utility.isValueNull(quoteDetailsResponse!!.users?.business?.disclaimer))
                    edtDisclaimer.setText(quoteDetailsResponse!!.users?.business?.disclaimer)

                if (!Utility.isValueNull(quoteDetailsResponse!!.users?.business?.logoUrl)) {
                    Glide.with(requireContext())
                        .load(BuildConfig.API_BASE_URL + quoteDetailsResponse!!.users?.business?.logoUrl)
                        .into(imgBusinessLogo)
                }
                imgCheckGST.setImageResource(
                    if (quoteDetailsResponse!!.users?.business?.registeredForGst.equals(
                            "1"
                        )
                    ) R.drawable.ic_checkbox_h else R.drawable.ic_checkbox
                )
            }
            if (quoteDetailsResponse?.customer != null) {
                edtCustomerName.setText(quoteDetailsResponse?.customer?.customerName)
                edtCustomerAddress.setText(quoteDetailsResponse?.customer?.customerAddress)
                edtEmailAddress.setText(quoteDetailsResponse?.customer?.customerEmail)
                edtCustomerMobileNum.setText(quoteDetailsResponse?.customer?.customerMobile)
                if (!Utility.isValueNull(quoteDetailsResponse?.customer?.customerPhone))
                    edtPhoneNum.setText(quoteDetailsResponse?.customer?.customerPhone)
            }
            if (quoteProducts == null) {
                quoteProducts = ArrayList()
                quoteProducts?.clear()
                if (quoteDetailsResponse?.products != null && quoteDetailsResponse?.products!!.size > 0) {
                    for (i in 0 until quoteDetailsResponse?.products!!.size) {
                        quoteProducts?.add(
                            QuoteProducts(
                                id = quoteDetailsResponse?.products!![i].id,
                                name = quoteDetailsResponse?.products!![i].name,
                                price = quoteDetailsResponse?.products!![i].price,
                                descriptions = "",
                                ancoProduct = true,
                                imageUrl = quoteDetailsResponse?.products!![i].featureImageUrl,
                                qty = quoteDetailsResponse?.products!![i].pivot.quantity,
                                unit = quoteDetailsResponse?.products!![i].productUnit,
                                margin = quoteDetailsResponse?.products!![i].pivot.margin
                            )
                        )
                    }
                }

                if (quoteDetailsResponse?.customProducts != null && quoteDetailsResponse?.customProducts!!.size > 0) {
                    for (i in 0 until quoteDetailsResponse?.customProducts!!.size) {
                        quoteProducts?.add(
                            QuoteProducts(
                                id = quoteDetailsResponse?.customProducts!![i].id,
                                name = quoteDetailsResponse?.customProducts!![i].name,
                                price = quoteDetailsResponse?.customProducts!![i].price,
                                descriptions = "",
                                ancoProduct = false,
                                imageUrl = quoteDetailsResponse?.customProducts!![i].imageUrl,
                                qty = quoteDetailsResponse?.customProducts!![i].pivot.quantity,
                                unit = "Quantity",
                                margin = 0
                            )
                        )
                    }
                }
            }
            edtDelivery.setText(quoteDetailsResponse?.deliveryCost.toString())
            edtDeliveryDate.setText(
                Utility.changeDateFormat(
                    quoteDetailsResponse?.deliveryDate,
                    Utility.DATE_FORMAT_YYYY_MM_DD,
                    Utility.DATE_FORMAT_D_MMMM_YYYY
                )
            )

            edtNote.setText(quoteDetailsResponse?.note)

            setProductAdapter()

            setEnableDisableBusinessInfo()
            setEnableDisableCustomerInfo()
            setEnableDisableQuoteSummary()
        }
    }

    private fun expandAddProductAndEnableEdit() {
        addProductEditMode = !addProductEditMode
        imgEditAddProduct.setColorFilter(
            if (addProductEditMode)
                ContextCompat.getColor(
                    activity as AppCompatActivity,
                    R.color.theme_green
                ) else ContextCompat.getColor(
                activity as AppCompatActivity,
                android.R.color.black
            )
        )
        if (addProductEditMode)
            layoutInnerAddProducts.visibility = VISIBLE
        if (layoutInnerAddProducts.visibility == VISIBLE)
            imgAddProductDropdown.rotation = 180f

    }

    private fun setEnableDisableBusinessInfo() {
        if (businessInfoEditMode) {
            edtContactName.isEnabled = true
            edtBusinessName.isEnabled = true
            edtAddress.isEnabled = true
            edtEmail.isEnabled = true
            edtMobileNum.isEnabled = true
            edtPhone.isEnabled = true
            edtAbn.isEnabled = true
            edtWeb.isEnabled = true
            edtPaymentTerms.isEnabled = true
            edtDisclaimer.isEnabled = true
            imgCheckGST.setOnClickListener(this)
            txtAddLogo.setOnClickListener(this)
        } else {
            edtContactName.isEnabled = false
            edtBusinessName.isEnabled = false
            edtAddress.isEnabled = false
            edtEmail.isEnabled = false
            edtMobileNum.isEnabled = false
            edtPhone.isEnabled = false
            edtAbn.isEnabled = false
            edtWeb.isEnabled = false
            edtPaymentTerms.isEnabled = false
            edtDisclaimer.isEnabled = false
            imgCheckGST.setOnClickListener(null)
            txtAddLogo.setOnClickListener(null)
        }
    }

    private fun setEnableDisableCustomerInfo() {
        if (customerInfoEditMode) {
            edtCustomerName.isEnabled = true
            edtCustomerAddress.isEnabled = true
            edtEmailAddress.isEnabled = true
            edtCustomerMobileNum.isEnabled = true
            edtPhoneNum.isEnabled = true
        } else {
            edtCustomerName.isEnabled = false
            edtCustomerAddress.isEnabled = false
            edtEmailAddress.isEnabled = false
            edtCustomerMobileNum.isEnabled = false
            edtPhoneNum.isEnabled = false
        }
    }

    private fun setEnableDisableQuoteSummary() {
        edtSubTotal.isEnabled = quoteSummaryEditMode
        edtDelivery.isEnabled = quoteSummaryEditMode
        edtDeliveryDate.isEnabled = quoteSummaryEditMode
        edtNote.isEnabled = quoteSummaryEditMode
        edtDeliveryDate.setOnClickListener(if (quoteSummaryEditMode) this else null)
        setProductAdapter()
    }

    private fun setProductAdapter() {
        totalQuotedPrice = 0.0f
        if (quoteProducts != null && quoteProducts!!.size > 0) {
            countTotalQuantity()
            ancoQuoteProductsAdapter = AncoQuoteProductsAdapter(
                requireActivity() as AppCompatActivity,
                quoteProducts!!,
                quoteSummaryEditMode,
                object :
                    OnProductChangeListener {
                    override fun onItemDelete(product: QuoteProducts) {
                        if (product.ancoProduct) {
                            if (isInProducts(product.id)) {
                                deletedProductIds.add(product.id)
                            }
                        } else {
                            if (isInCustomProducts(product.id)) {
                                deletedCustomProductIds.add(product.id)
                            }
                        }
                        quoteProducts?.remove(product)
                        setProductAdapter()
                    }

                    override fun onQuntityChange(product: QuoteProducts) {
                        if (isInQuotedProducts(product.id) != -1) {
                            quoteProducts?.set(isInQuotedProducts(product.id), product)
                            totalQuotedPrice = 0.0f
                            countTotalQuantity()
                        }
                    }
                })
            listQuoteProducts.adapter = ancoQuoteProductsAdapter
            layoutQuoteSummary.visibility = VISIBLE
        } else {
            layoutQuoteSummary.visibility = GONE
        }
    }

    private fun countTotalQuantity() {
        if (quoteProducts != null && quoteProducts!!.size > 0) {
            for (i in 0 until quoteProducts!!.size) {
                var qty = 0f
                if (!Utility.isValueNull(quoteProducts!![i].qty.toString()))
                    qty = quoteProducts!![i].qty!!.toFloat()
                if (Utility.isValueNull(quoteProducts!![i].margin.toString()))
                    totalQuotedPrice += (quoteProducts!![i].price.toFloat() * qty)
                else
                    totalQuotedPrice += ((quoteProducts!![i].price.toFloat() + (quoteProducts!![i].price.toFloat() * (quoteProducts!![i].margin.toFloat() / 100))) * qty)
            }
            edtSubTotal.setText("$" + Utility.formatNumber(Utility.roundTwoDecimals(totalQuotedPrice).toFloat()))
            if (Utility.isValueNull(edtDelivery.text.toString()) || Utility.isValueNull(
                    edtDelivery.text.toString().replace(
                        "$",
                        ""
                    ).replace(",", "")
                )
            ) {
                txtTotalCost.setText(
                    getString(
                        R.string.quote_total_cost,
                        Utility.formatNumber(
                            Utility.roundTwoDecimals(totalQuotedPrice).replace(
                                "$",
                                ""
                            ).replace(",", "").toFloat()
                        )
                    )
                )
            } else {
                txtTotalCost.setText(
                    getString(
                        R.string.quote_total_cost,
                        Utility.formatNumber(
                            (Utility.roundTwoDecimals(
                                totalQuotedPrice + edtDelivery.text.toString().replace(
                                    "$",
                                    ""
                                ).replace(",", "").toFloat()
                            ).toFloat())
                        )
                    )
                )
            }
        }
    }

    private fun isInProducts(productId: Int): Boolean {
        if (quoteDetailsResponse != null && quoteDetailsResponse!!.products != null && quoteDetailsResponse!!.products.size > 0) {
            for (i in 0 until quoteDetailsResponse!!.products.size) {
                if (quoteDetailsResponse!!.products[i].id == productId)
                    return true
            }
        }
        return false
    }

    private fun isInCustomProducts(productId: Int): Boolean {
        if (quoteDetailsResponse != null && quoteDetailsResponse!!.customProducts != null && quoteDetailsResponse!!.customProducts.size > 0) {
            for (i in 0 until quoteDetailsResponse!!.customProducts.size) {
                if (quoteDetailsResponse!!.customProducts[i].id == productId)
                    return true
            }
        }
        return false
    }

    private fun isInQuotedProducts(productId: Int): Int {
        if (quoteProducts != null && quoteProducts!!.size > 0) {
            for (i in 0 until quoteProducts!!.size) {
                if (quoteProducts!![i].id == productId)
                    return i
            }
        }
        return -1
    }

    var insertPos: Int = -1
    private fun initObservers() {
        //Dev_N
        getDeliveryDatesViewModel?.deliveryDatesLiveData?.observe(this, Observer {
            if (it != null) {
                var response: String = it
                response?.let {
                    var jsonObject: JSONObject = JSONObject(response)
                    var data: JSONObject = jsonObject.getJSONObject("data")
                    var gson: Gson = Gson()
                    deliveryDates =
                        gson.fromJson(data.toString(), DeliveryDatesResponseModel::class.java)
                }
            }
        })

        quoteViewModel!!.productCategoryLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null) {
                if (productCategories == null)
                    productCategories = ArrayList()
                productCategories.clear()
                productCategories.addAll(it)
                setProductCategoryAdapter()
            }
        })

        cartViewModel!!.productDeletionLiveData.observe(this, Observer {
            if (it != null) {
                cartViewModel!!._productDeletionLiveData.value = null
                sharedPrefs?.totalProductsInCart = 0
                Utility.showProgress(requireContext(), "", false)
                if (quoteDetailsResponse?.products != null && quoteDetailsResponse?.products!!.isNotEmpty()) {
                    insertPos = 0
                    getProductById()
                }
            }
        })

        cartViewModel!!.productLiveData.observe(this, Observer {

            Log.d("productLiveData>>", "${addToCartProduct?.productId}")

            if (addToCartProduct != null) {
                /*if (it != null) {
                    addToCartProduct!!.qty = addToCartProduct!!.qty + it.qty
                } else {
                    sharedPrefs.totalProductsInCart = sharedPrefs.totalProductsInCart + 1
                }*/
                sharedPrefs.totalProductsInCart = sharedPrefs.totalProductsInCart + 1
                cartViewModel!!.insertProduct(addToCartProduct!!, it != null)
                addToCartProduct = null

                insertPos = insertPos.plus(1)
                Log.d("AddEdit", "Position : ${insertPos}}")
                if (insertPos < quoteDetailsResponse?.products?.size!!) {
                    getProductById()
                } else {
                    insertPos == -1
                }
            }


            /*if (quoteDetailsResponse?.products != null && quoteDetailsResponse?.products!!.size > 0) {
                for (i in 0 until quoteDetailsResponse?.products!!.size) {

//                    if (it != null && quoteDetailsResponse?.products!![i].pivot.productId == it.product_id) {

                    val product = ProductDetailResponse(
                        id = quoteDetailsResponse?.products!![i].id,
                        productId = quoteDetailsResponse?.products!![i].pivot.productId,
                        productName = quoteDetailsResponse?.products!![i].name,
                        inStock = quoteDetailsResponse?.products!![i].inStock,
                        price = quoteDetailsResponse?.products!![i].price.toFloat(),
                        minimumQuantity = quoteDetailsResponse?.products!![i].minimumQuantity,
                        featureImageUrl = quoteDetailsResponse?.products!![i].featureImageUrl,
                        qty = quoteDetailsResponse?.products!![i].pivot.quantity,
                        productUnit = quoteDetailsResponse?.products!![i].productUnit,
                        productCategoryId = quoteDetailsResponse?.products!![i].productCategoryId,
                        productUnitId = quoteDetailsResponse?.products!![i].productUnitId
                    )

                    if (it != null) {
                        product!!.qty = product!!.qty + it.qty
                    } else {
                        sharedPrefs.totalProductsInCart = sharedPrefs.totalProductsInCart + 1
                    }


                    cartViewModel!!.insertProduct(product!!, it != null)
                }

//                }
            }*/

        })

        cartViewModel!!.productInsertionLiveData.observe(this, Observer {
            if (it != null && it) {
                Handler().postDelayed({
                    Utility.cancelProgress()
                    shortToast(getString(R.string.product_successfully_added_msg))
                    if (activity is HomeActivity) {
                        (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, false)
                        if (insertPos >= quoteDetailsResponse?.products?.size!! || insertPos == -1) {
                            var cartFragment = CartFragment()
                            var bundle = Bundle()
                            Logger.log(
                                " nnn Date : " + Utility.changeDateFormat(
                                    quoteDetailsResponse?.deliveryDate,
                                    Utility.DATE_FORMAT_YYYY_MM_DD,
                                    Utility.DATE_FORMAT_D_MMMM_YYYY
                                )
                            )
                            bundle.putString(
                                "deliveryDateOfQuote", Utility.changeDateFormat(
                                    quoteDetailsResponse?.deliveryDate,
                                    Utility.DATE_FORMAT_YYYY_MM_DD,
                                    Utility.DATE_FORMAT_D_MMMM_YYYY
                                )
                            )
                            bundle.putString("note", edtNote.text.toString())
                            cartFragment.arguments = bundle
                            //fragmentManager?.popBackStack()
                            (requireActivity() as AppCompatActivity).pushFragment(
                                cartFragment,
                                true,
                                true,
                                false,
                                R.id.flContainerHome
                            )
                        }
                    } else if (activity is PaymentActivity) {
                        (activity as PaymentActivity).showCartDetails(
                            imgCart,
                            txtCartProducts,
                            false
                        )
                    } else if (activity is ProfileActivity) {
                        (activity as ProfileActivity).showCartDetails(
                            imgCart,
                            txtCartProducts,
                            false
                        )
                    }
                    cartViewModel!!._productInsertionLiveData.value = null
                }, 1000)
            }
        })

        quoteViewModel!!.quoteDetailsLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null) {
                quoteDetailsResponse = it
                setData()
                quoteViewModel!!._quoteDetailsLiveData.value = null
            }
        })

        quoteViewModel!!.productsLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null) {
                if (products == null || pageNo == 1)
                    products = ArrayList()
                products.addAll(it.data as ArrayList<ProductsResponse.Data>)
                openAddAncoProductDialog()
                quoteViewModel!!._productsLiveData.value = null
            }
        })

        quoteViewModel!!.addEditQuotesLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null) {
                shortToast(it.message)
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
                quoteViewModel!!._addEditQuotesLiveData.value = null
                if (dialogSendQuote != null && dialogSendQuote!!.isShowing)
                    dialogSendQuote?.dismiss()
            }
        })

        quoteViewModel!!.customProductLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null && it.data != null) {
                shortToast(it.message)
                quoteProducts?.add(
                    QuoteProducts(
                        id = it.data!!.id,
                        unit = "Quantity",
                        qty = edtProductQuantity.text.toString().toInt(),
                        imageUrl = it.data!!.imageUrl,
                        ancoProduct = false,
                        descriptions = it.data!!.descriptions,
                        price = it.data!!.price,
                        name = it.data!!.name,
                        userId = it.data!!.userId,
                        margin = 0
                    )
                )

//                layoutInnerQuoteSummary.visibility = VISIBLE
                setProductAdapter()
                if (dialogAddCustomProduct != null && dialogAddCustomProduct!!.isShowing)
                    dialogAddCustomProduct?.dismiss()
                quoteViewModel!!._customProductLiveData.value = null
            }
        })

        quoteViewModel!!.errorLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null) {
                shortToast(it)
                quoteViewModel?._errorLiveData?.value = null
            }
        })
    }

    private fun getProductById() {
        val product = ProductDetailResponse(
            id = quoteDetailsResponse?.products!![insertPos].id,
            productId = quoteDetailsResponse?.products!![insertPos].pivot.productId,
            productName = quoteDetailsResponse?.products!![insertPos].name,
            inStock = quoteDetailsResponse?.products!![insertPos].inStock,
            price = quoteDetailsResponse?.products!![insertPos].price.toFloat(),
            minimumQuantity = quoteDetailsResponse?.products!![insertPos].minimumQuantity,
            featureImageUrl = quoteDetailsResponse?.products!![insertPos].featureImageUrl,
            qty = quoteDetailsResponse?.products!![insertPos].pivot.quantity,
            productUnit = quoteDetailsResponse?.products!![insertPos].productUnit,
            productCategoryId = quoteDetailsResponse?.products!![insertPos].productCategoryId,
            productUnitId = quoteDetailsResponse?.products!![insertPos].productUnitId
        )

        Utility.showProgress(requireContext(), "", false)
        addToCartProduct = product
        Log.d("getProductById>>", "${addToCartProduct?.productId}")
        cartViewModel?.getProductById(product.id)
    }

    private fun setProductCategoryAdapter() {
        if (productCategories != null && productCategories.size > 0) {
            var categoryAdapter = ProductCategoryAdapter(
                requireActivity() as AppCompatActivity,
                productCategories,
                null,
                object : OnProductCategorySelected {
                    override fun onProductCategorySelected(productCategoryData: ProductCategoryData) {
                        if (addProductEditMode) {
                            if (addProductEditMode) {
                                quoteViewModel?.callGetProducts(
                                    productCategoryData.id.toString(),
                                    "" + "1"
                                )
                                productCategoryName = productCategoryData.displayName
                            }
                        }
                    }
                }
            )
            listProducts.adapter = categoryAdapter
        }
    }

    private fun openAddAncoProductDialog() {

        if (!(dialogAddAncoProducts != null && dialogAddAncoProducts!!.isShowing)) {
            dialogAddAncoProducts = Dialog(requireContext())
            dialogAddAncoProducts?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogAddAncoProducts?.setContentView(R.layout.dialog_product_list)
            dialogAddAncoProducts?.show()
            val window = dialogAddAncoProducts?.window
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            window?.setGravity(Gravity.CENTER)
            pageNo = 1
            isLoad = false
            itemCount = 0
        }


        var selectedProduct: ProductsResponse.Data = products[0]
        products[0].selected = true
        val txtProductSubTitle =
            dialogAddAncoProducts?.findViewById(R.id.txtProductSubTitle) as TextView
        txtProductSubTitle.text = productCategoryName
        val txtProductQuantityLabel =
            dialogAddAncoProducts?.findViewById(R.id.txtProductQuantityLabel) as TextView
        /*txtProductQuantityLabel.text =
//            getString(R.string.product_quantity_label, productCategoryName.toLowerCase())
            getString(R.string.what_quantity_of_products_whould_you_like, productCategoryName.toLowerCase())*/

        when(productCategoryName){
            "Weeds, Disease & Pest" ->{
                txtProductQuantityLabel.text = "What quantity of weed, disease or pest control would you like?"
            }
            "Fertilisers" ->{
                txtProductQuantityLabel.text = "What quantity of fertiliser products would you like?"
            }
            "Lawn Care" ->{
                txtProductQuantityLabel.text = "What quantity of lawn care products would you like?"
            } "Seed" ->{
                txtProductQuantityLabel.text = "What quantity of seed products would you like?"
            }
            "Turf" ->{
                txtProductQuantityLabel.text = "What quantity of turf would you like?"
            }
            "Books" ->{
                txtProductQuantityLabel.text = "What quantity of books would you like?"
            }
            "Colour Guard" ->{
                txtProductQuantityLabel.text = "What quantity of ColourGuard products would you like?"
            }
            "Tools and Equipment" ->{
                txtProductQuantityLabel.text = "What quantity of tools and equipment products would you like?"
            }
            else ->{
                txtProductQuantityLabel.text = "What quantity of " + productCategoryName  + " products would you like?"
            }
        }



        val txtChoosingLabel =
            dialogAddAncoProducts?.findViewById(R.id.txtChoosingLabel) as TextView
        txtChoosingLabel.text =
            getString(R.string.choosing_product_label, productCategoryName.toLowerCase())
        val txtUnit = dialogAddAncoProducts?.findViewById(R.id.txtUnit) as TextView
        val edtQuantity = dialogAddAncoProducts?.findViewById(R.id.edtQuantity) as EditText
        edtQuantity.filters = arrayOf<InputFilter>(InputFilterMinMax(0, Constants.MAX_NUMBER))
//        edtQuantity.setText("")
//        edtQuantity.isEnabled = false
        edtQuantity.isEnabled = true
        var unit = selectedProduct.productUnit
        if (unit.equals("Square meter"))
            unit = "m\u00B2"
        else if (unit.equals("Quantity"))
            unit = "Qty"
        txtUnit.text = unit
        val listProducts = dialogAddAncoProducts?.findViewById(R.id.listProducts) as RecyclerView
        val nestedScroll =
            dialogAddAncoProducts?.findViewById(R.id.nestedScroll) as NestedScrollView
        val edtMargin = dialogAddAncoProducts?.findViewById(R.id.edtMargin) as EditText
        edtMargin.setText("")
//        edtMargin.isEnabled = false
        edtMargin.setFilters(arrayOf<InputFilter>(InputFilterMinMax(0, Constants.MAX_NUMBER)))
        val txtSingleUnitPriceLabel =
            dialogAddAncoProducts?.findViewById(R.id.txtSingleUnitPriceLabel) as TextView
        txtSingleUnitPriceLabel.text = getString(R.string.quoted_final_price, "m²")
        val txtSingleUnitPrice =
            dialogAddAncoProducts?.findViewById(R.id.txtSingleUnitPrice) as TextView
        val txtTotalPriceLabel =
            dialogAddAncoProducts?.findViewById(R.id.txtTotalPriceLabel) as TextView
        val txtTotalPrice = dialogAddAncoProducts?.findViewById(R.id.txtTotalPrice) as TextView
        val txtFinalPriceLabel =
            dialogAddAncoProducts?.findViewById(R.id.txtFinalPriceLabel) as TextView
        val txtFinalPrice = dialogAddAncoProducts?.findViewById(R.id.txtFinalPrice) as TextView
        txtFinalPrice.setText("$" + Utility.formatNumber(Utility.roundTwoDecimals(totalQuotedPrice).toFloat()))

        if (!Utility.isValueNull(edtQuantity.text.toString()) && selectedProduct != null) {
            selectedProduct?.qty = edtQuantity.text.toString().toInt()
            if (!Utility.isValueNull(edtMargin.text.toString())) {
                txtSingleUnitPrice.text =
                    "$" + (Utility.formatNumber(Utility.roundTwoDecimals(selectedProduct!!.price + (selectedProduct!!.price * (edtMargin.text.toString().toFloat() / 100f))).toFloat()))
                txtTotalPrice.text =
                    "$" + Utility.formatNumber(Utility.roundTwoDecimals((selectedProduct!!.price + (selectedProduct!!.price * (edtMargin.text.toString().toFloat() / 100f))) * selectedProduct!!.qty).toFloat())
                txtFinalPrice.text =
                    "$" + Utility.formatNumber(Utility.roundTwoDecimals(totalQuotedPrice + ((selectedProduct!!.price + (selectedProduct!!.price * (edtMargin.text.toString().toFloat() / 100f))) * selectedProduct!!.qty)).toFloat())
            } else {
                txtSingleUnitPrice.text =
                    "$" + Utility.formatNumber((Utility.roundTwoDecimals(selectedProduct!!.price)).toFloat())
                txtTotalPrice.text =
                    "$" + Utility.formatNumber(Utility.roundTwoDecimals(selectedProduct!!.price * selectedProduct!!.qty).toFloat())
                txtFinalPrice.text =
                    "$" + Utility.formatNumber(Utility.roundTwoDecimals(totalQuotedPrice + (selectedProduct!!.price * selectedProduct!!.qty)).toFloat())
            }
        } else {
            txtSingleUnitPrice.text = "$0.00"
            txtTotalPrice.text = "$0.00"
            txtFinalPrice.text =
                "$${Utility.formatNumber(Utility.roundTwoDecimals(totalQuotedPrice).toFloat())}"
        }

        products = removeSelectedItems(products)


        var quoteProductsAdapter =
            QuoteProductsAdapter(
                requireActivity() as AppCompatActivity,
                products,
                object : OnProductSelectedListener {
                    override fun onProductSelectedListener(
                        product: ProductsResponse.Data,
                        selected: Boolean
                    ) {
                        if (selected) {
                            edtQuantity.requestFocus()
                            selectedProduct = product
                            edtQuantity.isEnabled = true
                            edtMargin.isEnabled = true
                            var unit = product.productUnit
                            if (unit.equals("Square meter"))
                                unit = "m\u00B2"
                            else if (unit.equals("Quantity"))
                                unit = "Qty"
                            txtUnit.text = unit
                            txtSingleUnitPriceLabel.text =
                                getString(R.string.quoted_final_price, unit)
                            if (!Utility.isValueNull(edtQuantity.text.toString()) && selectedProduct != null) {
                                selectedProduct?.qty = edtQuantity.text.toString().toInt()
                                if (!Utility.isValueNull(edtMargin.text.toString())) {
                                    txtSingleUnitPrice.text =
                                        "$" + (Utility.formatNumber(
                                            Utility.roundTwoDecimals(
                                                selectedProduct!!.price + (selectedProduct!!.price * (edtMargin.text.toString().toFloat() / 100f))
                                            ).toFloat()
                                        ))
                                    txtTotalPrice.text =
                                        "$" + Utility.formatNumber(Utility.roundTwoDecimals((selectedProduct!!.price + (selectedProduct!!.price * (edtMargin.text.toString().toFloat() / 100f))) * selectedProduct!!.qty).toFloat())
                                    txtFinalPrice.text =
                                        "$" + Utility.formatNumber(
                                            Utility.roundTwoDecimals(
                                                totalQuotedPrice + ((selectedProduct!!.price + (selectedProduct!!.price * (edtMargin.text.toString().toFloat() / 100f))) * selectedProduct!!.qty)
                                            ).toFloat()
                                        )
                                } else {
                                    txtSingleUnitPrice.text = "$" + Utility.formatNumber(
                                        (Utility.roundTwoDecimals(selectedProduct!!.price)).toFloat()
                                    )
                                    txtTotalPrice.text =
                                        "$" + Utility.formatNumber(
                                            Utility.roundTwoDecimals(
                                                selectedProduct!!.price * selectedProduct!!.qty
                                            ).toFloat()
                                        )
                                    txtFinalPrice.text =
                                        "$" + Utility.formatNumber(
                                            Utility.roundTwoDecimals(
                                                totalQuotedPrice + (selectedProduct!!.price * selectedProduct!!.qty)
                                            ).toFloat()
                                        )
                                }
                            } else {
                                txtSingleUnitPrice.text = "$0.00"
                                txtTotalPrice.text = "$0.00"
                                txtFinalPrice.text = "$${Utility.formatNumber(
                                    Utility.roundTwoDecimals(totalQuotedPrice).toFloat()
                                )}"
                            }
                        } else {
                            edtQuantity.setText("")
                            edtQuantity.isEnabled = false
                            edtMargin.setText("")
                            edtMargin.isEnabled = false
                            txtUnit.text = "m\u00B2"
                            txtSingleUnitPriceLabel.text =
                                getString(R.string.quoted_final_price, "m\u00B2")
                            txtSingleUnitPrice.text = "$0.00"
                            txtTotalPrice.text = "$0.00"
                            txtFinalPrice.text =
                                "$" + Utility.formatNumber(Utility.roundTwoDecimals(totalQuotedPrice).toFloat())
                        }
                    }
                })
        listProducts.adapter = quoteProductsAdapter

        nestedScroll.viewTreeObserver.addOnScrollChangedListener {
            val linearLayoutManager = listProducts.layoutManager as LinearLayoutManager?
            if (!isLoad) {
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == products.size - 1) {
                    //bottom of list!
                    val view: View = nestedScroll.getChildAt(nestedScroll.getChildCount() - 1)
                    var diff = (view.bottom - (nestedScroll.height + nestedScroll.scrollY))
                    if (diff == 0) {
                        pageNo++
                        if (quoteViewModel?._isNextPageUrlForProducts?.value != null)
                            if (quoteViewModel?._isNextPageUrlForProducts?.value!!)
                                quoteViewModel?.callGetProducts(productCategoryName, "" + pageNo)!!

                        Handler().postDelayed({
                            isLoad = !quoteViewModel!!._isNextPageUrlForProducts.value!!
                        }, 500)

                    }
                }
            }
        }

        if (pageNo > 1) {
            listProducts.scrollToPosition(products.size - 1)
            listProducts.post {
                //(10 * (pageNo - 1)) - 1 = last position of last loaded page where per page item count is 10
                listProducts.getChildAt((10 * (pageNo - 1)) - 1).post {
                    nestedScroll.smoothScrollTo(
                        0,
                        listProducts.getChildAt((10 * (pageNo - 1)) - 1).bottom
                    )
                }
            }
        }

        edtQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!Utility.isValueNull(edtQuantity.text.toString()) && selectedProduct != null) {
                    selectedProduct?.qty = edtQuantity.text.toString().toInt()
                    if (!Utility.isValueNull(edtMargin.text.toString())) {
                        txtSingleUnitPrice.text =
                            "$" + Utility.formatNumber(Utility.roundTwoDecimals(selectedProduct!!.price + (selectedProduct!!.price * (edtMargin.text.toString().toFloat() / 100f))).toFloat())
                        txtTotalPrice.text =
                            "$" + Utility.formatNumber(Utility.roundTwoDecimals((selectedProduct!!.price + (selectedProduct!!.price * (edtMargin.text.toString().toFloat() / 100f))) * selectedProduct!!.qty).toFloat())
                        txtFinalPrice.text =
                            "$" + Utility.formatNumber(Utility.roundTwoDecimals(totalQuotedPrice + ((selectedProduct!!.price + (selectedProduct!!.price * (edtMargin.text.toString().toFloat() / 100f))) * selectedProduct!!.qty)).toFloat())
                    } else {
                        txtSingleUnitPrice.text =
                            "$" + Utility.formatNumber((Utility.roundTwoDecimals(selectedProduct!!.price)).toFloat())
                        txtTotalPrice.text =
                            "$" + Utility.formatNumber(Utility.roundTwoDecimals(selectedProduct!!.price * selectedProduct!!.qty).toFloat())
                        txtFinalPrice.text =
                            "$" + Utility.formatNumber(Utility.roundTwoDecimals(totalQuotedPrice + (selectedProduct!!.price * selectedProduct!!.qty)).toFloat())
                    }
                } else {
                    txtSingleUnitPrice.text = "$0.00"
                    txtTotalPrice.text = "$0.00"
                    txtFinalPrice.text =
                        "$" + Utility.formatNumber(Utility.roundTwoDecimals(totalQuotedPrice).toFloat())
                }
            }
        })
        edtMargin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!Utility.isValueNull(edtQuantity.text.toString()) && selectedProduct != null) {
                    selectedProduct?.qty = edtQuantity.text.toString().toInt()
                    if (!Utility.isValueNull(edtMargin.text.toString())) {
                        txtSingleUnitPrice.text =
                            "$" + Utility.formatNumber(Utility.roundTwoDecimals(selectedProduct!!.price + (selectedProduct!!.price * (edtMargin.text.toString().toFloat() / 100f))).toFloat())
                        txtTotalPrice.text =
                            "$" + Utility.formatNumber(Utility.roundTwoDecimals((selectedProduct!!.price + (selectedProduct!!.price * (edtMargin.text.toString().toFloat() / 100f))) * selectedProduct!!.qty).toFloat())
                        txtFinalPrice.text =
                            "$" + Utility.formatNumber(Utility.roundTwoDecimals(totalQuotedPrice + ((selectedProduct!!.price + (selectedProduct!!.price * (edtMargin.text.toString().toFloat() / 100f))) * selectedProduct!!.qty)).toFloat())
                    } else {
                        txtSingleUnitPrice.text =
                            "$" + Utility.formatNumber((Utility.roundTwoDecimals(selectedProduct!!.price)).toFloat())
                        txtTotalPrice.text =
                            "$" + Utility.formatNumber(Utility.roundTwoDecimals(selectedProduct!!.price * selectedProduct!!.qty).toFloat())
                        txtFinalPrice.text =
                            "$" + Utility.formatNumber(Utility.roundTwoDecimals(totalQuotedPrice + (selectedProduct!!.price * selectedProduct!!.qty)).toFloat())
                    }
                } else {
                    txtSingleUnitPrice.text = "$0.00"
                    txtTotalPrice.text = "$0.00"
                    txtFinalPrice.text =
                        "$" + Utility.formatNumber(Utility.roundTwoDecimals(totalQuotedPrice).toFloat())
                }
            }
        })

        val imgClose = dialogAddAncoProducts?.findViewById(R.id.imgClose) as ImageView
        imgClose.setOnClickListener {
            dialogAddAncoProducts?.dismiss()
        }

        val txtCancel = dialogAddAncoProducts?.findViewById(R.id.txtCancel) as TextView
        txtCancel.setOnClickListener {
            dialogAddAncoProducts?.dismiss()
        }

        val txtAddProduct = dialogAddAncoProducts?.findViewById(R.id.txtAddProduct) as TextView
        txtAddProduct.setOnClickListener {
            if (selectedProduct == null) {
                shortToast(getString(R.string.please_select_a_product))
            } else if (Utility.isValueNull(edtQuantity.text.toString())) {
                shortToast("Please add quantity")
            } else if (edtQuantity.text.toString().toInt() <= 0) {
                shortToast("The product quantity needs to be greater than 0")
            } else if (Utility.isValueNull(edtMargin.text.toString())) {
                shortToast(getString(R.string.blank_margin_msg))
            } else if (edtMargin.text.toString().toInt() <= 0) {
                shortToast(getString(R.string.invalid_margin_msg))
            } else {
                dialogAddAncoProducts?.dismiss()
                var price = Utility.roundTwoDecimals(selectedProduct!!.price)
                if (!Utility.isValueNull(edtMargin.text.toString())) {
                    price =
                        Utility.roundTwoDecimals(selectedProduct!!.price + (selectedProduct!!.price * (edtMargin.text.toString().toFloat() / 100f)))
                }
                quoteProducts?.add(
                    QuoteProducts(
                        id = selectedProduct!!.productId,
                        unit = selectedProduct!!.productUnit,
                        qty = edtQuantity.text.toString().toInt(),
                        imageUrl = selectedProduct!!.featureImageUrl,
                        ancoProduct = true,
                        descriptions = "",
                        price = price,
                        name = selectedProduct!!.productName,
                        userId = sharedPrefs.userId,
                        margin = edtMargin.text.toString().toInt()
                    )
                )
//                expandAddProductAndEnableEdit()
                /*layoutInnerQuoteSummary.visibility = VISIBLE
                layoutInnerAddProducts.visibility =
                    if (layoutInnerAddProducts.visibility == VISIBLE) GONE else VISIBLE
                imgAddProductDropdown.rotation =
                    if (layoutInnerAddProducts.visibility == VISIBLE) 180f else 0f*/

                setProductAdapter()
            }
        }
        dialogAddAncoProducts?.setOnDismissListener {
            products.clear()
        }
    }

    private fun openSendQuoteDialog() {
        dialogSendQuote = Dialog(requireContext())
        dialogSendQuote?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogSendQuote?.setContentView(R.layout.dialog_send_quote)
        dialogSendQuote?.show()

        val window = dialogSendQuote?.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)

        val txtUserEmail = dialogSendQuote?.findViewById(R.id.txtUserEmail) as EditText
        txtUserEmail.setText(sharedPrefs.userEmail)
        val txtConfirm = dialogSendQuote?.findViewById(R.id.txtConfirm) as TextView
        txtConfirm.setOnClickListener {
            if (Utility.isValueNull(txtUserEmail.text.toString()))
                shortToast(getString(R.string.blank_email_message))
            else
                confirmQuote(true, txtUserEmail.text.toString())
        }

        val imgClose = dialogSendQuote?.findViewById(R.id.imgClose) as ImageView
        imgClose.setOnClickListener {
            dialogSendQuote?.dismiss()
        }

        val txtCancel = dialogSendQuote?.findViewById(R.id.txtCancel) as TextView
        txtCancel.setOnClickListener {
            dialogSendQuote?.dismiss()
        }
    }

    private fun removeSelectedItems(products: java.util.ArrayList<ProductsResponse.Data>): java.util.ArrayList<ProductsResponse.Data> {
        var productArray = products

        if (quoteProducts != null && quoteProducts!!.size > 0) {
            for (i in 0 until quoteProducts!!.size) {
                var selctedProductID = quoteProducts!![i].id
                for (j in 0 until products.size) {
                    if (products[j].productId == selctedProductID) {
                        productArray.remove(products[j])
                        break
                    }
                }
            }
        }
        return productArray
    }


    private fun openAddNonAncoProductDialog() {

        var addImage = canAddImage()

        if (!(dialogAddCustomProduct != null && dialogAddCustomProduct!!.isShowing)) {
            dialogAddCustomProduct = Dialog(requireContext())
            dialogAddCustomProduct?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogAddCustomProduct?.setContentView(R.layout.dialog_add_non_anco_product_quote)
            dialogAddCustomProduct?.show()

            val window = dialogAddCustomProduct?.window
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            window?.setGravity(Gravity.CENTER)
        }

        val edtProductName = dialogAddCustomProduct?.findViewById(R.id.edtProductName) as EditText
        val edtProductDescription =
            dialogAddCustomProduct?.findViewById(R.id.edtProductDescription) as EditText
        edtProductQuantity =
            dialogAddCustomProduct?.findViewById(R.id.edtProductQuantity) as EditText
        edtProductQuantity.setFilters(
            arrayOf<InputFilter>(
                InputFilterMinMax(
                    0, Constants.MAX_NUMBER
                )
            )
        )

        val edtProductPrice = dialogAddCustomProduct?.findViewById(R.id.edtProductPrice) as EditText
//        edtProductPrice.setFilters(
//            arrayOf<InputFilter>(
//                InputFilterMinMax(
//                    0, Constants.MAX_NUMBER
//                )
//            )
//        )

        edtProductPrice.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        edtProductPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!Utility.isValueNull(s.toString()) && !Utility.isValueNull(
                        s.toString().replace(
                            "$",
                            ""
                        ).replace(",", "")
                    ) && before != count
                )
                    edtProductPrice.setText(
                        "$" + (Utility.formatNumber(
                            s.toString().replace(
                                "$",
                                ""
                            ).replace(",", "").toLong()
                        ))
                    )
                edtProductPrice.text?.length?.let { edtProductPrice.setSelection(it) }
            }
        })

        val layoutAddImage =
            dialogAddCustomProduct?.findViewById(R.id.layoutAddImage) as ConstraintLayout
        if (addImage)
            layoutAddImage.visibility = VISIBLE
        else
            layoutAddImage.visibility = GONE

        imgProductLogo = dialogAddCustomProduct?.findViewById(R.id.imgLogo) as ImageView
        imgProductLogo.setOnClickListener {
            openImageCaptureDialog(
                REQUEST_GET_IMAGE_GALLERY_FOR_PRODUCT,
                REQUEST_GET_IMAGE_CAMERA_FOR_PRODUCT
            )
        }

        txtAddProductLogo = dialogAddCustomProduct?.findViewById(R.id.txtAddLogo) as TextView
        txtAddProductLogo.setOnClickListener {
            openImageCaptureDialog(
                REQUEST_GET_IMAGE_GALLERY_FOR_PRODUCT,
                REQUEST_GET_IMAGE_CAMERA_FOR_PRODUCT
            )
        }

        imgDeleteImage = dialogAddCustomProduct?.findViewById(R.id.imgDeleteImage) as ImageView
        imgDeleteImage.setOnClickListener {
            imgProductLogo.setImageResource(R.drawable.img_place_holder)
            txtAddProductLogo.visibility = VISIBLE
            imgDeleteImage.visibility = GONE
            selectedPhotoUriForProduct = null
            selectedFilePathForProduct = ""
        }

        if (!Utility.isValueNull(selectedFilePathForBusiness)) {
            Glide.with(requireContext()).load(selectedFilePathForProduct).into(imgProductLogo)
            txtAddProductLogo.visibility = GONE
            imgDeleteImage.visibility = VISIBLE
        } else {
            imgProductLogo.setImageResource(R.drawable.img_place_holder)
            txtAddProductLogo.visibility = VISIBLE
            imgDeleteImage.visibility = GONE
        }

        val imgClose = dialogAddCustomProduct?.findViewById(R.id.imgClose) as ImageView
        imgClose.setOnClickListener {
            dialogAddCustomProduct?.dismiss()
            imgProductLogo.setImageResource(R.drawable.img_place_holder)
            txtAddProductLogo.visibility = VISIBLE
            imgDeleteImage.visibility = GONE
            selectedPhotoUriForProduct = null
            selectedFilePathForProduct = ""
        }

        val txtCancel = dialogAddCustomProduct?.findViewById(R.id.txtCancel) as TextView
        txtCancel.setOnClickListener {
            dialogAddCustomProduct?.dismiss()
        }

        val txtAddProduct = dialogAddCustomProduct?.findViewById(R.id.txtAddProduct) as TextView
        txtAddProduct.setOnClickListener {
            if (Utility.isValueNull(edtProductName.text.toString())) {
                shortToast(getString(R.string.black_product_name_message))
            } else if (Utility.isValueNull(edtProductDescription.text.toString())) {
                shortToast(getString(R.string.black_product_description_message))
            } else if (Utility.isValueNull(edtProductQuantity.text.toString())) {
                shortToast(getString(R.string.black_product_quantity_message))
            } else if (edtProductQuantity.text.toString().toInt() <= 0) {
                shortToast(getString(R.string.invalid_product_quantity_message))
            } else if (Utility.isValueNull(edtProductPrice.text.toString())) {
                shortToast(getString(R.string.black_product_price_message))
            } else if (edtProductPrice.text.toString().replace("$", "").replace(
                    ",",
                    ""
                ).toFloat() <= 0
            ) {
                shortToast(getString(R.string.invalid_product_price_message))
            } else {
                Utility.showProgress(requireContext(), "", false)
                quoteViewModel?.callAddCustomProduct(
                    customProductId = 0,
                    name = edtProductName.text.toString(),
                    descriptions = edtProductDescription.text.toString(),
                    price = edtProductPrice.text.toString().replace("$", "").replace(",", ""),
                    imagePath = selectedFilePathForProduct
                )
                selectedFilePathForProduct = null
            }
        }
    }

    private fun canAddImage(): Boolean {
        var totalProductsWithImage = 0
        if (quoteProducts != null && quoteProducts!!.size > 0) {
            for (i in 0 until quoteProducts!!.size) {
                if (!quoteProducts!![i].ancoProduct && !Utility.isValueNull(quoteProducts!![i].imageUrl)) {
                    totalProductsWithImage += 1
                }
            }
        }
        return totalProductsWithImage < sharedPrefs.max_images_non_anco_products_quote
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {
            /*  R.id.txtNextAddProducts ->{
                  if (quoteProducts != null && quoteProducts!!.size > 0) {
                      layoutQuoteSummary.visibility = VISIBLE
                  }else{
                      layoutQuoteSummary.visibility = GONE
                  }
              }*/
            R.id.ivOpenPDF -> {
                if (quoteDetailsResponse!!.quotePDFUrl != "") {
                    val bundle = Bundle()
                    bundle.putBoolean("isQuote", true)
                    (requireActivity() as AppCompatActivity).pushFragment(
                        QuotePDFFragment(quoteDetailsResponse!!.quotePDFUrl).apply { arguments = bundle  },
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                }
            }
            R.id.imgBell -> {
                openNotificationFragment()
            }
            R.id.imgBack, R.id.ivClose -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }

            R.id.imgCheckGST -> {
                quoteDetailsResponse!!.users?.business?.registeredForGst =
                    if (quoteDetailsResponse!!.users?.business?.registeredForGst.equals("1")) "0" else "1"
                imgCheckGST.setImageResource(
                    if (quoteDetailsResponse!!.users?.business?.registeredForGst.equals(
                            "1"
                        )
                    ) R.drawable.ic_checkbox_h else R.drawable.ic_checkbox
                )
            }

            R.id.edtDeliveryDate -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                openDatePickerDialog()
            }

            R.id.txtAddLogo -> {
                openImageCaptureDialog(
                    REQUEST_GET_IMAGE_GALLERY_FOR_BUSINESS,
                    REQUEST_GET_IMAGE_CAMERA_FOR_BUSINESS
                )
            }

            R.id.txtAddNonAncoProduct -> {
                openAddNonAncoProductDialog()
            }

            R.id.txtConfirmQuote -> {
                openSendQuoteDialog()
            }

            R.id.txtSave -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                if (quoteDetailsResponse!!.status.equals("Sent")) requireActivity().supportFragmentManager.popBackStack()
                else

                    confirmQuote(
                        false,
                        ""
                    )
            }

            R.id.txtCancel -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }

            R.id.imgBusinessDropdown -> {
                layoutInnerBusinessInfo.visibility =
                    if (layoutInnerBusinessInfo.visibility == VISIBLE) GONE else VISIBLE
                imgBusinessDropdown.rotation =
                    if (layoutInnerBusinessInfo.visibility == VISIBLE) 180f else 0f
            }

            R.id.imgCustomerDropdown -> {
                layoutInnerCustomerInfo.visibility =
                    if (layoutInnerCustomerInfo.visibility == VISIBLE) GONE else VISIBLE
                imgCustomerDropdown.rotation =
                    if (layoutInnerCustomerInfo.visibility == VISIBLE) 180f else 0f
            }

            R.id.imgQuoteSummaryDropdown -> {
                layoutInnerQuoteSummary.visibility =
                    if (layoutInnerQuoteSummary.visibility == VISIBLE) GONE else VISIBLE
                imgQuoteSummaryDropdown.rotation =
                    if (layoutInnerQuoteSummary.visibility == VISIBLE) 180f else 0f
            }

            R.id.imgAddProductDropdown -> {
                layoutInnerAddProducts.visibility =
                    if (layoutInnerAddProducts.visibility == VISIBLE) GONE else VISIBLE
                imgAddProductDropdown.rotation =
                    if (layoutInnerAddProducts.visibility == VISIBLE) 180f else 0f
            }

            R.id.imgEditQuote -> {
                if (quoteMode == Constants.VIEW_QUOTE) {
                    imgEditQuote.setColorFilter(
                        ContextCompat.getColor(
                            activity as AppCompatActivity,
                            R.color.theme_green
                        )
                    )
                    quoteMode = Constants.EDIT_QUOTE
                    imgEditBusinessInfo.visibility = VISIBLE
                    imgEditCustomerInfo.visibility = VISIBLE
                    imgEditQuoteSummary.visibility = VISIBLE
                    imgEditAddProduct.visibility = VISIBLE
                    layoutAddProducts.visibility = VISIBLE

                    expandAddProductAndEnableEdit()

                    arguments?.let {
                        if (it.getInt("quoteMode") == Constants.DRAFT_QUOTE) {
                            quoteSummaryEditMode = true;
                            imgEditQuoteSummary.setColorFilter(
                                if (quoteSummaryEditMode)
                                    ContextCompat.getColor(
                                        activity as AppCompatActivity,
                                        R.color.theme_green
                                    ) else ContextCompat.getColor(
                                    activity as AppCompatActivity,
                                    android.R.color.black
                                )
                            )
                        }
                    }
                } else if (quoteMode == Constants.EDIT_QUOTE) {
                    imgEditQuote.setColorFilter(
                        ContextCompat.getColor(
                            activity as AppCompatActivity,
                            android.R.color.black
                        )
                    )
                    quoteMode = Constants.VIEW_QUOTE
                    imgEditBusinessInfo.visibility = GONE
                    imgEditCustomerInfo.visibility = GONE
                    imgEditQuoteSummary.visibility = GONE
                    imgEditAddProduct.visibility = GONE
                    imgEditQuote.visibility = GONE
                    businessInfoEditMode = false
                    customerInfoEditMode = false
                    quoteSummaryEditMode = false
                    addProductEditMode = false
                } else if (quoteMode == Constants.DRAFT_QUOTE) {
                    imgEditQuote.setColorFilter(
                        ContextCompat.getColor(
                            activity as AppCompatActivity,
                            android.R.color.black
                        )
                    )
                    quoteMode = Constants.VIEW_QUOTE
                    imgEditBusinessInfo.visibility = GONE
                    imgEditCustomerInfo.visibility = GONE
                    imgEditQuoteSummary.visibility = GONE
                    imgEditAddProduct.visibility = GONE
                    imgEditQuote.visibility = GONE
                    businessInfoEditMode = false
                    customerInfoEditMode = false
                    quoteSummaryEditMode = false
                    addProductEditMode = false
                }
                setData()
            }

            R.id.imgEditBusinessInfo -> {
                businessInfoEditMode = !businessInfoEditMode
                setEnableDisableBusinessInfo()
                imgEditBusinessInfo.setColorFilter(
                    if (businessInfoEditMode) ContextCompat.getColor(
                        activity as AppCompatActivity,
                        R.color.theme_green
                    ) else ContextCompat.getColor(
                        activity as AppCompatActivity,
                        android.R.color.black
                    )
                )
                if (businessInfoEditMode)
                    layoutInnerBusinessInfo.visibility = VISIBLE
                if (layoutInnerBusinessInfo.visibility == VISIBLE)
                    imgBusinessDropdown.rotation = 180f
            }

            R.id.imgEditCustomerInfo -> {
                customerInfoEditMode = !customerInfoEditMode
                setEnableDisableCustomerInfo()
                imgEditCustomerInfo.setColorFilter(
                    if (customerInfoEditMode) ContextCompat.getColor(
                        activity as AppCompatActivity,
                        R.color.theme_green
                    ) else ContextCompat.getColor(
                        activity as AppCompatActivity,
                        android.R.color.black
                    )
                )
                if (customerInfoEditMode)
                    layoutInnerCustomerInfo.visibility = VISIBLE
                if (layoutInnerCustomerInfo.visibility == VISIBLE)
                    imgCustomerDropdown.rotation = 180f
            }

            R.id.imgEditAddProduct -> {
                addProductEditMode = !addProductEditMode
                imgEditAddProduct.setColorFilter(
                    if (addProductEditMode)
                        ContextCompat.getColor(
                            activity as AppCompatActivity,
                            R.color.theme_green
                        ) else ContextCompat.getColor(
                        activity as AppCompatActivity,
                        android.R.color.black
                    )
                )
                if (addProductEditMode)
                    layoutInnerAddProducts.visibility = VISIBLE
                if (layoutInnerAddProducts.visibility == VISIBLE)
                    imgAddProductDropdown.rotation = 180f
            }

            R.id.imgEditQuoteSummary -> {
                quoteSummaryEditMode = !quoteSummaryEditMode
                setEnableDisableQuoteSummary()
                imgEditQuoteSummary.setColorFilter(
                    if (quoteSummaryEditMode) ContextCompat.getColor(
                        activity as AppCompatActivity,
                        R.color.theme_green
                    ) else ContextCompat.getColor(
                        activity as AppCompatActivity,
                        android.R.color.black
                    )
                )
                if (quoteSummaryEditMode)
                    layoutInnerQuoteSummary.visibility = VISIBLE
                if (layoutInnerQuoteSummary.visibility == VISIBLE)
                    imgQuoteSummaryDropdown.rotation = 180f
            }

            R.id.imgLogo -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    HomeFragment(),
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgMore -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    ContactFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.txtCreateOrder -> {
                sharedPrefs.quoteID = quoteDetailsResponse?.id.toString()!!
                if (sharedPrefs?.totalProductsInCart > 0) {

                    (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(getString(R.string.app_name),
                        sharedPrefs.quoteToOrderMessage
                        ,
                        getString(R.string.yes),
                        getString(R.string.no),
                        onDialogButtonClick = object : OnDialogButtonClick {
                            override fun onPositiveButtonClick() {
                                cartViewModel?.deleteProduct(null)
                                cartViewModel?.deleteCoupon(null)
                            }

                            override fun onNegativeButtonClick() {

                            }

                        })

                } else if (quoteDetailsResponse?.products != null && quoteDetailsResponse?.products!!.size > 0) {
                    insertPos = 0
                    getProductById()

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

    private fun confirmQuote(shouldSend: Boolean, email: String) {
        if (sendQuoteTo == null)
            sendQuoteTo = ArrayList()
        sendQuoteTo.clear()
        if (shouldSend)
            sendQuoteTo.add(email)

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
        } else if (edtAbn.text.toString().length < 11) {
            shortToast(getString(R.string.invalid_abn_messgae))
        } else if (Utility.isValueNull(edtCustomerName.text.toString())) {
            shortToast(getString(R.string.blank_customer_name_message))
        } else if (Utility.isValueNull(edtCustomerAddress.text.toString())) {
            shortToast(getString(R.string.blank_customer_address_message))
        } else if (Utility.isValueNull(edtEmailAddress.text.toString())) {
            shortToast(getString(R.string.blank_customer_email_address_message))
        } else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmailAddress.text.toString()).matches()) {
            shortToast(getString(R.string.invalid_customer_email_address_message))
        } else if (Utility.isValueNull(edtCustomerMobileNum.text.toString())) {
            shortToast(getString(R.string.blank_customer_mobile_num_message))
        } else if (edtCustomerMobileNum.text.toString().length < 10) {
            shortToast(getString(R.string.invalid_customer_mobile_num_message))
        } else if (quoteProducts == null || (quoteProducts != null && quoteProducts!!.size <= 0)) {
            shortToast(
                getString(
                    R.string.no_product_selected_message,
                    quoteDetailsResponse!!.id.toString()
                )
            )
        } else if (isAnyQtyBlank()) {
            shortToast(getString(R.string.blank_quantity_message))
        } else if (isAnyQtyZero()) {
            shortToast(getString(R.string.zero_quantity_message))
        } else if (Utility.isValueNull(
                edtDelivery.text.toString().replace("$", "").replace(
                    ",",
                    ""
                )
            )
        ) {
            shortToast(getString(R.string.blank_delivery_cost_message))
        } else if (Utility.isValueNull(edtDeliveryDate.text.toString())) {
            shortToast(getString(R.string.blank_delivery_date_message))
        } else if (!Utility.isValueNull(edtWeb.text.toString()) && !(edtWeb.text.toString().contains(
                "."
            ))
        ) {
            shortToast(getString(R.string.invalid_weburl_message))
        } else {
            var ancoProducts = ArrayList<QuoteAncoProductRequest>()
            var nonAncoProducts = ArrayList<QuoteNonAncoProductRequest>()
            if (quoteProducts != null && quoteProducts!!.size > 0) {
                for (i in 0 until quoteProducts!!.size) {
                    if (quoteProducts!![i].ancoProduct) {
                        var quoteAncoProductRequest =
                            QuoteAncoProductRequest(
                                quoteProducts!![i].id,
                                quoteProducts!![i].qty,
                                quoteProducts!![i].margin
                            )
                        ancoProducts.add(quoteAncoProductRequest)
                    } else {
                        var quoteNonAncoProductRequest =
                            QuoteNonAncoProductRequest(
                                quoteProducts!![i].id,
                                quoteProducts!![i].qty
                            )
                        nonAncoProducts.add(quoteNonAncoProductRequest)
                    }
                }
            }

            Utility.showProgress(requireContext(), "", false)

            val builder = Uri.Builder()
            builder.scheme(getString(R.string.config_scheme)) // "https"
                .authority(getString(R.string.config_host)) // "ancoapp.page.link"
                .appendPath(getString(R.string.config_path_quote)) // "quote"  https://ancoapp.page.link/quote
                .appendQueryParameter(Constants.QUOTE_ID_PARAM, quoteId.toString())
                .appendQueryParameter(Constants.QUOTE_STATUS_PARAM, quoteDetailsResponse?.status!!)

            var myUri = builder.build()

            FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(myUri)
                .setDomainUriPrefix("https://ancoapp.page.link")
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder("com.app.ancoturf").build())
                .setIosParameters(DynamicLink.IosParameters.Builder("com.ancoturf").build())
                .buildShortDynamicLink()
                .addOnCompleteListener {
                    if (it?.isSuccessful) {
                        val shortenUrl = it.result?.shortLink
                        Logger.log("shortenURL: $shortenUrl")

                        quoteViewModel?.callAddEditQuote(
                            quoteId,
                            quoteDetailsResponse!!.customerId.toInt(),
                            edtCustomerName.text.toString(),
                            edtCustomerAddress.text.toString(),
                            edtEmailAddress.text.toString(),
                            edtCustomerMobileNum.text.toString(),
                            edtPhoneNum.text.toString(),
                            edtDelivery.text.toString().replace("$", "").replace(",", ""),
                            Utility.changeDateFormat(
                                edtDeliveryDate.text.toString(),
                                Utility.DATE_FORMAT_D_MMMM_YYYY,
                                Utility.DATE_FORMAT_YYYY_MM_DD
                            ),
                            ancoProducts,
                            nonAncoProducts,
                            deletedProductIds,
                            deletedCustomProductIds,
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
                            quoteDetailsResponse!!.users?.business!!.registeredForGst,
                            selectedFilePathForBusiness,
                            sendQuoteTo,
                            shortenUrl.toString(),
                            edtNote.text.toString()
                        )
                    } else {
                        Utility.cancelProgress()
                    }
                }.addOnFailureListener {
                    Utility.cancelProgress()
                }
        }
    }

    private fun isAnyQtyBlank(): Boolean {
        if (quoteProducts != null && quoteProducts!!.size > 0) {
            for (i in 0 until quoteProducts!!.size) {
                if (Utility.isValueNull(quoteProducts!![i].qty.toString()))
                    return true
            }
        }
        return false
    }

    private fun isAnyQtyZero(): Boolean {
        if (quoteProducts != null && quoteProducts!!.size > 0) {
            for (i in 0 until quoteProducts!!.size) {
                if (quoteProducts!![i].qty!! <= 0)
                    return true
            }
        }
        return false
    }

    private fun openImageCaptureDialog(requestCodeForGallery: Int, requestCodeForCamera: Int) {
        val builder = AlertDialog.Builder(activity as AppCompatActivity)
        builder.setTitle(R.string.dialog_title_choose_image_from)
        builder.setItems(
            resources.getStringArray(R.array.dialog_choose_image)
        ) { _, which ->
            when (which) {
                0 -> checkCameraPermission(requestCodeForCamera)
                1 -> checkPermissionForGallery(requestCodeForGallery)
            }
        }
        builder.show()
    }

    private fun checkCameraPermission(requestCode: Int) {
        Log.e("3>", "Inside Camera Permission")
        Dexter.withActivity(activity as AppCompatActivity)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    // permission is granted
                    takePhoto(requestCode)
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

    private fun checkPermissionForGallery(requestCode: Int) {
        Log.e("3>", "Inside Gallery Permission")
        Dexter.withActivity(activity as AppCompatActivity)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    // permission is granted
                    fromGallery(requestCode)
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

    private fun fromGallery(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            this.startActivityForResult(intent, requestCode)
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

    private fun takePhoto(requestCode: Int) {
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
            var selectedFilePath = photoFile.absolutePath
            var cameraUri =
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
                startActivityForResult(pictureIntent, requestCode)
            } else {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.msg_app_not_found))
            }

            if (requestCode == REQUEST_GET_IMAGE_CAMERA_FOR_BUSINESS) {
                selectedFilePathForBusiness = selectedFilePath
                cameraUriForBusiness = cameraUri
            } else if (requestCode == REQUEST_GET_IMAGE_CAMERA_FOR_PRODUCT) {
                selectedFilePathForProduct = selectedFilePath
                cameraUriForProduct = cameraUri
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
            if ((requestCode == REQUEST_GET_IMAGE_CAMERA_FOR_BUSINESS && cameraUriForBusiness != null) || (requestCode == REQUEST_GET_IMAGE_CAMERA_FOR_PRODUCT && cameraUriForProduct != null)) {
                if (requestCode == REQUEST_GET_IMAGE_CAMERA_FOR_BUSINESS)
                    selectedPhotoUriForBusiness = cameraUriForBusiness
                else (requestCode == REQUEST_GET_IMAGE_CAMERA_FOR_PRODUCT)
                selectedPhotoUriForProduct = cameraUriForProduct
            } else if ((requestCode == REQUEST_GET_IMAGE_GALLERY_FOR_BUSINESS || requestCode == REQUEST_GET_IMAGE_GALLERY_FOR_PRODUCT) && data != null) {
                if (requestCode == REQUEST_GET_IMAGE_GALLERY_FOR_BUSINESS) {
                    selectedPhotoUriForBusiness = data.data
//                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    selectedPhotoUriForBusiness?.let {
                        selectedFilePathForBusiness = getPath(it)
                    }
                } else if (requestCode == REQUEST_GET_IMAGE_GALLERY_FOR_PRODUCT) {
                    selectedPhotoUriForProduct = data.data
//                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    selectedPhotoUriForProduct?.let {
                        selectedFilePathForProduct = getPath(it)
                    }
                }
//                }
            }
//            else if (requestCode == REQUEST_STORAGE_PERMISSION_SETTINGS) {
//                checkPermissionForGallery(REQUEST_GET_IMAGE_GALLERY_FOR_BUSINESS)
//            }
            try {
                if (!Utility.isValueNull(selectedFilePathForBusiness))
                    CheckImageOrientation.handleSamplingAndRotationBitmap(
                        requireContext(),
                        selectedPhotoUriForBusiness!!,
                        selectedFilePathForBusiness
                    )?.let { bitmap ->
                        Glide.with(requireContext()).load(selectedFilePathForBusiness)
                            .into(imgBusinessLogo)
                    } else if (!Utility.isValueNull(selectedFilePathForProduct))
                    CheckImageOrientation.handleSamplingAndRotationBitmap(
                        requireContext(),
                        selectedPhotoUriForProduct!!,
                        selectedFilePathForProduct
                    )?.let { bitmap ->
                        if (imgProductLogo != null) {
                            Glide.with(requireContext()).load(selectedFilePathForProduct)
                                .into(imgProductLogo)
                            txtAddProductLogo.visibility = GONE
                            imgDeleteImage.visibility = VISIBLE
                        }

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
        const val REQUEST_GET_IMAGE_GALLERY_FOR_BUSINESS = 1
        const val REQUEST_GET_IMAGE_CAMERA_FOR_BUSINESS = 2
        const val REQUEST_GET_IMAGE_GALLERY_FOR_PRODUCT = 3
        const val REQUEST_GET_IMAGE_CAMERA_FOR_PRODUCT = 4
        const val REQUEST_STORAGE_PERMISSION_SETTINGS = 7
        const val IMG_PROFILE = "IMG_PROFILE"
        const val RESULT_NOT_FOUND = "Not found"
    }

    private fun openDatePickerDialog() {
        val c = Calendar.getInstance()

        // dpd : Dev_N
        val dpd =
            com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance({ view, year, monthOfYear, dayOfMonth ->
                deliveryDate =
                    Utility.changeDateFormat(
                        "" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year,
                        Utility.DATE_FORMAT_D_M_YYYY,
                        Utility.DATE_FORMAT_D_MMMM_YYYY
                    )
                edtDeliveryDate.text = deliveryDate
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))

        var calendarMinDate = Calendar.getInstance()
        calendarMinDate.time = SimpleDateFormat("yyyy-MM-dd").parse(deliveryDates?.min_date)

        var calendarMaxDate = Calendar.getInstance()
        calendarMaxDate.time = SimpleDateFormat("yyyy-MM-dd").parse(deliveryDates?.max_date)

        dpd?.minDate = calendarMinDate
        dpd?.maxDate = calendarMaxDate

        deliveryDates?.disable_date?.size?.let {
            for (value in deliveryDates?.disable_date!!) {
                var calendars = arrayOfNulls<Calendar>(1)
                calendars[0] = Calendar.getInstance()
                calendars[0]?.time = SimpleDateFormat("yyyy-MM-dd").parse(value)
                dpd?.disabledDays = calendars
            }
        }
        dpd?.show(requireActivity().supportFragmentManager, "")

        /*
          val year = c.get(Calendar.YEAR)
         val month = c.get(Calendar.MONTH)
         val day = c.get(Calendar.DAY_OF_MONTH)

         val dpdOld = DatePickerDialog(
             activity,
             DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                 // Display Selected date in textbox
                 edtDeliveryDate.setText(
                     Utility.changeDateFormat(
                         "" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year,
                         Utility.DATE_FORMAT_D_M_YYYY,
                         Utility.DATE_FORMAT_D_MMMM_YYYY
                     )
                 )
             },
             year,
             month,
             day
         )*/

//        dpdOld.datePicker.minDate = System.currentTimeMillis()
//        dpdOld.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        quoteViewModel!!._quoteDetailsLiveData.value = null
    }
}
