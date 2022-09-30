package com.app.ancoturf.presentation.home.quote.filter

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
import com.app.ancoturf.data.quote.remote.entity.QuoteStatus
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.openAlertDialogWithOneClick
import com.app.ancoturf.extension.openAlertDialogWithTwoClick
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.products.filter.adapters.SpinnerSortByAdapter
import com.app.ancoturf.presentation.home.quote.QuoteViewModel
import com.app.ancoturf.presentation.home.quote.filter.adapter.QuoteStatusFilterAdapter
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.utils.Utility
import com.app.ancoturf.utils.interfces.OnDialogButtonClick
import com.appyvet.materialrangebar.RangeBar
import kotlinx.android.synthetic.main.fragment_product_filter.btnApply
import kotlinx.android.synthetic.main.fragment_product_filter.listCategories
import kotlinx.android.synthetic.main.fragment_product_filter.seekMinMax
import kotlinx.android.synthetic.main.fragment_product_filter.spinnerSortBy
import kotlinx.android.synthetic.main.fragment_product_filter.txtClearAll
import kotlinx.android.synthetic.main.fragment_product_filter.viewApply
import kotlinx.android.synthetic.main.fragment_quote_filter.*
import kotlinx.android.synthetic.main.header_filter.*
import java.util.*
import javax.inject.Inject

class   QuoteFilterFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private var quoteViewModel: QuoteViewModel? = null

    var quoteStatusFilterAdapter: QuoteStatusFilterAdapter? = null
    var statuses: ArrayList<QuoteStatus> = ArrayList()

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
            "Sort by date",
            "date"
        ),
        ProductSortByCategories("Sort by status", "status")
    ).toList()

    val data = mutableMapOf(
        UseCaseConstants.STATUS to "",
        UseCaseConstants.COST_MIN to "",
        UseCaseConstants.COST_MAX to "",
        UseCaseConstants.ADDRESS to "",
        UseCaseConstants.SORT_BY to ""
    )

    var title: String? = "Shop All"
    var priceFilter = 0
    var categoryFilters = 0
    var sortingFilter = 0

        override fun getContentResource(): Int = R.layout.fragment_quote_filter

    override fun viewModelSetup() {
        quoteViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)[QuoteViewModel::class.java]
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

        statuses.add(QuoteStatus("Draft"))
        statuses.add(QuoteStatus("Pending"))
        statuses.add(QuoteStatus("Sent"))

        setData()

        btnApply.setOnClickListener(this)
        viewApply.setOnClickListener(this)

        txtClearAll.setOnClickListener(this)
        imgClose.setOnClickListener(this)
    }

    private fun setData() {
        data.put(
            UseCaseConstants.STATUS,
            quoteViewModel?.data?.get(UseCaseConstants.STATUS)!!
        )
        data.put(UseCaseConstants.COST_MIN, quoteViewModel?.data?.get(UseCaseConstants.COST_MIN)!!)
        data.put(UseCaseConstants.COST_MAX, quoteViewModel?.data?.get(UseCaseConstants.COST_MAX)!!)
        data.put(UseCaseConstants.ADDRESS, quoteViewModel?.data?.get(UseCaseConstants.ADDRESS)!!)
        data.put(UseCaseConstants.SORT_BY, quoteViewModel?.data?.get(UseCaseConstants.SORT_BY)!!)

//        title = quoteViewModel?.title
//        numberOfFilters = quoteViewModel?.numberOfFilters

        setEnableDisableClearAll()

        priceFilter = if((Utility.isValueNull(data.get(UseCaseConstants.COST_MIN) as String) || data.get(UseCaseConstants.COST_MIN).equals("0")) &&
            (Utility.isValueNull(data.get(UseCaseConstants.COST_MAX) as String) || data.get(UseCaseConstants.COST_MAX).equals(sharedPrefs.quote_max_price))) {
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
        setStatusAdapter()

        //set sort by values
        spinnerSortByAdapter =
            SpinnerSortByAdapter(requireActivity(), sortByCategories)
        spinnerSortBy.setAdapter(spinnerSortByAdapter)
        quoteViewModel?.data?.get(UseCaseConstants.SORT_BY)
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

        seekMinMax.setTickInterval((sharedPrefs.quote_max_price.toFloat() / 15))
        //set price values
        seekMinMax.tickEnd = sharedPrefs.quote_max_price.toFloat()
        seekMinMax.tickStart = 0f


        if (!quoteViewModel?.data?.get(UseCaseConstants.COST_MAX).equals("") && !quoteViewModel?.data?.get(
                UseCaseConstants.COST_MIN
            ).equals("")
        ) {
            seekMinMax.setRangePinsByValue(
                quoteViewModel?.data?.get(UseCaseConstants.COST_MIN)!!.toFloat(),
                quoteViewModel?.data?.get(UseCaseConstants.COST_MAX)!!.toFloat()
            )
        } else {
            if (!quoteViewModel?.data?.get(UseCaseConstants.COST_MAX).equals("")) {
                seekMinMax.setRangePinsByValue(0f, quoteViewModel?.data?.get(UseCaseConstants.COST_MAX)!!.toFloat())
            } else if (!quoteViewModel?.data?.get(UseCaseConstants.COST_MIN).equals("")) {
                seekMinMax.setRangePinsByValue(
                    quoteViewModel?.data?.get(UseCaseConstants.COST_MIN)!!.toFloat(),
                    sharedPrefs.quote_max_price.toFloat()
                )
            } else {
                seekMinMax.setRangePinsByValue(0f, sharedPrefs.quote_max_price.toFloat())
            }
        }
        if (!quoteViewModel?.data?.get(UseCaseConstants.COST_MIN).equals("")) {
            quoteViewModel?.data?.get(UseCaseConstants.COST_MIN)
                ?.let { seekMinMax.left = it.toFloat().toInt() }
        }

        seekMinMax.setOnRangeBarChangeListener(object : RangeBar.OnRangeBarChangeListener {
            override fun onRangeChangeListener(
                rangeBar: RangeBar?,
                leftPinIndex: Int,
                rightPinIndex: Int,
                leftPinValue: String?,
                rightPinValue: String?
            ) {
                // quoteViewModel?.data?.put(UseCaseConstants.PRICE_MIN, minValue.toString())
                leftPinValue?.let {
                    data?.put(UseCaseConstants.COST_MIN, it)
                    setEnableDisableClearAll()
                }
                // quoteViewModel?.data?.put(UseCaseConstants.PRICE_MAX, maxValue.toString())
                rightPinValue?.let {
                    data?.put(UseCaseConstants.COST_MAX, it)
                    setEnableDisableClearAll()
                }

                if((Utility.isValueNull(data.get(UseCaseConstants.COST_MIN) as String) || data.get(UseCaseConstants.COST_MIN).equals("0")) &&
                    (Utility.isValueNull(data.get(UseCaseConstants.COST_MAX) as String) || data.get(UseCaseConstants.COST_MAX).equals(sharedPrefs.quote_max_price))) {
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
        var selectedItem = quoteViewModel?.data?.get(UseCaseConstants.SORT_BY)
        for (i in 0..(sortByCategories.size - 1)) {
            var productSortByCategories: ProductSortByCategories = sortByCategories.get(i)
            if (productSortByCategories.slug.equals(selectedItem))
                return i
        }
        return selectedPosition
    }

    fun setSelectedStatus(status: ArrayList<QuoteStatus>) {
        data?.put(UseCaseConstants.STATUS, getStatusNames(status))
        setEnableDisableClearAll()
    }


    private fun getTagIds(tags: ArrayList<SettingsResponse.Data.Tag>): String {
        val selectedTags = StringBuilder()
        for (i in 0..(tags.size - 1)) {
            if (tags.get(i).selected) {
                if (Utility.isValueNull(selectedTags.toString()))
                    selectedTags.append(tags.get(i).id)
                else
                    selectedTags.append("," + tags.get(i).id)
            }
        }
        return selectedTags.toString()
    }


    private fun getStatusNames(status: ArrayList<QuoteStatus>): String {
//        var anyChecked = false
        categoryFilters = 0
        val selectedCategories = StringBuilder()
        if (status != null && status.size > 0) {
            for (i in 0..(status.size - 1)) {
                if (status.get(i).checked) {
//                    anyChecked = true
                    if (Utility.isValueNull(selectedCategories.toString())) {
                        selectedCategories.append(status.get(i).displayName)
//                        title = categories.get(i).displayName
                    } else {
                        selectedCategories.append("," + status.get(i).displayName)
//                        title = getString(R.string.shop_all)
                    }
                    categoryFilters = categoryFilters + 1
                }
            }
        } else {
//            title = getString(R.string.shop_all)
        }
//        if (!anyChecked)
//            title = getString(R.string.shop_all)
        return selectedCategories.toString()
    }

    private fun setStatusAdapter() {
//        if(productCategoryAdapter == null) {
        var status = data.get(UseCaseConstants.STATUS) as String
        var statusArray = status.split(",")
        for (i in 0 until statuses.size) {
            for (j in 0 until statusArray.size) {
                if(statuses[i].displayName.equals(statusArray[j])) {
                    statuses[i].checked = true
                    break
                }
            }
        }

        quoteStatusFilterAdapter =
            activity?.let {
                QuoteStatusFilterAdapter(
                    it as AppCompatActivity,
                    statuses,
                    this
                )
            }!!
        listCategories.adapter = quoteStatusFilterAdapter
//        } else{
//            productCategoryAdapter!!.offers = orderStatuses
//            productCategoryAdapter!!.notifyDataSetChanged()
//        }
    }

    private fun setEnableDisableClearAll() {

        if (data != null) {
            if (Utility.isValueNull(data.get(UseCaseConstants.SORT_BY) as String) &&
                Utility.isValueNull(data.get(UseCaseConstants.STATUS) as String) &&
                Utility.isValueNull(data.get(UseCaseConstants.ADDRESS) as String) &&
                (Utility.isValueNull(data.get(UseCaseConstants.COST_MIN) as String) || data.get(UseCaseConstants.COST_MIN).equals("0")) &&
                (Utility.isValueNull(data.get(UseCaseConstants.COST_MAX) as String) || data.get(UseCaseConstants.COST_MAX).equals(sharedPrefs.quote_max_price))
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

    private fun isANyFilterApplied() : Boolean{

        if (data != null) {
            return !(Utility.isValueNull(data.get(UseCaseConstants.SORT_BY) as String) &&
                    Utility.isValueNull(data.get(UseCaseConstants.STATUS) as String) &&
                    Utility.isValueNull(data.get(UseCaseConstants.ADDRESS) as String) &&
                    (Utility.isValueNull(data.get(UseCaseConstants.COST_MIN) as String) || data.get(UseCaseConstants.COST_MIN).equals("0")) &&
                    (Utility.isValueNull(data.get(UseCaseConstants.COST_MAX) as String) || data.get(UseCaseConstants.COST_MAX).equals(sharedPrefs.quote_max_price)))
        }
        return false
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnApply, R.id.viewApply -> {
               quoteViewModel!!.filterAvailable = true
                quoteViewModel?.data?.put(
                    UseCaseConstants.STATUS,
                    data.get(UseCaseConstants.STATUS)!!
                )
                quoteViewModel?.data?.put(UseCaseConstants.COST_MIN, data.get(UseCaseConstants.COST_MIN)!!)
                quoteViewModel?.data?.put(UseCaseConstants.COST_MAX, data.get(UseCaseConstants.COST_MAX)!!)
                quoteViewModel?.data?.put(UseCaseConstants.ADDRESS, data.get(UseCaseConstants.ADDRESS)!!)
                quoteViewModel?.data?.put(UseCaseConstants.SORT_BY, data.get(UseCaseConstants.SORT_BY)!!)

             //   quoteViewModel?.title = title
             //   quoteViewModel?.numberOfFilters = sortingFilter + tagsFilters + categoryFilters + priceFilter

                (requireActivity() as AppCompatActivity).hideKeyboard()
                    requireActivity().supportFragmentManager.popBackStack()
            }
            R.id.txtClearAll -> {
                if(isANyFilterApplied()) {
                    (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                        title = getString(R.string.app_name),
                        message = getString(R.string.clear_filter_confirmation_message),
                        positiveButton = getString(android.R.string.yes),
                        nagetiveButton = getString(android.R.string.cancel),
                        onDialogButtonClick = object : OnDialogButtonClick {
                            override fun onPositiveButtonClick() {
                                quoteViewModel?.data?.put(UseCaseConstants.STATUS, "")
                                quoteViewModel?.data?.put(UseCaseConstants.COST_MIN, "")
                                quoteViewModel?.data?.put(UseCaseConstants.COST_MAX, "")
                                quoteViewModel?.data?.put(UseCaseConstants.ADDRESS, "")
                                quoteViewModel?.data?.put(UseCaseConstants.SORT_BY, "")
                                for (i in 0..(statuses.size - 1)) {
                                    statuses.get(i).checked = false
                                }
//                quoteViewModel!!.filterAvailable = false
//                            setData()

//                                quoteViewModel?.title = getString(R.string.shop_all)
//                                quoteViewModel?.numberOfFilters = 0
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
//                quoteViewModel?.title = title
//                quoteViewModel?.numberOfFilters = numberOfFilters
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }
}
