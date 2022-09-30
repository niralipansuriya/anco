package com.app.ancoturf.presentation.home.portfolio

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.openLogoutAlertDialog
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.portfolio.adapters.PortfolioProductsAdapter
import com.app.ancoturf.presentation.home.portfolio.interfaces.OnProductSelectedListener
import com.app.ancoturf.presentation.home.products.ProductsViewModel
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.android.synthetic.main.fragment_shop.listProducts
import kotlinx.android.synthetic.main.header.*
import java.util.ArrayList
import javax.inject.Inject

class PortfolioProductsFragment(
    private val categoryIds: String,
    private val title: String
) :
    BaseFragment(),
    View.OnClickListener, OnProductSelectedListener {

    private var addedProducts = ArrayList<ProductsResponse.Data>()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var productsViewModel: ProductsViewModel? = null
    private var portfolioViewModel: PortfolioViewModel? = null

    var productsAdapter: PortfolioProductsAdapter? = null
    var products: ArrayList<ProductsResponse.Data> = ArrayList()

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun getContentResource(): Int = R.layout.fragment_products

    override fun viewModelSetup() {
        portfolioViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)[PortfolioViewModel::class.java]
        productsViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)[ProductsViewModel::class.java]
        initObservers()
    }

    override fun viewSetup() {
        if(activity is HomeActivity) {
            (activity as HomeActivity).hideShowFooter(true)
            (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, false)
        } else if(activity is ProfileActivity) {
            (activity as ProfileActivity).hideShowFooter(true)
            (activity as ProfileActivity).showCartDetails(imgCart, txtCartProducts, false)
        } else if(activity is PaymentActivity) {
            (activity as PaymentActivity).hideShowFooter(true)
            (activity as PaymentActivity).showCartDetails(imgCart, txtCartProducts, false)
        }

        txtAddProduct.visibility = View.VISIBLE
        Utility.showProgress(context = requireActivity(), message = "", cancellable = false)
        productsViewModel!!.data.put(UseCaseConstants.PRODUCT_CATEGORY_IDS, categoryIds)
        if (!Utility.isValueNull(categoryIds))
            setCategoryIdChecked()
        productsViewModel!!.filterAvailable = true
        productsViewModel!!.callGetProducts("")
        imgFilter.visibility = View.GONE
        txtClearFilters.visibility = View.GONE
        txtNumberOfFilters.visibility = View.GONE
        imgBack.setOnClickListener(this@PortfolioProductsFragment)
        txtAddProduct.setOnClickListener(this@PortfolioProductsFragment)
        imgLogo.setOnClickListener(this@PortfolioProductsFragment)
        imgMore.setOnClickListener(this@PortfolioProductsFragment)
        imgSearch.setOnClickListener(this@PortfolioProductsFragment)
        imgCart.setOnClickListener(this@PortfolioProductsFragment)
        txtTitle.text = getString(R.string.portfolio_product_title)

        if(portfolioViewModel?.ancoProductLiveData?.value != null && portfolioViewModel?.ancoProductLiveData?.value!!.size > 0) {
            addedProducts.addAll(portfolioViewModel?.ancoProductLiveData?.value!!)
        }

    }

    private fun setCategoryIdChecked() {
        if (portfolioViewModel?.productCategoryLiveData?.value != null) {
            var productCategories = portfolioViewModel?.productCategoryLiveData?.value!!
            for (i in 0..(productCategories.size - 1)) {
                if (productCategories.get(i).id == categoryIds.toInt()) {
                    productCategories.get(i).checked = true
                }
            }
        }
    }

    private fun initObservers() {
        productsViewModel!!.productsLiveData.observe(this, Observer {
            if(it != null) {
                if (it.success) {
                    Log.e("resp", it.toString())
                    if (it.data != null) {
                        if (products == null)
                            products = ArrayList()
                        products.clear()
                        products.addAll(it.data!!.data)
                        removeSelectedProducts()
                        setAdapter()
                    }
                } else {
                    shortToast(it!!.message)
                }
            }
        })

        productsViewModel!!.errorLiveData.observe(this, Observer {
            if (!Utility.isValueNull(productsViewModel!!.errorLiveData.value)) {
                if (it.equals(ErrorConstants.UNAUTHORIZED_ERROR_CODE)) {
                    if(activity is HomeActivity) {
                        (activity as HomeActivity).cartViewModel?.deleteProduct(null)
                        (activity as HomeActivity).cartViewModel?.deleteCoupon(null)
                    } else if(activity is PaymentActivity) {
                        (activity as PaymentActivity).cartViewModel?.deleteProduct(null)
                        (activity as PaymentActivity).cartViewModel?.deleteCoupon(null)
                    }  else if(activity is ProfileActivity) {
                        (activity as ProfileActivity).cartViewModel?.deleteProduct(null)
                        (activity as ProfileActivity).cartViewModel?.deleteCoupon(null)
                    }
                    (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                } else {
                    it?.let { it1 ->
                        shortToast(it1)
                        if (products != null)
                            products.clear()
                        setAdapter()
                    }
                }
                productsViewModel!!._errorLiveData.value = null
            }
        })
    }

    private fun removeSelectedProducts() {
        val products = ArrayList<ProductsResponse.Data>()
        products.addAll(this.products)
        var portfolioDetail = portfolioViewModel?.portfolioDetailLiveData?.value
        if (portfolioDetail != null && portfolioDetail.products != null && portfolioDetail.products.size > 0) {
            for (i in 0 until portfolioDetail.products.size) {
                var selctedProductID = portfolioDetail.products[i].id
                for (j in 0 until products.size) {
                    if (products[j].productId == selctedProductID) {
//                        this.products.remove(products[j])
                        this.products[j].selected = true
                    }
                }
            }
        }

        var ancoProduct = portfolioViewModel?.ancoProductLiveData?.value
        if (ancoProduct != null && ancoProduct.size > 0) {
            for (i in 0 until ancoProduct.size) {
                var selctedProductID = ancoProduct[i].productId
                for (j in 0 until products.size) {
                    if (products[j].productId == selctedProductID) {
//                        this.products.remove(products[j])
                        this.products[j].selected = true
                    }
                }
            }
        }
    }

    private fun setAdapter() {
        productsAdapter = activity?.let {
            PortfolioProductsAdapter(
                it as AppCompatActivity,
                products,
                object : OnProductSelectedListener {
                    override fun onProductSelectedListener(product: ProductsResponse.Data, selected: Boolean) {
                        if (addedProducts == null)
                            addedProducts = ArrayList()
                        if (selected) {
                            if (isInList(product) == -1)
                                addedProducts?.add(product)
                        } else {
                            if (isInList(product) != -1 )
                                addedProducts?.removeAt(isInList(product))
                            if (isInSelectedList(product) != -1 ) {
                                var portfolioDetail = portfolioViewModel?.portfolioDetailLiveData?.value
                                if (portfolioDetail != null && portfolioDetail.products != null && portfolioDetail.products.size > 0) {
                                    portfolioDetail.products?.removeAt(isInSelectedList(product))
                                }
                            }
                        }
//        portfolioViewModel!!._ancoProductLiveData.value = products                    }
                    }
                })
        }!!
        listProducts.adapter = productsAdapter
    }

    private fun isInList(product: ProductsResponse.Data) : Int{
        if(addedProducts != null && addedProducts!!.size > 0) {
            for (i in 0 until addedProducts!!.size) {
                if(addedProducts!![i].productId == product.productId)
                    return i
            }
        }
        return -1
    }

    private fun isInSelectedList(product: ProductsResponse.Data) : Int{
        var portfolioDetail = portfolioViewModel?.portfolioDetailLiveData?.value
        if (portfolioDetail != null && portfolioDetail.products != null && portfolioDetail.products.size > 0) {
            for (i in 0 until portfolioDetail.products!!.size) {
                if(portfolioDetail.products!![i].id == product.productId)
                    return i
            }
        }
        return -1
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {
            R.id.imgBack -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }
            R.id.txtAddProduct -> {
                if(isAnySelected()) {
                    portfolioViewModel?.isAnythingEdited = true
                    portfolioViewModel!!._ancoProductLiveData.value = addedProducts
                    (requireActivity() as AppCompatActivity).hideKeyboard()
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    shortToast("Please select at least one product")
                }
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

            R.id.imgSearch -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    SearchFragment(),
                    true,
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


            R.id.imgCart -> {
//                (requireActivity() as AppCompatActivity).hideKeyboard()
                if(sharedPrefs.totalProductsInCart > 0) {
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

    private fun isAnySelected(): Boolean {
        var anySelected = false
        if(products != null && products.size > 0) {
            for (i in 0 until products.size) {
                if(products[i].selected) {
                    anySelected = true
                    break
                }
            }
        }
        return anySelected
    }

    override fun onProductSelectedListener(product: ProductsResponse.Data, selected: Boolean) {

    }

    override fun onDestroy() {
        super.onDestroy()
        portfolioViewModel?._addEditPortfolioLiveData?.value = null
        productsViewModel?._productsLiveData?.value = null
    }
}
