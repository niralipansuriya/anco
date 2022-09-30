package com.app.ancoturf.presentation.home.products

import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.cart.persistance.entity.ProductDB
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.data.product.remote.entity.response.RelatedProductsResponse
import com.app.ancoturf.data.setting.remote.entity.SettingsResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.extension.*
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.cart.CartViewModel
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.interfaces.OnProductAddedToCart
import com.app.ancoturf.presentation.home.portfolio.PortfolioViewModel
import com.app.ancoturf.presentation.home.products.adapters.ProductsAdapter
import com.app.ancoturf.presentation.home.products.adapters.RelatedProductsAdapter
import com.app.ancoturf.presentation.home.products.filter.ProductFilterFragment
import com.app.ancoturf.presentation.home.shop.ShopViewModel
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import com.app.ancoturf.utils.interfces.OnDialogButtonClick
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_products.*
import kotlinx.android.synthetic.main.header.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ProductsFragment(
//    private val categoryIds: String,
//    private val title: String?,
//    private val fromRelated: Boolean
) :
    BaseFragment(),
    View.OnClickListener {

    private var categoryIds: String = ""
    private var title: String = ""
    private var fromRelated: Boolean = false
    private var qtyAddInCart: Int = 0

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var productsViewModel: ProductsViewModel? = null
    private var shopViewModel: ShopViewModel? = null
    private var portfolioViewModel: PortfolioViewModel? = null
    private var cartViewModel: CartViewModel? = null

    var productsAdapter: ProductsAdapter? = null
    var products: ArrayList<ProductsResponse.Data> = ArrayList()
    var productIdQty : ArrayList<ProductDB> = ArrayList()

    var relatedProductsAdapter: RelatedProductsAdapter? = null
    var relatedProducts: ArrayList<RelatedProductsResponse.Data> = ArrayList()

    var addToCartProduct: ProductDetailResponse? = null
    var updateProduct: Boolean = false

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    var pageNo = 1
    private var isLoad = false
    var itemCount = 0


    override fun getContentResource(): Int = R.layout.fragment_products

    override fun viewModelSetup() {
        pageNo = 1
        isLoad = false
        shopViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[ShopViewModel::class.java]
        productsViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[ProductsViewModel::class.java]
        portfolioViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[PortfolioViewModel::class.java]
        cartViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[CartViewModel::class.java]
        initObservers()
    }

    override fun viewSetup() {

        arguments?.let {
            categoryIds = it.getString("categoryIds").toString()
            title = it.getString("title").toString()
            fromRelated = it.getBoolean("fromRelated")
            arguments = null
        }

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

        txtAddProduct.visibility = View.GONE
        Utility.showProgress(context = requireActivity(), message = "", cancellable = false)

        if (!productsViewModel!!.filterAvailable) {
            productsViewModel!!.data.put(UseCaseConstants.PRODUCT_CATEGORY_IDS, categoryIds)
            if (!Utility.isValueNull(categoryIds))
                setCategoryIdChecked()
            else {
                productsViewModel?.title = getString(R.string.shop_all)
                productsViewModel?.numberOfFilters = 0
                //pagination
                getProductByPaging()
            }

            if (!Utility.isValueNull(sharedPrefs.tags)) {
                productsViewModel?._tagsLiveData?.value = Gson().fromJson(
                    sharedPrefs.tags,
                    Array<SettingsResponse.Data.Tag>::class.java
                ).toList() as ArrayList<SettingsResponse.Data.Tag>
            }
            productsViewModel!!.filterAvailable = true
        }

        if (fromRelated) {
            txtTitle.text = title
            productsViewModel?.callGetRelatedProducts(categoryIds.toInt(), 100)
            imgFilter.visibility = View.GONE
            txtNumberOfFilters.visibility = View.GONE
            txtClearFilters.visibility = View.GONE
        } else {
            txtTitle.text = productsViewModel?.title
            productsViewModel!!.callGetProducts("")
            imgFilter.visibility = View.VISIBLE

            //pagination
            getProductByPaging()

            if (productsViewModel?.numberOfFilters != 0) {
                txtNumberOfFilters.visibility = View.VISIBLE
                txtNumberOfFilters.text = "" + productsViewModel?.numberOfFilters
                txtClearFilters.visibility = View.VISIBLE
                txtClearFilters.setOnClickListener(this)
            } else {
                txtNumberOfFilters.visibility = View.GONE
                txtClearFilters.visibility = View.GONE
                getProductByPaging()

            }
        }

        imgBack.setOnClickListener(this@ProductsFragment)
        imgFilter.setOnClickListener(this@ProductsFragment)
        imgMore.setOnClickListener(this@ProductsFragment)
        imgSearch.setOnClickListener(this@ProductsFragment)
        imgCart.setOnClickListener(this@ProductsFragment)
        imgLogo.setOnClickListener(this@ProductsFragment)
    }

    private fun getProductByPaging() {
        listProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoad) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == products.size - 1) {
                        //bottom of list!
                        pageNo++
                        if (productsViewModel?._isNextPageUrl?.value != null)
                            if (productsViewModel?._isNextPageUrl?.value!!)
                                productsViewModel?.callGetProducts("" + pageNo)!!

                        Handler().postDelayed({
                            isLoad = !productsViewModel!!._isNextPageUrl.value!!
                        }, 500)

                    }
                }
            }
        })
    }

    private fun setCategoryIdChecked() {
        if (shopViewModel?.productCategoryLiveData?.value != null) {
            var productCategories = shopViewModel?.productCategoryLiveData?.value!!
            for (i in 0..(productCategories.size - 1)) {
                if (productCategories.get(i).id == categoryIds.toInt()) {
                    productCategories.get(i).checked = true
                    productsViewModel?.title = productCategories.get(i).displayName
//                    productsViewModel?.numberOfFilters = productsViewModel?.numberOfFilters!! + 1
                }
            }
        } else if (portfolioViewModel?.productCategoryLiveData?.value != null) {
            var productCategories = portfolioViewModel?.productCategoryLiveData?.value!!
            for (i in 0..(productCategories.size - 1)) {
                if (productCategories.get(i).id == categoryIds.toInt()) {
                    productCategories.get(i).checked = true
                    productsViewModel?.title = productCategories.get(i).displayName
//                    productsViewModel?.numberOfFilters = productsViewModel?.numberOfFilters!! + 1
                }
            }
        } else {
            productsViewModel?.title = getString(R.string.shop_all)
            productsViewModel?.numberOfFilters = 0
        }
    }

    private fun initObservers() {

        productsViewModel!!.productsLiveData.observe(this, Observer {
            if (it != null) {

                Log.e("resp", it.toString())
                if (it.data != null) {
                    if (products == null)
                        products = ArrayList()

                    if (pageNo == 1) {
                        if (productsViewModel?.filterAvailable!!)
                            products.clear()
                        products.addAll(it.data!!.data)
                        setAdapter()

                    } else {
                        //nnn 15122020
                        products.addAll(it.data.data)
                        setAdapter()
//                        shortToast(it.message)
                    }

                } else
                    productsAdapter?.addItems(it.data as ArrayList<ProductsResponse.Data>?)

                productsViewModel!!._productsLiveData.value = null
            }
        })

        productsViewModel!!.relatedProductsLiveData.observe(this, Observer {
            if (it != null && it.success) {
                Log.e("resp", it.toString())
                if (relatedProducts == null)
                    relatedProducts = ArrayList()
                relatedProducts.clear()
                it.data?.data?.let { it1 -> relatedProducts.addAll(it1) }
                setAdapter()
                productsViewModel!!._relatedProductsLiveData.value = null
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
                    productsViewModel!!._errorLiveData.value = null
                }
            }
        })

        cartViewModel!!.AvailableQtyLiveData.observe(this, Observer {
            if (it != null) {
                if (it.success){
                    cartViewModel?.getProductById(addToCartProduct!!.id)
                }else{
                    Utility.cancelProgress()
                    shortToast(it.message)
                }
                cartViewModel!!._availableQtyLiveData.value = null
            }
        })
        cartViewModel!!.productInsertionLiveData.observe(this, Observer {
            if (it != null && it) {
                Handler().postDelayed({
                    Utility.cancelProgress()

//                    cartViewModel!!.getAvailableQty(productIdQty!!)
                    shortToast(if (!updateProduct) getString(R.string.product_successfully_added_msg) else getString(R.string.product_successfully_updated_msg))
                    if(activity is HomeActivity) {
                        (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, false)
                    } else if(activity is PaymentActivity) {
                        (activity as PaymentActivity).showCartDetails(imgCart, txtCartProducts, false)
                    } else if(activity is ProfileActivity) {
                        (activity as ProfileActivity).showCartDetails(imgCart, txtCartProducts, false)
                    }
                        updateProduct = false
                    cartViewModel!!._productInsertionLiveData.value = null
                }, 1000)
            }  else {
                if(updateProduct) {
                    updateProduct = false
                    shortToast(getString(R.string.product_updation_failed_msg))
                }
            }
        })

        cartViewModel!!.productLiveData.observe(this, Observer {
            if (addToCartProduct != null) {
                if (it != null) {
                    updateProduct = true
                    addToCartProduct!!.qty = addToCartProduct!!.qty + it.qty
                } else {
                    updateProduct = false
                    sharedPrefs.totalProductsInCart = sharedPrefs.totalProductsInCart + 1
                }
                cartViewModel!!.insertProduct(addToCartProduct!!, it != null)
            }
        })
    }


    private fun setAdapter() {
        if (fromRelated) {
            if (relatedProducts != null && relatedProducts.size > 0) {
                relatedProductsAdapter = activity?.let {
                    RelatedProductsAdapter(
                        it as AppCompatActivity,
                        relatedProducts,
                        relatedProducts.size,
                        object : OnProductAddedToCart {
                            override fun OnProductAddedToCart(product: ProductDetailResponse) {
                                Utility.showProgress(requireContext(), "", false)
                                addToCartProduct = product
                                if (addToCartProduct != null){
                                    productIdQty.clear()
                                    var productDB =  ProductDB(
                                        product_id =  addToCartProduct!!.id,
                                        feature_img_url = "",
                                        qty =  addToCartProduct!!.qty,
                                        price = 0f ,
                                        product_category_id = 0,
                                        product_name = "",
                                        product_unit = "",
                                        product_unit_id = 0,
                                        base_total_price = 0f,
                                        is_turf = 0,
                                        total_price = 0f,
                                        product_redeemable_against_credit = false
                                    )
                                    productIdQty.add(productDB!!)
                                }
                                cartViewModel?.getAvailableQty(productIdQty)
                            }
                        }
                    )
                }!!
                listProducts.adapter = relatedProductsAdapter
                txtNoProduct.visibility = View.GONE
                listProducts.visibility = View.VISIBLE
            } else {
                txtNoProduct.visibility = View.VISIBLE
                listProducts.visibility = View.GONE
            }
        } else {
            if (products != null && products.size > 0) {
                productsAdapter = activity?.let {
                    ProductsAdapter(
                        it as AppCompatActivity,
                        products,
                        object : OnProductAddedToCart {
                            override fun OnProductAddedToCart(product: ProductDetailResponse) {
                                Utility.showProgress(requireContext(), "", false)
                                addToCartProduct = product
//                                cartViewModel?.getProductById(product.id)
                                if (addToCartProduct != null){
                                    productIdQty.clear()
                                    var productDB =  ProductDB(
                                        product_id =  addToCartProduct!!.id,
                                        feature_img_url = "",
                                        qty =  addToCartProduct!!.qty,
                                        price = 0f ,
                                        product_category_id = 0,
                                        product_name = "",
                                        product_unit = "",
                                        product_unit_id = 0,
                                        base_total_price = 0f,
                                        is_turf = 0,
                                        total_price = 0f,
                                        product_redeemable_against_credit = false
                                    )
                                    productIdQty.add(productDB!!)
                                }
                                cartViewModel?.getAvailableQty(productIdQty)
                            }
                        }
                    )
                }!!
                listProducts.adapter = productsAdapter
                txtNoProduct.visibility = View.GONE
                listProducts.visibility = View.VISIBLE
            } else {
                txtNoProduct.visibility = View.VISIBLE
                listProducts.visibility = View.GONE
            }
        }
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {
            R.id.imgBack -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
//                requireActivity().supportFragmentManager.popBackStack()
                (requireActivity() as AppCompatActivity).onBackPressed()
            }
            R.id.imgFilter -> {
                pageNo = 1
                (requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductFilterFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
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

            R.id.txtClearFilters -> {
                (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                    title = getString(R.string.app_name),
                    message = getString(R.string.clear_filter_confirmation_message),
                    positiveButton = getString(android.R.string.ok),
                    nagetiveButton = getString(android.R.string.cancel),
                    onDialogButtonClick = object : OnDialogButtonClick {
                        override fun onPositiveButtonClick() {
                            productsViewModel?.data?.put(UseCaseConstants.PRODUCT_CATEGORY_IDS, "")
                            productsViewModel?.data?.put(UseCaseConstants.PRODUCT_TAG_IDS, "")
                            productsViewModel?.data?.put(UseCaseConstants.PRICE_MIN, "")
                            productsViewModel?.data?.put(UseCaseConstants.PRICE_MAX, "")
                            productsViewModel?.data?.put(UseCaseConstants.SEARCH, "")
                            productsViewModel?.data?.put(UseCaseConstants.SORT_BY, "")
                            if (!Utility.isValueNull(sharedPrefs.tags)) {
                                productsViewModel?._tagsLiveData?.value = Gson().fromJson(
                                    sharedPrefs.tags,
                                    Array<SettingsResponse.Data.Tag>::class.java
                                ).toList() as ArrayList<SettingsResponse.Data.Tag>
                            }

                            if (shopViewModel?.productCategoryLiveData?.value != null && shopViewModel?.productCategoryLiveData?.value!!.size > 0) {
                                for (i in 0..(shopViewModel?.productCategoryLiveData?.value!!.size - 1)) {
                                    shopViewModel?.productCategoryLiveData?.value!!.get(i).checked =
                                        false
                                }
                            }
                            pageNo = 1
                            productsViewModel?.title = getString(R.string.shop_all)
                            txtTitle.text = getString(R.string.shop_all) //nnn 11/12/2020
                            productsViewModel?.numberOfFilters = 0

                            Utility.showProgress(requireContext(), "", false)
                            productsViewModel?.callGetProducts("")

                            txtNumberOfFilters.visibility = View.GONE
                            txtClearFilters.visibility = View.GONE

                        }

                        override fun onNegativeButtonClick() {
                        }
                    })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        productsViewModel?.data?.put(UseCaseConstants.PRODUCT_CATEGORY_IDS, "")
        productsViewModel?.data?.put(UseCaseConstants.PRODUCT_TAG_IDS, "")
        productsViewModel?.data?.put(UseCaseConstants.PRICE_MIN, "")
        productsViewModel?.data?.put(UseCaseConstants.PRICE_MAX, "")
        productsViewModel?.data?.put(UseCaseConstants.SEARCH, "")
        productsViewModel?.data?.put(UseCaseConstants.SORT_BY, "")
        productsViewModel?.filterAvailable = false
        productsViewModel?.numberOfFilters = 0
        addToCartProduct = null
    }
}
