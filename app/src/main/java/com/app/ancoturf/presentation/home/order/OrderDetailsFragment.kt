package com.app.ancoturf.presentation.home.order

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.order.remote.entity.response.OrderDataResponse
import com.app.ancoturf.data.order.remote.entity.response.OrderDetailResponse
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.extension.*
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.cart.CartViewModel
import com.app.ancoturf.presentation.cart.DeliveryDatesResponseModel
import com.app.ancoturf.presentation.cart.DeliveryDatesViewModel
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.order.adapters.OrderProductsAdapter
import com.app.ancoturf.presentation.home.tracking.LiveTrackingFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import com.app.ancoturf.utils.interfces.OnDialogButtonClick
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import kotlinx.android.synthetic.main.fragment_order_details.*
import kotlinx.android.synthetic.main.header.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class OrderDetailsFragment(
    private var orderDetailResponse: OrderDetailResponse?,
    private var position: Int
) : BaseFragment(),
    View.OnClickListener {


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var getDeliveryDatesViewModel: DeliveryDatesViewModel? = null
    private var orderViewModel: OrderViewModel? = null

    var orderProductsAdapter: OrderProductsAdapter? = null
    var products: ArrayList<OrderDataResponse.Product> = ArrayList()

    private var cartViewModel: CartViewModel? = null
    private var deliveryDate = ""
    private var txtScheduledDeliveryDate: TextView? = null

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private var dialog: Dialog? = null
    private var dpd: DatePickerDialog? = null
    private var quoteDate: String = ""

    var contactInfo: SettingsResponse.Data.ContactScreenSetting? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null


    override fun getContentResource(): Int = R.layout.fragment_order_details

    override fun viewModelSetup() {
        cartViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[CartViewModel::class.java]
        orderViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[OrderViewModel::class.java]

        getDeliveryDatesViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[DeliveryDatesViewModel::class.java]

        initObservers()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context)

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



        contactInfo =
            Gson().fromJson(
                sharedPrefs.contactInfo,
                SettingsResponse.Data.ContactScreenSetting::class.java
            )
//        Utility.showProgress(requireContext() , "" , false)
//        orderViewModel?.callGetOrderDetails(orderId)
        imgBack.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        txtReschedule.setOnClickListener(this)
        txtCancel.setOnClickListener(this)
        txtLiveTracking.setOnClickListener(this)
        txtGetOrderDetail.setOnClickListener(this)

        setData()


        if (orderDetailResponse != null) {
            orderRefView.visibility = View.GONE
            layoutOrderDetail.visibility = View.VISIBLE
        } else {
            orderRefView.visibility = View.VISIBLE
            txtRefNumber.requestFocus()
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.showSoftInput(txtRefNumber, InputMethodManager.SHOW_IMPLICIT)
            layoutOrderDetail.visibility = View.GONE
        }
        arguments?.let {
            orderRefView.visibility = View.GONE
            layoutOrderDetail.visibility = View.VISIBLE
            orderViewModel?.callGetOrderDetails(arguments?.getString(Constants.REFERENCE_NUMBER)!!)

        }

        getDeliveryDatesViewModel?.getDeliveryDates(sharedPrefs.userType!!)


    }

    private fun setData() {
        if (orderDetailResponse != null) {
            layoutOrderDetail.visibility = View.VISIBLE
            txtOrderNumber.text =
                getString(R.string.order_number, orderDetailResponse!!.referenceNumber)
            if (!Utility.isValueNull(orderDetailResponse!!.createdAt))
                txtOrderDate.text = Utility.changeDateFormat(
                    orderDetailResponse!!.createdAt,
                    Utility.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS,
                    Utility.DATE_FORMAT_DD_MM_YY
                )
            if (orderDetailResponse?.instructionsNotes != null) {
                txtOrderInstructions.text = orderDetailResponse?.instructionsNotes
            }
            if (!Utility.isValueNull(orderDetailResponse!!.createdAt))
                txtDeliveryDate.text = Utility.changeDateFormat(
                    orderDetailResponse!!.deliveryDate,
                    Utility.DATE_FORMAT_YYYY_MM_DD,
                    Utility.DATE_FORMAT_DD_MM_YY
                )

            if (!Utility.isValueNull(orderDetailResponse!!.invoiceDate))
                txtInvoicedDate.text = Utility.changeDateFormat(
                    orderDetailResponse!!.invoiceDate,
                    Utility.DATE_FORMAT_YYYY_MM_DD,
                    Utility.DATE_FORMAT_DD_MM_YY
                )

            if (!Utility.isValueNull(orderDetailResponse!!.paymentDate))
                txtPaymentDate.text = Utility.changeDateFormat(
                    orderDetailResponse!!.paymentDate,
                    Utility.DATE_FORMAT_YYYY_MM_DD,
                    Utility.DATE_FORMAT_DD_MM_YY
                )

            if (!Utility.isValueNull(orderDetailResponse?.orderStatus?.displayName) && !Utility.isValueNull(
                    orderDetailResponse?.deliveryStatus?.displayName
                )
            ) {
                txtOrderStatus.text =
                    "${orderDetailResponse?.orderStatus?.displayName} - ${orderDetailResponse?.deliveryStatus?.displayName}"
            }

//            if (orderDetailResponse!!.shippingType.contains(Constants.DELIVER_TO_DOOR_LABEL)) {
            if (orderDetailResponse!!.shippingType != null && orderDetailResponse!!.shippingType.contains(
                    Constants.DELIVER_TO_DOOR_LABEL
                )
            ) {
                txtDeliveryAddressLabel.text = getString(R.string.delivery_address_label)
                if (!Utility.isValueNull(orderDetailResponse!!.shippingAddressLine1)) {
                    if (!Utility.isValueNull(orderDetailResponse!!.shippingAddressLine2)) {
                        txtDeliveryAddress.text =
                            orderDetailResponse!!.shippingAddressLine1 + ", " + orderDetailResponse!!.shippingAddressLine2
                    } else {
                        txtDeliveryAddress.text =
                            orderDetailResponse!!.shippingAddressLine1
                    }
                    if (!Utility.isValueNull(orderDetailResponse!!.shippingCity)) {
                        txtDeliveryAddress.text =
                            txtDeliveryAddress.text.toString() + ", " + orderDetailResponse?.shippingCity
                    }
                } else
                    if (!Utility.isValueNull(orderDetailResponse!!.shippingCity)) {
                        txtDeliveryAddress.text =
                            txtDeliveryAddress.text.toString() + ", " + orderDetailResponse?.shippingCity
                    } else {
                        txtDeliveryAddress.text = "---------"
                    }
            } else {
                txtDeliveryAddressLabel.text = getString(R.string.pickup_address_label)
                if (contactInfo != null) {
                    if (!Utility.isValueNull(contactInfo!!.contactAddressLine1) || !Utility.isValueNull(
                            contactInfo!!.contactAddressLine2
                        ) || !Utility.isValueNull(contactInfo!!.contactAddressLine3)
                    ) {
                        val address = StringBuilder()
                        if (!Utility.isValueNull(contactInfo!!.contactAddressLine1))
                            address.append(contactInfo!!.contactAddressLine1 + "\n")
                        if (!Utility.isValueNull(contactInfo!!.contactAddressLine2))
                            address.append(contactInfo!!.contactAddressLine2 + "\n")
                        if (!Utility.isValueNull(contactInfo!!.contactAddressLine3))
                            address.append(contactInfo!!.contactAddressLine3)
                        txtDeliveryAddress.text = address.toString()
                    }
                }
            }

            if (products == null)
                products = ArrayList()
            products.clear()
            //HERE
            if (orderDetailResponse!!.products.size > 0) {
                var tmpProductList: ArrayList<OrderDataResponse.Product> = ArrayList()
                var tmpMainListProductList: ArrayList<OrderDataResponse.Product> = ArrayList()
                tmpMainListProductList.addAll(orderDetailResponse!!.products)
                for (i in 0 until orderDetailResponse!!.products.size) {
                    if (orderDetailResponse!!.products[i].free_product != 0 && orderDetailResponse!!.products[i].free_products != null && (orderDetailResponse!!.products[i]).free_products.product_id != 0 && orderDetailResponse!!.products[i].pivot.quantity >= orderDetailResponse!!.products[i].free_product_qty) {
                        var tmpProduct: OrderDataResponse.Product = OrderDataResponse.Product()
                        tmpProduct.productUnit = "Quantity"
                        tmpProduct.featureImageUrl =
                            orderDetailResponse?.products!![i].free_products.feature_image_url
                        tmpProduct.pivot.quantity =
                            orderDetailResponse?.products!![i].free_products.quantity
                        tmpProduct.name =
                            orderDetailResponse?.products!![i].free_products.productName
                        tmpProductList.add(tmpProduct)
                    }
                }
                if (tmpProductList.size > 0) {
                    tmpMainListProductList.addAll(tmpProductList)
                }
                products.addAll(tmpMainListProductList)

            }

            setProductAdapter()

            txtTotalAmount.text =
                "$${
                    Utility.formatNumber(
                        Utility.roundTwoDecimals(orderDetailResponse!!.totalCartPrice.toDouble())
                            .toFloat()
                    ).trim()
                }"
            txtShippingAmount.text =
                "$${
                    Utility.formatNumber(
                        Utility.roundTwoDecimals(orderDetailResponse!!.shippingCost.toDouble())
                            .toFloat()
                    ).trim()
                }"

            if (orderDetailResponse!!.deliveryStatus != null) {
                if (!(!sharedPrefs.isLogged || orderDetailResponse!!.deliveryStatus.slug.equals(
                        Constants.DELIVERED,
                        true
                    )
                            || orderDetailResponse!!.orderStatus.slug.equals(
                        Constants.CANCELLED,
                        true
                    ))
                ) {
                    txtReschedule.visibility = View.VISIBLE
                    txtCancel.visibility = View.VISIBLE
                    txtLiveTracking.visibility = View.VISIBLE
                    txtLiveTracking.isEnabled =
                        orderDetailResponse!!.deliveryStatus.slug.equals(Constants.ON_THE_WAY, true)
                    txtLiveTracking.setTextColor(
                        if (orderDetailResponse!!.deliveryStatus.slug.equals(
                                Constants.ON_THE_WAY,
                                true
                            )
                        ) ContextCompat.getColor(
                            requireContext(),
                            R.color.theme_green
                        ) else ContextCompat.getColor(requireContext(), R.color.grey)
                    )
                    txtLiveTracking.setBackgroundResource(
                        if (orderDetailResponse!!.deliveryStatus.slug.equals(
                                Constants.ON_THE_WAY,
                                true
                            )
                        ) R.drawable.bg_green_line_rounded else R.drawable.bg_grey_line_rounded
                    )
                } else {
                    txtLiveTracking.visibility = View.GONE
                    txtReschedule.visibility = View.GONE
                    txtCancel.visibility = View.GONE
                }
            }
        }
    }

    var deliveryDates: DeliveryDatesResponseModel? = null

    private fun initObservers() {


        getDeliveryDatesViewModel?.deliveryDatesLiveData?.observe(this, Observer {
            if (it != null) {
                var response: String = it
                response.let {
                    var jsonObject: JSONObject = JSONObject(response)
                    var data: JSONObject = jsonObject.getJSONObject("data")
                    var gson: Gson = Gson()
                    deliveryDates =
                        gson.fromJson(data.toString(), DeliveryDatesResponseModel::class.java)
                    if (arguments != null && requireArguments().containsKey("deliveryDateOfQuote")) {
                        quoteDate = requireArguments().getString("deliveryDateOfQuote").toString()
                        var strQuoteDateCompare: String = Utility.changeDateFormat(
                            quoteDate,
                            Utility.DATE_FORMAT_D_MMMM_YYYY,
                            Utility.DATE_FORMAT_YYYY_MM_DD
                        )
                        var setDate: Boolean = false
                        deliveryDates?.enable_date?.size?.let {
                            for (value in deliveryDates?.enable_date!!) {
                                if (value == strQuoteDateCompare) {
                                    setDate = true
                                    break
                                }
                            }
                            if (setDate) {
                                txtDeliveryDate.text = quoteDate
                            }
                        }
                    }
                }
            }
        })

        orderViewModel!!.orderDetailLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null) {
                orderDetailResponse = it
                setData()

                orderViewModel?._orderDetailLiveData?.value = null

                val bundle = Bundle()
                bundle.putString("OrderNo", orderDetailResponse?.referenceNumber)
                bundle.putString("OrderValue", orderDetailResponse?.totalCartPrice)
                bundle.putString("Postcode", orderDetailResponse?.shippingPostCode)
                bundle.putString("Suburb", orderDetailResponse?.shippingCity)
                bundle.putString("Email", orderDetailResponse?.user?.email)
                mFirebaseAnalytics?.logEvent("tracking_click", bundle)

                arguments?.let {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        LiveTrackingFragment(orderDetailResponse!!),
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                }
            }
        })

        orderViewModel!!.rescheduleOrderLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null) {
                if (it.success) {
                    dialog?.dismiss()
                    if (it.data != null && !Utility.isValueNull(it.data.deliveryDate)) {
                        txtDeliveryDate.text = Utility.changeDateFormat(
                            it.data.deliveryDate,
                            Utility.DATE_FORMAT_YYYY_MM_DD,
                            Utility.DATE_FORMAT_DD_MM_YY
                        )
                        shortToast(it.message)
                    }
                } else
                    shortToast(it.message)
                orderViewModel!!._rescheduleOrderLiveData.value = null
            }
        })

        orderViewModel!!.cancelOrderLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null) {
                if (it.success) {

                    txtLiveTracking.visibility = View.GONE
                    txtReschedule.visibility = View.GONE
                    txtCancel.visibility = View.GONE
                    var cancelResponse = it.data
                    txtOrderStatus.text =
                        "${cancelResponse?.orderStatus?.displayName} - ${orderDetailResponse?.deliveryStatus?.displayName}"


                } else
                    shortToast(it.message)
                orderViewModel!!._rescheduleOrderLiveData.value = null
            }
        })

        cartViewModel!!.deliveryDateValidationLiveData.observe(this, Observer {
            if (it != null) {
                if (it) {
                    orderViewModel?.callRescheduleOrders(
                        orderDetailResponse!!.id,
                        Utility.changeDateFormat(
                            deliveryDate,
                            Utility.DATE_FORMAT_D_MMMM_YYYY,
                            Utility.DATE_FORMAT_YYYY_MM_DD
                        )
                    )
                } else {
                    shortToast(getString(R.string.invalid_delivery_date_messgae))
                }
                cartViewModel!!._deliveryDateValidationLiveData.value = null
            }
        })

        orderViewModel!!.errorLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                if (it.equals(ErrorConstants.UNAUTHORIZED_ERROR_CODE)) {
                    (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                } else {
                    it?.let { it1 -> shortToast(it1) }
                }
                orderViewModel?._errorLiveData?.value = null
            }
        })
    }

    private fun setProductAdapter() {
        if (products != null && products.size > 0) {

            orderProductsAdapter =
                OrderProductsAdapter(requireActivity() as AppCompatActivity, products)
            listProducts.adapter = orderProductsAdapter
        }
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {
            R.id.imgBack -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }

            R.id.imgLogo -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                if (activity is HomeActivity) {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        HomeFragment(),
                        false,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                } else {
                    requireActivity().startActivity(
                        Intent(
                            requireActivity(),
                            HomeActivity::class.java
                        )
                    )
                    requireActivity().finishAffinity()
                }
            }
            R.id.imgBell -> {
                openNotificationFragment()
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

            R.id.txtGetOrderDetail -> {
                if (!Utility.isValueNull(txtRefNumber.text.toString()))
                    orderViewModel?.callGetOrderDetails(txtRefNumber.text.toString())
                else
                    shortToast(getString(R.string.blank_ref_num_msg))
            }

            R.id.txtReschedule -> {
                if (!orderDetailResponse!!.deliveryStatus.slug.equals(
                        Constants.ON_THE_WAY,
                        true
                    ) && isReschedulable()
                ) {
                    openRescheduleDialog()
                } else
                    shortToast("Please call  ${contactInfo?.contactPhone} or email ${contactInfo?.contactEmail} to cancel or reschedule your order.")
            }

            R.id.txtLiveTracking -> {

                val bundle = Bundle()
                bundle.putString("OrderNo", orderDetailResponse?.referenceNumber)
                bundle.putString("OrderValue", orderDetailResponse?.totalCartPrice)
                bundle.putString("Postcode", orderDetailResponse?.shippingPostCode)
                bundle.putString("Suburb", orderDetailResponse?.shippingCity)
                bundle.putString("Email", orderDetailResponse?.user?.email)
                mFirebaseAnalytics?.logEvent("tracking_click", bundle)

                (requireActivity() as AppCompatActivity).pushFragment(
                    LiveTrackingFragment(orderDetailResponse!!),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.txtCancel -> {
                if (!orderDetailResponse!!.deliveryStatus.slug.equals(
                        Constants.PICKED_UP,
                        true
                    ) && !orderDetailResponse!!.deliveryStatus.slug.equals(
                        Constants.ON_THE_WAY,
                        true
                    ) && (orderDetailResponse!!.orderStatus.slug.equals(
                        Constants.INVOICED,
                        true
                    ) || orderDetailResponse!!.orderStatus.slug.equals(
                        Constants.PAID,
                        true
                    ) || isReschedulable())
                ) {
                    (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                        title = getString(R.string.app_name),
                        message = getString(R.string.cancel_dialog_message),
                        positiveButton = getString(R.string.yes),
                        nagetiveButton = getString(R.string.no),
                        onDialogButtonClick = object : OnDialogButtonClick {
                            override fun onPositiveButtonClick() {
                                Utility.showProgress(requireContext(), "", false)
                                orderViewModel?.callCancelOrders(orderDetailResponse!!.id, position)
                            }

                            override fun onNegativeButtonClick() {
                            }
                        })
                } else
                    shortToast("Please call ${contactInfo?.contactPhone} or email ${contactInfo?.contactEmail} to cancel or reschedule your order.")
            }
        }
    }

    private fun isReschedulable(): Boolean {
        if (!Utility.isValueNull(orderDetailResponse?.deliveryDate)) {
            var deliveryMillis = Utility.getMillis(
                Utility.changeDateFormat(
                    orderDetailResponse?.deliveryDate,
                    Utility.DATE_FORMAT_YYYY_MM_DD,
                    Utility.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS
                )
            )
            if (deliveryMillis > System.currentTimeMillis()) {
                var millisDiff = deliveryMillis - System.currentTimeMillis()
                if ((millisDiff / 1000 / 60 / 60) > 72)
                    return true
            }
        }
        return false
    }

    private fun openRescheduleDialog() {
        // Create custom dialog object
        dialog = Dialog(requireContext())

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Include dialog.xml file
        dialog?.setContentView(R.layout.dialog_reschedule)
        dialog?.show()

        val window = dialog?.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)

        var c = Calendar.getInstance()
        c.add(Calendar.DATE, sharedPrefs.deliveryMinDays)
        deliveryDate = Utility.getDateFromMillis(c.timeInMillis, Utility.DATE_FORMAT_D_MMMM_YYYY)


        txtScheduledDeliveryDate = dialog?.findViewById(R.id.txtDeliveryDate)
        txtScheduledDeliveryDate?.text = Utility.changeDateFormat(
            orderDetailResponse!!.deliveryDate,
            Utility.DATE_FORMAT_YYYY_MM_DD,
            Utility.DATE_FORMAT_D_MMMM_YYYY
        )
        txtScheduledDeliveryDate?.setOnClickListener {
            openDatePickerDialog()
        }

        val imgClose = dialog?.findViewById(R.id.imgClose) as ImageView
        imgClose.setOnClickListener {
            dialog?.dismiss()
        }

        val txtDeliveryDateInfo = dialog?.findViewById(R.id.txtDeliveryDateInfo) as TextView
        txtDeliveryDateInfo.text = sharedPrefs.deliveryMessage

        val txtReschedule = dialog?.findViewById(R.id.txtReschedule) as TextView
        txtReschedule.setOnClickListener {
            Utility.showProgress(requireContext(), "", false)
//            orderViewModel?.callRescheduleOrders(
//                orderDetailResponse!!.id,
//                Utility.changeDateFormat(deliveryDate, Utility.DATE_FORMAT_D_MMMM_YYYY, Utility.DATE_FORMAT_YYYY_MM_DD)
//            )
            cartViewModel?.deliveryDAteValidation(
                Utility.changeDateFormat(
                    deliveryDate,
                    Utility.DATE_FORMAT_D_MMMM_YYYY,
                    Utility.DATE_FORMAT_YYYY_MM_DD
                )
            )
        }
    }

    private fun openDatePickerDialog() {
        val c = Calendar.getInstance()

        dpd = DatePickerDialog.newInstance({ view, year, monthOfYear, dayOfMonth ->
            deliveryDate =
                Utility.changeDateFormat(
                    "" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year,
                    Utility.DATE_FORMAT_D_M_YYYY,
                    Utility.DATE_FORMAT_D_MMMM_YYYY
                )
            txtScheduledDeliveryDate?.text = deliveryDate
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
          dpd?.minDate = c

          val minCal = Calendar.getInstance()
          minCal.add(Calendar.DATE, sharedPrefs.deliveryMinDays)
          dpd?.minDate = minCal

          val maxCal = Calendar.getInstance()
          maxCal.add(Calendar.DATE, sharedPrefs.deliveryMaxDays)
          dpd?.maxDate = maxCal

          //Disable all SUNDAYS and SATURDAYS between Min and Max Dates
          var loopdate = minCal
          while (minCal.before(maxCal)) {
              val dayOfWeek = loopdate.get(Calendar.DAY_OF_WEEK)
              if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.MONDAY) {
                  val disabledDays = arrayOfNulls<Calendar>(1)
                  disabledDays[0] = loopdate
                  dpd?.disabledDays = disabledDays
              }
              minCal.add(Calendar.DATE, 1)
              loopdate = minCal
          }*/
    }


    /*commented by nirali private fun openDatePickerDialog() {
         val c = Calendar.getInstance()

         dpd = DatePickerDialog.newInstance({ view, year, monthOfYear, dayOfMonth ->
             deliveryDate =
                 Utility.changeDateFormat(
                     "" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year,
                     Utility.DATE_FORMAT_D_M_YYYY,
                     Utility.DATE_FORMAT_D_MMMM_YYYY
                 )
             txtScheduledDeliveryDate?.text = deliveryDate
         }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH))
         dpd?.minDate = c

         val minCal = Calendar.getInstance()
         minCal.add(Calendar.DATE, sharedPrefs.deliveryMinDays)
         dpd?.minDate = minCal

         val maxCal = Calendar.getInstance()
         maxCal.add(Calendar.DATE, sharedPrefs.deliveryMaxDays)
         dpd?.maxDate = maxCal

         //Disable all SUNDAYS and SATURDAYS between Min and Max Dates
         var loopdate = minCal
         while (minCal.before(maxCal)) {
             val dayOfWeek = loopdate.get(Calendar.DAY_OF_WEEK)
             if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.MONDAY) {
                 val disabledDays = arrayOfNulls<Calendar>(1)
                 disabledDays[0] = loopdate
                 dpd?.disabledDays = disabledDays
             }
             minCal.add(Calendar.DATE, 1)
             loopdate = minCal
         }
         dpd?.show(requireActivity().supportFragmentManager, "")
     }*/


    override fun onDestroy() {
        super.onDestroy()
        orderViewModel?.data?.put(UseCaseConstants.STATUS, "")
        orderViewModel?.data?.put(UseCaseConstants.PRICE_MIN, "")
        orderViewModel?.data?.put(UseCaseConstants.PRICE_MAX, "")
        orderViewModel?.data?.put(UseCaseConstants.ADDRESS, "")
        orderViewModel?.data?.put(UseCaseConstants.SORT_BY, "")
    }
}
