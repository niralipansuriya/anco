package com.app.ancoturf.presentation.home.products.filter

import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.product.remote.entity.response.ProductCategoryData
import com.app.ancoturf.data.product.remote.entity.response.ProductSortByCategories
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.openAlertDialogWithOneClick
import com.app.ancoturf.extension.openAlertDialogWithTwoClick
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.products.filter.adapters.ProductCategoryFilterAdapter
import com.app.ancoturf.presentation.home.products.filter.adapters.SpinnerSortByAdapter
import com.app.ancoturf.presentation.home.products.ProductsViewModel
import com.app.ancoturf.presentation.home.shop.ShopViewModel
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.utils.Utility
import com.app.ancoturf.utils.interfces.OnDialogButtonClick
import com.appyvet.materialrangebar.RangeBar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_product_filter.*
import kotlinx.android.synthetic.main.header_filter.*
import java.util.*
import javax.inject.Inject

class ProductFilterFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private var productsViewModel: ProductsViewModel? = null
    private var shopViewModel: ShopViewModel? = null

    var productCategoryFilterAdapter: ProductCategoryFilterAdapter? = null
    var productCategories: ArrayList<ProductCategoryData> = ArrayList()

    private var spinnerSortByAdapter: SpinnerSortByAdapter? = null
    var sortByCategories: List<ProductSortByCategories> = arrayOf(
        ProductSortByCategories("Default Sorting", ""),
        ProductSortByCategories(
            "Sort by popularity",
            "popularity"
        ),
        ProductSortByCategories(
            "Sort by Price - high to low",
            "price-desc"
        ),
        ProductSortByCategories(
            "Sort by Price - low to high",
            "price"
        ),
        ProductSortByCategories("Sort by average rating", "rating")
    ).toList()

    val data = mutableMapOf(
        UseCaseConstants.PRODUCT_CATEGORY_IDS to "",
        UseCaseConstants.PRODUCT_TAG_IDS to "",
        UseCaseConstants.PRICE_MIN to "",
        UseCaseConstants.PRICE_MAX to "",
        UseCaseConstants.SEARCH to "",
        UseCaseConstants.SORT_BY to ""
    )

    var title: String? = "Shop All"
    var numberOfFilters: Int? = 0
    var priceFilter = 0
    var categoryFilters = 0
    var tagsFilters = 0
    var sortingFilter = 0

        override fun getContentResource(): Int = R.layout.fragment_product_filter

    override fun viewModelSetup() {
        shopViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)[ShopViewModel::class.java]
        productsViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)[ProductsViewModel::class.java]
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

        setData()

        btnApply.setOnClickListener(this)
        viewApply.setOnClickListener(this)

        txtClearAll.setOnClickListener(this)
        imgClose.setOnClickListener(this)
    }

    private fun setData() {
        data.put(
            UseCaseConstants.PRODUCT_CATEGORY_IDS,
            productsViewModel?.data?.get(UseCaseConstants.PRODUCT_CATEGORY_IDS)!!
        )
        data.put(
            UseCaseConstants.PRODUCT_TAG_IDS,
            productsViewModel?.data?.get(UseCaseConstants.PRODUCT_TAG_IDS)!!
        )
        data.put(UseCaseConstants.PRICE_MIN, productsViewModel?.data?.get(UseCaseConstants.PRICE_MIN)!!)
        data.put(UseCaseConstants.PRICE_MAX, productsViewModel?.data?.get(UseCaseConstants.PRICE_MAX)!!)
        data.put(UseCaseConstants.SEARCH, productsViewModel?.data?.get(UseCaseConstants.SEARCH)!!)
        data.put(UseCaseConstants.SORT_BY, productsViewModel?.data?.get(UseCaseConstants.SORT_BY)!!)

        title = productsViewModel?.title
        numberOfFilters = productsViewModel?.numberOfFilters

        //set tag values
        addTags()

        if((Utility.isValueNull(data.get(UseCaseConstants.PRICE_MIN) as String) || data.get(UseCaseConstants.PRICE_MIN).equals("0")) &&
            (Utility.isValueNull(data.get(UseCaseConstants.PRICE_MAX) as String) || data.get(UseCaseConstants.PRICE_MAX).equals(sharedPrefs.product_max_price))) {
            priceFilter = 0
        } else {
            priceFilter = 1
        }

        setEnableDisableClearAll()

        //set category values
        productCategories = shopViewModel?.productCategoryLiveData?.value!!
        setCategoryAdapter()

        //set sort by values
        spinnerSortByAdapter =
            SpinnerSortByAdapter(requireActivity(), sortByCategories)
        spinnerSortBy.adapter = spinnerSortByAdapter
        productsViewModel?.data?.get(UseCaseConstants.SORT_BY)
            ?.let { if (getSelectedItemPosition() != -1) spinnerSortBy.setSelection(getSelectedItemPosition()) }

        if (getSelectedItemPosition() == -1 || getSelectedItemPosition() == 0) sortingFilter = 0 else sortingFilter = 1

        spinnerSortBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            //Performing action onItemSelected and onNothing selected
            override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
//                productsViewModel?.data?.put(UseCaseConstants.SORT_BY, sortByCategories.get(position).slug)
                data?.put(UseCaseConstants.SORT_BY, sortByCategories.get(position).slug)
                if (position == -1 || position == 0) sortingFilter = 0 else sortingFilter = 1
                setEnableDisableClearAll()
            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        //set price values
        seekMinMax.tickEnd = sharedPrefs.product_max_price.toFloat()
        seekMinMax.tickStart = 0f
        if (!productsViewModel?.data?.get(UseCaseConstants.PRICE_MAX).equals("") && !productsViewModel?.data?.get(
                UseCaseConstants.PRICE_MIN
            ).equals("")
        ) {
            seekMinMax.setRangePinsByValue(
                productsViewModel?.data?.get(UseCaseConstants.PRICE_MIN)!!.toFloat(),
                productsViewModel?.data?.get(UseCaseConstants.PRICE_MAX)!!.toFloat()
            )
        } else {
            if (!productsViewModel?.data?.get(UseCaseConstants.PRICE_MAX).equals("")) {
                seekMinMax.setRangePinsByValue(0f, productsViewModel?.data?.get(UseCaseConstants.PRICE_MAX)!!.toFloat())
            } else if (!productsViewModel?.data?.get(UseCaseConstants.PRICE_MIN).equals("")) {
                seekMinMax.setRangePinsByValue(
                    productsViewModel?.data?.get(UseCaseConstants.PRICE_MIN)!!.toFloat(),
                    sharedPrefs.product_max_price.toFloat()
                )
            } else {
                seekMinMax.setRangePinsByValue(0f, sharedPrefs.product_max_price.toFloat())
            }
        }
        if (!productsViewModel?.data?.get(UseCaseConstants.PRICE_MIN).equals("")) {
            productsViewModel?.data?.get(UseCaseConstants.PRICE_MIN)
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
                // productsViewModel?.data?.put(UseCaseConstants.PRICE_MIN, minValue.toString())
                leftPinValue?.let {
                    data?.put(UseCaseConstants.PRICE_MIN, it)
                    setEnableDisableClearAll()
                }
                // productsViewModel?.data?.put(UseCaseConstants.PRICE_MAX, maxValue.toString())
                rightPinValue?.let {
                    data?.put(UseCaseConstants.PRICE_MAX, it)
                    setEnableDisableClearAll()
                }

                if((Utility.isValueNull(data.get(UseCaseConstants.PRICE_MIN) as String) || data.get(UseCaseConstants.PRICE_MIN).equals("0")) &&
                    (Utility.isValueNull(data.get(UseCaseConstants.PRICE_MAX) as String) || data.get(UseCaseConstants.PRICE_MAX).equals(sharedPrefs.product_max_price))) {
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
        var selectedItem = productsViewModel?.data?.get(UseCaseConstants.SORT_BY)
        for (i in 0..(sortByCategories.size - 1)) {
            var productSortByCategories: ProductSortByCategories = sortByCategories.get(i)
            if (productSortByCategories.slug.equals(selectedItem))
                return i
        }
        return selectedPosition
    }

    private fun addTags() {
        tagsFilters = 0
        if (productsViewModel?._tagsLiveData?.value != null) {
            var tags: ArrayList<SettingsResponse.Data.Tag> = productsViewModel?._tagsLiveData?.value!!
            for (i in 0..(tags.size - 1)) {
                val tag: SettingsResponse.Data.Tag = tags.get(i)
                val tagView: View = LayoutInflater.from(activity).inflate(R.layout.item_tags, null)
                val txtTag: TextView = tagView.findViewById(R.id.txtTag)
                txtTag.text = tag.displayName
                txtTag.setBackgroundResource(if (tag.selected) R.drawable.bg_green_rounded else R.drawable.bg_green_line_rounded)
                if(tag.selected)
                    tagsFilters = tagsFilters + 1
                txtTag.setTextColor(
                    if (tag.selected) ContextCompat.getColor(
                        requireActivity(),
                        android.R.color.white
                    ) else ContextCompat.getColor(requireActivity(), R.color.theme_green)
                )
                txtTag.setOnClickListener {
                    tag.selected = !tag.selected
                    tagsFilters = if(tag.selected) tagsFilters + 1 else tagsFilters - 1
                    txtTag.setBackgroundResource(if (tag.selected) R.drawable.bg_green_rounded else R.drawable.bg_green_line_rounded)
                    txtTag.setTextColor(
                        if (tag.selected) ContextCompat.getColor(
                            requireActivity(),
                            android.R.color.white
                        ) else ContextCompat.getColor(requireActivity(), R.color.theme_green)
                    )
                    var position = tags.indexOf(tag)
                    tags.set(position, tag)
                    //                productsViewModel?.data?.put(UseCaseConstants.PRODUCT_TAG_IDS, getTagIds(tags))
                    data?.put(UseCaseConstants.PRODUCT_TAG_IDS, getTagIds(tags))
                    setEnableDisableClearAll()
                }
                linTags.addView(tagView)
            }
        }
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

    fun setSelectedCategories(categories: ArrayList<ProductCategoryData>) {
//        productsViewModel?.data?.put(UseCaseConstants.PRODUCT_CATEGORY_IDS, getCategoryIds(categories))
        data?.put(UseCaseConstants.PRODUCT_CATEGORY_IDS, getCategoryIds(categories))
        setEnableDisableClearAll()
    }

    private fun getCategoryIds(categories: ArrayList<ProductCategoryData>): String {
        var anyChecked = false
        categoryFilters = 0
        val selectedCategories = StringBuilder()
        if (categories != null && categories.size > 0) {
            for (i in 0..(categories.size - 1)) {
                if (categories.get(i).checked) {
                    anyChecked = true
                    if (Utility.isValueNull(selectedCategories.toString())) {
                        selectedCategories.append(categories.get(i).id)
                        title = categories.get(i).displayName
                    } else {
                        selectedCategories.append("," + categories.get(i).id)
                        title = getString(R.string.shop_all)
                    }
                    categoryFilters = categoryFilters + 1
                }
            }
        } else {
            title = getString(R.string.shop_all)
        }
        if (!anyChecked)
            title = getString(R.string.shop_all)
        return selectedCategories.toString()
    }

    private fun setCategoryAdapter() {
//        if(productCategoryAdapter == null) {
        productCategoryFilterAdapter =
            activity?.let {
                ProductCategoryFilterAdapter(
                    it as AppCompatActivity,
                    productCategories,
                    this
                )
            }!!
        listCategories.adapter = productCategoryFilterAdapter
//        } else{
//            productCategoryAdapter!!.offers = orderStatuses
//            productCategoryAdapter!!.notifyDataSetChanged()
//        }
    }

    private fun setEnableDisableClearAll() {

        if (data != null) {
            if (Utility.isValueNull(data.get(UseCaseConstants.SORT_BY) as String) &&
                Utility.isValueNull(data.get(UseCaseConstants.PRODUCT_CATEGORY_IDS) as String) &&
                Utility.isValueNull(data.get(UseCaseConstants.SEARCH) as String) &&
                Utility.isValueNull(data.get(UseCaseConstants.PRODUCT_TAG_IDS) as String) &&
                (Utility.isValueNull(data.get(UseCaseConstants.PRICE_MIN) as String) || data.get(UseCaseConstants.PRICE_MIN).equals("0")) &&
                (Utility.isValueNull(data.get(UseCaseConstants.PRICE_MAX) as String) || data.get(UseCaseConstants.PRICE_MAX).equals(sharedPrefs.product_max_price))
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
            if (Utility.isValueNull(data.get(UseCaseConstants.SORT_BY) as String) &&
                Utility.isValueNull(data.get(UseCaseConstants.PRODUCT_CATEGORY_IDS) as String) &&
                Utility.isValueNull(data.get(UseCaseConstants.SEARCH) as String) &&
                Utility.isValueNull(data.get(UseCaseConstants.PRODUCT_TAG_IDS) as String) &&
                (Utility.isValueNull(data.get(UseCaseConstants.PRICE_MIN) as String) || data.get(UseCaseConstants.PRICE_MIN).equals("0")) &&
                (Utility.isValueNull(data.get(UseCaseConstants.PRICE_MAX) as String) || data.get(UseCaseConstants.PRICE_MAX).equals(sharedPrefs.product_max_price))
            ) {
                return false
            } else {
                return true
            }
        }
        return false
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnApply, R.id.viewApply -> {
                productsViewModel!!.filterAvailable = true
                productsViewModel?.data?.put(
                    UseCaseConstants.PRODUCT_CATEGORY_IDS,
                    data.get(UseCaseConstants.PRODUCT_CATEGORY_IDS)!!
                )
                productsViewModel?.data?.put(
                    UseCaseConstants.PRODUCT_TAG_IDS,
                    data.get(UseCaseConstants.PRODUCT_TAG_IDS)!!
                )
                productsViewModel?.data?.put(UseCaseConstants.PRICE_MIN, data.get(UseCaseConstants.PRICE_MIN)!!)
                productsViewModel?.data?.put(UseCaseConstants.PRICE_MAX, data.get(UseCaseConstants.PRICE_MAX)!!)
                productsViewModel?.data?.put(UseCaseConstants.SEARCH, data.get(UseCaseConstants.SEARCH)!!)
                productsViewModel?.data?.put(UseCaseConstants.SORT_BY, data.get(UseCaseConstants.SORT_BY)!!)

                productsViewModel?.title = title
                productsViewModel?.numberOfFilters = sortingFilter + tagsFilters + categoryFilters + priceFilter

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
                                productsViewModel?.data?.put(UseCaseConstants.PRODUCT_CATEGORY_IDS, "")
                                productsViewModel?.data?.put(UseCaseConstants.PRODUCT_TAG_IDS, "")
                                productsViewModel?.data?.put(UseCaseConstants.PRICE_MIN, "")
                                productsViewModel?.data?.put(UseCaseConstants.PRICE_MAX, "")
                                productsViewModel?.data?.put(UseCaseConstants.SEARCH, "")
                                productsViewModel?.data?.put(UseCaseConstants.SORT_BY, "")
                                if(!Utility.isValueNull(sharedPrefs.tags)) {
                                    productsViewModel?._tagsLiveData?.value = Gson().fromJson(
                                        sharedPrefs.tags,
                                        Array<SettingsResponse.Data.Tag>::class.java
                                    ).toList() as ArrayList<SettingsResponse.Data.Tag>
                                }
                                for (i in 0 until productCategories.size) {
                                    productCategories[i].checked = false
                                }
                                shopViewModel?._productCategoryLiveData?.value = productCategories

                                productsViewModel?.title = getString(R.string.shop_all)
                                productsViewModel?.numberOfFilters = 0

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
                productsViewModel?.title = title
                productsViewModel?.numberOfFilters = numberOfFilters
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
    }
}
