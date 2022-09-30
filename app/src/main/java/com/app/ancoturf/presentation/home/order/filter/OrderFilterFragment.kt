package com.app.ancoturf.presentation.home.order.filter

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.product.remote.entity.response.ProductSortByCategories
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.openAlertDialogWithOneClick
import com.app.ancoturf.extension.openAlertDialogWithTwoClick
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.order.OrderViewModel
import com.app.ancoturf.presentation.home.order.filter.adapter.DeliveryStatusFilterAdapter
import com.app.ancoturf.presentation.home.order.filter.adapter.OrderStatusFilterAdapter
import com.app.ancoturf.presentation.home.products.filter.adapters.SpinnerSortByAdapter
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.utils.Utility
import com.app.ancoturf.utils.interfces.OnDialogButtonClick
import com.appyvet.materialrangebar.RangeBar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_orders_filter.*
import kotlinx.android.synthetic.main.header_filter.*
import javax.inject.Inject

class OrderFilterFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private var orderViewModel: OrderViewModel? = null

    var orderStatusFilterAdapter: OrderStatusFilterAdapter? = null
    var orderStatuses: ArrayList<SettingsResponse.Data.OrderStatus> = ArrayList()

    var deliveryStatusFilterAdapter: DeliveryStatusFilterAdapter? = null
    var deliveryStatuses: ArrayList<SettingsResponse.Data.DeliveryStatus> = ArrayList()

    private var spinnerSortByAdapter: SpinnerSortByAdapter? = null
    var sortByCategories: List<ProductSortByCategories> = arrayOf(
        ProductSortByCategories("Default Sorting", ""),
        ProductSortByCategories(
            "Sort by Price - high to low",
            "cost-desc"
        ),
        ProductSortByCategories(
            "Sort by Price - low to high",
            "cost"
        ),
        ProductSortByCategories(
            "Sort by date - oldest to newest",
            "date"
        ),
        ProductSortByCategories("Sort by date - newest to oldest", "date-desc")
    ).toList()

    val data = mutableMapOf(
        UseCaseConstants.ORDER_STATUS to "",
        UseCaseConstants.DELIVERY_STATUS to "",
        UseCaseConstants.PRICE_MIN to "",
        UseCaseConstants.PRICE_MAX to "",
        UseCaseConstants.ADDRESS to "",
        UseCaseConstants.SORT_BY to ""
    )

    var title: String? = "Shop All"
    var numberOfFilters: Int? = 0
    var priceFilter = 0
    var orderStastusFilters = 0
    var deliveryStastusFilters = 0
    var sortingFilter = 0

    override fun getContentResource(): Int = R.layout.fragment_orders_filter

    override fun viewModelSetup() {
        orderViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)[OrderViewModel::class.java]
    }

    override fun viewSetup() {
        if(activity is HomeActivity) {
            (activity as HomeActivity).hideShowFooter(false)
        } else if(activity is ProfileActivity) {
            (activity as ProfileActivity).hideShowFooter(false)
        } else if(activity is PaymentActivity) {
            (activity as PaymentActivity).hideShowFooter(false)
        }

        imgLogo.visibility = View.GONE
        txtTitle.visibility = View.VISIBLE

        if (orderStatuses == null)
            orderStatuses = ArrayList()
        orderStatuses.clear()

        orderStatuses.addAll(Gson().fromJson(sharedPrefs.orderStatues, Array<SettingsResponse.Data.OrderStatus>::class.java))

        orderViewModel?._orderStatusLiveData?.value = orderStatuses

        if (deliveryStatuses == null)
            deliveryStatuses = ArrayList()
        deliveryStatuses.clear()
        deliveryStatuses.addAll(Gson().fromJson(sharedPrefs.deliveryStatues, Array<SettingsResponse.Data.DeliveryStatus>::class.java))

        setData()

        btnApply.setOnClickListener(this)
        viewApply.setOnClickListener(this)

        txtClearAll.setOnClickListener(this)
        imgClose.setOnClickListener(this)
    }

    private fun setData() {
        data.put(
            UseCaseConstants.ORDER_STATUS,
            orderViewModel?.data?.get(UseCaseConstants.ORDER_STATUS)!!
        )
        data.put(
            UseCaseConstants.DELIVERY_STATUS,
            orderViewModel?.data?.get(UseCaseConstants.DELIVERY_STATUS)!!
        )
        data.put(UseCaseConstants.PRICE_MIN, orderViewModel?.data?.get(UseCaseConstants.PRICE_MIN)!!)
        data.put(UseCaseConstants.PRICE_MAX, orderViewModel?.data?.get(UseCaseConstants.PRICE_MAX)!!)
        data.put(UseCaseConstants.ADDRESS, orderViewModel?.data?.get(UseCaseConstants.ADDRESS)!!)
        data.put(UseCaseConstants.SORT_BY, orderViewModel?.data?.get(UseCaseConstants.SORT_BY)!!)

//        title = orderViewModel?.title
        numberOfFilters = orderViewModel?.numberOfFilters

        setEnableDisableClearAll()

        priceFilter =
            if ((Utility.isValueNull(data.get(UseCaseConstants.PRICE_MIN) as String) || data.get(UseCaseConstants.PRICE_MIN).equals(
                    "0"
                )) &&
                (Utility.isValueNull(data.get(UseCaseConstants.PRICE_MAX) as String) || data.get(UseCaseConstants.PRICE_MAX).equals(
                    sharedPrefs.order_max_price
                ))
            ) {
                0
            } else {
                1
            }

        //set search value
        edtSearch.setText(data.get(UseCaseConstants.ADDRESS) as String)
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                data?.put(UseCaseConstants.ADDRESS, s.toString())
            }
        })

        //set status values
        setOrderStatusAdapter()
        setDeliveryStatusAdapter()

        //set sort by values
        spinnerSortByAdapter =
            SpinnerSortByAdapter(requireActivity(), sortByCategories)
        spinnerSortBy.setAdapter(spinnerSortByAdapter)
        orderViewModel?.data?.get(UseCaseConstants.SORT_BY)
            ?.let { if (getSelectedItemPosition() != -1) spinnerSortBy.setSelection(getSelectedItemPosition()) }

        if (getSelectedItemPosition() == -1 || getSelectedItemPosition() == 0) sortingFilter = 0 else sortingFilter = 1

        spinnerSortBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            //Performing action onItemSelected and onNothing selected
            override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
                data?.put(UseCaseConstants.SORT_BY, sortByCategories.get(position).slug)
                if (position == -1 || position == 0) sortingFilter = 0 else sortingFilter = 1
                setEnableDisableClearAll()
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        //set price values
        seekMinMax.setTickInterval((sharedPrefs.order_max_price.toFloat() / 10))

        seekMinMax.tickEnd = sharedPrefs.order_max_price.toFloat()
        seekMinMax.tickStart = 0f

        if (!orderViewModel?.data?.get(UseCaseConstants.PRICE_MAX).equals("") && !orderViewModel?.data?.get(
                UseCaseConstants.PRICE_MIN
            ).equals("")
        ) {
            seekMinMax.setRangePinsByValue(
                orderViewModel?.data?.get(UseCaseConstants.PRICE_MIN)!!.toFloat(),
                orderViewModel?.data?.get(UseCaseConstants.PRICE_MAX)!!.toFloat()
            )
        } else {
            if (!orderViewModel?.data?.get(UseCaseConstants.PRICE_MAX).equals("")) {
                seekMinMax.setRangePinsByValue(0f, orderViewModel?.data?.get(UseCaseConstants.PRICE_MAX)!!.toFloat())
            } else if (!orderViewModel?.data?.get(UseCaseConstants.PRICE_MIN).equals("")) {
                seekMinMax.setRangePinsByValue(
                    orderViewModel?.data?.get(UseCaseConstants.PRICE_MIN)!!.toFloat(),
                    sharedPrefs.order_max_price.toFloat()
                )
            } else {
                seekMinMax.setRangePinsByValue(0f, sharedPrefs.order_max_price.toFloat())
            }
        }
        if (!orderViewModel?.data?.get(UseCaseConstants.PRICE_MIN).equals("")) {
            orderViewModel?.data?.get(UseCaseConstants.PRICE_MIN)
                ?.let { seekMinMax.left = it.toInt() }
        }

        seekMinMax.setOnRangeBarChangeListener(object : RangeBar.OnRangeBarChangeListener {
            override fun onRangeChangeListener(
                rangeBar: RangeBar?,
                leftPinIndex: Int,
                rightPinIndex: Int,
                leftPinValue: String?,
                rightPinValue: String?
            ) {
                // orderViewModel?.data?.put(UseCaseConstants.PRICE_MIN, minValue.toString())
                leftPinValue?.let {
                    data?.put(UseCaseConstants.PRICE_MIN, it)
                    setEnableDisableClearAll()
                }
                // orderViewModel?.data?.put(UseCaseConstants.PRICE_MAX, maxValue.toString())
                rightPinValue?.let {
                    data?.put(UseCaseConstants.PRICE_MAX, it)
                    setEnableDisableClearAll()
                }

                if ((Utility.isValueNull(data.get(UseCaseConstants.PRICE_MIN) as String) || data.get(UseCaseConstants.PRICE_MIN).equals(
                        "0"
                    )) &&
                    (Utility.isValueNull(data.get(UseCaseConstants.PRICE_MAX) as String) || data.get(UseCaseConstants.PRICE_MAX).equals(
                        sharedPrefs.order_max_price
                    ))
                ) {
                    priceFilter = 0
                } else {
                    priceFilter = 1
                }
            }

            override fun onTouchStarted(rangeBar: RangeBar?) {
            }

            override fun onTouchEnded(rangeBar: RangeBar?) {
            }
        })
    }

    private fun getSelectedItemPosition(): Int {
        var selectedPosition = -1
        var selectedItem = orderViewModel?.data?.get(UseCaseConstants.SORT_BY)
        for (i in 0..(sortByCategories.size - 1)) {
            var productSortByCategories: ProductSortByCategories = sortByCategories.get(i)
            if (productSortByCategories.slug.equals(selectedItem))
                return i
        }
        return selectedPosition
    }

    fun setSelectedOrderStatus(status: ArrayList<SettingsResponse.Data.OrderStatus>) {
        data?.put(UseCaseConstants.ORDER_STATUS, getOrderStatusIds(status))
        setEnableDisableClearAll()
    }

    fun setSelectedDeliveryStatus(status: ArrayList<SettingsResponse.Data.DeliveryStatus>) {
        data?.put(UseCaseConstants.DELIVERY_STATUS, getDeliveryStatusIds(status))
        setEnableDisableClearAll()
    }

    private fun getOrderStatusIds(status: ArrayList<SettingsResponse.Data.OrderStatus>): String {
//        var anyChecked = false
        orderStastusFilters = 0
        val selectedCategories = StringBuilder()
        if (status != null && status.size > 0) {
            for (i in 0 until status.size) {
                if (status.get(i).checked) {
//                    anyChecked = true
                    if (Utility.isValueNull(selectedCategories.toString())) {
                        selectedCategories.append(status.get(i).id)
//                        title = categories.get(i).displayName
                    } else {
                        selectedCategories.append("," + status.get(i).id)
//                        title = getString(R.string.shop_all)
                    }
                    orderStastusFilters = orderStastusFilters + 1
                }
            }
        } else {
//            title = getString(R.string.shop_all)
        }
//        if (!anyChecked)
//            title = getString(R.string.shop_all)
        return selectedCategories.toString()
    }

    private fun getDeliveryStatusIds(status: ArrayList<SettingsResponse.Data.DeliveryStatus>): String {
//        var anyChecked = false
        deliveryStastusFilters = 0
        val selectedCategories = StringBuilder()
        if (status != null && status.size > 0) {
            for (i in 0 until status.size) {
                if (status.get(i).checked) {
//                    anyChecked = true
                    if (Utility.isValueNull(selectedCategories.toString())) {
                        selectedCategories.append(status.get(i).id)
//                        title = categories.get(i).displayName
                    } else {
                        selectedCategories.append("," + status.get(i).id)
//                        title = getString(R.string.shop_all)
                    }
                    deliveryStastusFilters = deliveryStastusFilters + 1
                }
            }
        } else {
//            title = getString(R.string.shop_all)
        }
//        if (!anyChecked)
//            title = getString(R.string.shop_all)
        return selectedCategories.toString()
    }

    private fun setOrderStatusAdapter() {
//        if(productCategoryAdapter == null) {
        var status = data.get(UseCaseConstants.ORDER_STATUS) as String
        var statusArray = status.split(",")
        for (i in 0 until orderStatuses.size) {
            for (j in 0 until statusArray.size) {
                if (orderStatuses[i].id.toString().equals(statusArray[j])) {
                    orderStatuses[i].checked = true
                    break
                }
            }
        }

        orderStatusFilterAdapter =
            activity?.let {
                OrderStatusFilterAdapter(
                    it as AppCompatActivity,
                    orderStatuses,
                    this
                )
            }!!
        listOrderStatus.adapter = orderStatusFilterAdapter
//        } else{
//            productCategoryAdapter!!.offers = orderStatuses
//            productCategoryAdapter!!.notifyDataSetChanged()
//        }
    }

    private fun setDeliveryStatusAdapter() {
//        if(productCategoryAdapter == null) {
        var status = data.get(UseCaseConstants.DELIVERY_STATUS) as String
        var statusArray = status.split(",")
        for (i in 0 until deliveryStatuses.size) {
            for (j in 0 until statusArray.size) {
                if (deliveryStatuses[i].id.toString().equals(statusArray[j])) {
                    deliveryStatuses[i].checked = true
                    break
                }
            }
        }

        deliveryStatusFilterAdapter =
            activity?.let {
                DeliveryStatusFilterAdapter(
                    it as AppCompatActivity,
                    deliveryStatuses,
                    this
                )
            }!!
        listDeliveryStatus.adapter = deliveryStatusFilterAdapter
//        } else{
//            productCategoryAdapter!!.offers = orderStatuses
//            productCategoryAdapter!!.notifyDataSetChanged()
//        }
    }

    private fun setEnableDisableClearAll() {
        if (data != null) {
            if (Utility.isValueNull(data.get(UseCaseConstants.SORT_BY) as String) &&
                Utility.isValueNull(data.get(UseCaseConstants.ORDER_STATUS) as String) &&
                Utility.isValueNull(data.get(UseCaseConstants.DELIVERY_STATUS) as String) &&
                Utility.isValueNull(data.get(UseCaseConstants.ADDRESS) as String) &&
                (Utility.isValueNull(data.get(UseCaseConstants.PRICE_MIN) as String) || data.get(UseCaseConstants.PRICE_MIN).equals(
                    "0"
                )) &&
                (Utility.isValueNull(data.get(UseCaseConstants.PRICE_MAX) as String) || data.get(UseCaseConstants.PRICE_MAX).equals(
                    sharedPrefs.order_max_price
                ))
            ) {
                txtClearAll.isEnabled = false
                txtClearAll.alpha = 0.5f
            } else {
                txtClearAll.isEnabled = true
                txtClearAll.alpha = 1f
                txtClearAll.setOnClickListener(this)
            }
        }
    }

    private fun isANyFilterApplied(): Boolean {

        if (data != null) {
            return !(Utility.isValueNull(data.get(UseCaseConstants.SORT_BY) as String) &&
                    Utility.isValueNull(data.get(UseCaseConstants.ORDER_STATUS) as String) &&
                    Utility.isValueNull(data.get(UseCaseConstants.DELIVERY_STATUS) as String) &&
                    Utility.isValueNull(data.get(UseCaseConstants.ADDRESS) as String) &&
                    (Utility.isValueNull(data.get(UseCaseConstants.PRICE_MIN) as String) || data.get(UseCaseConstants.PRICE_MIN).equals(
                        "0"
                    )) &&
                    (Utility.isValueNull(data.get(UseCaseConstants.PRICE_MAX) as String) || data.get(UseCaseConstants.PRICE_MAX).equals(
                        sharedPrefs.order_max_price
                    )))
        }
        return false
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnApply, R.id.viewApply -> {
                orderViewModel!!.filterAvailable = true
                orderViewModel!!.isfilterApplied = true
                orderViewModel?.data?.put(
                    UseCaseConstants.ORDER_STATUS,
                    data.get(UseCaseConstants.ORDER_STATUS)!!
                )
                orderViewModel?.data?.put(
                    UseCaseConstants.DELIVERY_STATUS,
                    data.get(UseCaseConstants.DELIVERY_STATUS)!!
                )
                orderViewModel?.data?.put(UseCaseConstants.PRICE_MIN, data.get(UseCaseConstants.PRICE_MIN)!!)
                orderViewModel?.data?.put(UseCaseConstants.PRICE_MAX, data.get(UseCaseConstants.PRICE_MAX)!!)
                orderViewModel?.data?.put(UseCaseConstants.ADDRESS, data.get(UseCaseConstants.ADDRESS)!!)
                orderViewModel?.data?.put(UseCaseConstants.SORT_BY, data.get(UseCaseConstants.SORT_BY)!!)


                orderViewModel?.numberOfFilters = sortingFilter + orderStastusFilters + deliveryStastusFilters + priceFilter

                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }
            R.id.txtClearAll -> {
                orderViewModel!!.filterAvailable = true
                orderViewModel!!.isfilterApplied = true
                if (isANyFilterApplied()) {
                    (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                        title = getString(R.string.app_name),
                        message = getString(R.string.clear_filter_confirmation_message),
                        positiveButton = getString(android.R.string.yes),
                        nagetiveButton = getString(android.R.string.cancel),
                        onDialogButtonClick = object : OnDialogButtonClick {
                            override fun onPositiveButtonClick() {
                                orderViewModel?.data?.put(UseCaseConstants.ORDER_STATUS, "")
                                orderViewModel?.data?.put(UseCaseConstants.DELIVERY_STATUS, "")
                                orderViewModel?.data?.put(UseCaseConstants.PRICE_MIN, "")
                                orderViewModel?.data?.put(UseCaseConstants.PRICE_MAX, "")
                                orderViewModel?.data?.put(UseCaseConstants.ADDRESS, "")
                                orderViewModel?.data?.put(UseCaseConstants.SORT_BY, "")
                                for (i in 0 until orderStatuses.size) {
                                    orderStatuses[i].checked = false
                                }

                                orderViewModel?._orderStatusLiveData?.value = orderStatuses
                                orderViewModel?.title = getString(R.string.shop_all)

                                orderViewModel?.numberOfFilters = 0
                                (requireActivity() as AppCompatActivity).hideKeyboard()
                                requireActivity().supportFragmentManager.popBackStack()
                            }

                            override fun onNegativeButtonClick() {
                            }
                        })
                } else {
                    (requireActivity() as AppCompatActivity).openAlertDialogWithOneClick(
                        title = getString(R.string.app_name),
                        message = getString(R.string.no_filter_message),
                        positiveButton = getString(android.R.string.ok),
                        onDialogButtonClick = object : OnDialogButtonClick {
                            override fun onPositiveButtonClick() {
                            }

                            override fun onNegativeButtonClick() {
                            }
                        })
                }
            }

            R.id.imgClose -> {
//                orderViewModel?.title = title
                orderViewModel?.numberOfFilters = numberOfFilters
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }
}
