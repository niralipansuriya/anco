package com.app.ancoturf.presentation.search

import android.app.Dialog
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.product.remote.entity.response.ProductDetailResponse
import com.app.ancoturf.data.product.remote.entity.response.ProductsResponse
import com.app.ancoturf.data.search.remote.entity.response.SearchProductResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.openLogoutAlertDialog
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.cart.CartViewModel
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.interfaces.OnProductAddedToCart
import com.app.ancoturf.presentation.home.products.ProductDetailFragment
import com.app.ancoturf.presentation.home.products.ProductsViewModel
import com.app.ancoturf.presentation.home.products.adapters.ProductsAdapter
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.adapter.SearchAdapter
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject

class SearchFragment : BaseFragment(),
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var searchViewModel: SearchViewModel? = null

    var searchAdapter: SearchAdapter? = null
    var lastrelatedsearch: ArrayList<SearchProductResponse.SearchedProducts> = ArrayList()
    var lastsearch: ArrayList<SearchProductResponse.SearchedProducts> = ArrayList()

    private var productsViewModel: ProductsViewModel? = null
    private var cartViewModel: CartViewModel? = null

    var productsAdapter: ProductsAdapter? = null
    var products: ArrayList<ProductsResponse.Data> = ArrayList()

    var addToCartProduct: ProductDetailResponse? = null

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    var pageNo = 1
    private var isLoad = false
    var itemCount = 0

    private lateinit var dialog: Dialog


    override fun getContentResource(): Int = R.layout.fragment_search

    override fun viewModelSetup() {

        pageNo = 1
        isLoad = false

        productsViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[ProductsViewModel::class.java]

        cartViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[CartViewModel::class.java]
        searchViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[SearchViewModel::class.java]

        initObservers()
    }

    override fun viewSetup() {
        if (activity is HomeActivity) {
            (activity as HomeActivity).hideShowFooter(true)
            (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, false)
        } else if (activity is PaymentActivity) {
            (activity as PaymentActivity).hideShowFooter(true)
            (activity as PaymentActivity).showCartDetails(imgCart, txtCartProducts, false)
        } else if (activity is ProfileActivity) {
            (activity as ProfileActivity).hideShowFooter(true)
            (activity as ProfileActivity).showCartDetails(imgCart, txtCartProducts, false)
        }

        txtSearch.setOnClickListener(this)
        txtRequestProduct.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgCart.setOnClickListener(this)

        imgProductOne.setOnClickListener(this)
        imgProductTwo.setOnClickListener(this)
        imgProductThree.setOnClickListener(this)
        imgProductFour.setOnClickListener(this)
        imgProductFive.setOnClickListener(this)

        imgSearchProductOne.setOnClickListener(this)
        imgSearchProductTwo.setOnClickListener(this)
        imgSearchProductThree.setOnClickListener(this)
        imgSearchProductFour.setOnClickListener(this)
        imgSearchProductFive.setOnClickListener(this)
        imgSearchProductSix.setOnClickListener(this)
        imgSearchProductSeven.setOnClickListener(this)
        imgSearchProductEight.setOnClickListener(this)

        Utility.showProgress(context = requireActivity(), message = "", cancellable = false)

        when {
            searchViewModel!!.isSearch -> productsViewModel?.callGetProducts("")

            sharedPrefs.userType == Constants.GUEST_USER -> searchViewModel?.callGetLastSearchRelatedProduct(
                searchViewModel!!.searchKey
            )

            else -> searchViewModel?.callGetLastSearchRelatedProduct("")
        }
        if (sharedPrefs.userType == Constants.LANDSCAPER) {
            txtRequestProduct.visibility = View.VISIBLE
        }

        txtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                (activity as AppCompatActivity).hideKeyboard()
                productsViewModel?.data?.set(UseCaseConstants.SEARCH, txtSearch.text.toString())
                searchViewModel?.isSearch = true
                searchViewModel?.searchKey = txtSearch.text.toString()
                Utility.showProgress(requireContext(), "", false)
                productsViewModel?.callGetProducts("")


                /*if (Utility.isValueNull(txtSearch.text.toString()))

                else {

                }*/
            }
            false
        }
    }

    private fun initObservers() {
        searchViewModel!!.searchLiveData.observe(this, Observer {
            if (it != null) {
                Log.e("resp", it.toString())
                if (lastrelatedsearch == null) {
                    lastrelatedsearch = ArrayList()
                    lastsearch = ArrayList()
                }
                lastrelatedsearch.clear()
                lastsearch.clear()
                lastrelatedsearch.addAll(it.recommandedProducts)
                lastsearch.addAll(it.lastSearchedProducts)
                setData()
            } else {
                lastrelatedsearch.clear()
                lastsearch.clear()
                setData()
            }

        })

        searchViewModel!!.requestProductLiveData.observe(this, Observer {
            if (it != null) {
                Log.e("resp", it.toString())
                Utility.cancelProgress()
                if (dialog != null)
                    dialog.dismiss()
                shortToast(it.message)
                searchViewModel?._requestProductLiveData?.value = null
            }
        })

        productsViewModel!!.productsLiveData.observe(this, Observer {
            if (it != null) {
                Log.e("resp", it.toString())
                if (it.data != null) {
                    if (products == null)
                        products = ArrayList()

                    if (pageNo == 1) {

                        products.clear()
                        products.addAll(it.data!!.data)
                        setAdapter()

                    } else
                        productsAdapter?.addItems(it.data as java.util.ArrayList<ProductsResponse.Data>)
                } else {
                    shortToast(it.message)
                }

                productsViewModel!!._productsLiveData.value = null
            }
        })

        searchViewModel!!.errorLiveData.observe(this, Observer {
            if (!Utility.isValueNull(searchViewModel!!.errorLiveData.value)) {
                if (it.equals(ErrorConstants.UNAUTHORIZED_ERROR_CODE)) {
                    if(activity is HomeActivity) {
                        (activity as HomeActivity).cartViewModel?.deleteProduct(null)
                        (activity as HomeActivity).cartViewModel?.deleteCoupon(null)
                    } else if(activity is PaymentActivity) {
                        (activity as PaymentActivity).cartViewModel?.deleteProduct(null)
                        (activity as PaymentActivity).cartViewModel?.deleteCoupon(null)
                    } else if(activity is ProfileActivity) {
                        (activity as ProfileActivity).cartViewModel?.deleteProduct(null)
                        (activity as ProfileActivity).cartViewModel?.deleteCoupon(null)
                    }
                        (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                } else {
                    it?.let { it1 ->
                        shortToast(it1)
                    }
                    searchViewModel!!._errorLiveData.value = null
                }
            }

        })

        productsViewModel!!.errorLiveData.observe(this, Observer {
            if (!Utility.isValueNull(searchViewModel!!.errorLiveData.value)) {
                if (it.equals(ErrorConstants.UNAUTHORIZED_ERROR_CODE)) {
                    if(activity is HomeActivity) {
                        (activity as HomeActivity).cartViewModel?.deleteProduct(null)
                        (activity as HomeActivity).cartViewModel?.deleteCoupon(null)
                    } else if(activity is PaymentActivity) {
                        (activity as PaymentActivity).cartViewModel?.deleteProduct(null)
                        (activity as PaymentActivity).cartViewModel?.deleteCoupon(null)
                    } else if(activity is ProfileActivity) {
                        (activity as ProfileActivity).cartViewModel?.deleteProduct(null)
                        (activity as ProfileActivity).cartViewModel?.deleteCoupon(null)
                    }
                    (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                } else {
                    it?.let { it1 ->
                        shortToast(it1)
                    }
                    searchViewModel!!._errorLiveData.value = null
                }
            }

        })

        cartViewModel!!.productInsertionLiveData.observe(this, Observer {
            if (it != null && it) {
                Handler().postDelayed({
                    shortToast(if (addToCartProduct == null) getString(R.string.product_successfully_added_msg) else getString(R.string.product_successfully_updated_msg))
                }, 1000)
                if (activity is HomeActivity) {
                    (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, false)
                } else if (activity is PaymentActivity) {
                    (activity as PaymentActivity).showCartDetails(imgCart, txtCartProducts, false)
                } else if (activity is ProfileActivity) {
                    (activity as ProfileActivity).showCartDetails(imgCart, txtCartProducts, false)
                }
                addToCartProduct = null
                cartViewModel!!._productInsertionLiveData.value = null
            }  else {
                if (addToCartProduct != null) {
                    addToCartProduct = null
                    shortToast(getString(R.string.product_updation_failed_msg))
                }
            }
        })

        cartViewModel!!.productLiveData.observe(this, Observer {
            if (addToCartProduct != null) {
                if (it != null) {
                    addToCartProduct!!.qty = addToCartProduct!!.qty + it.qty
                } else {
                    sharedPrefs.totalProductsInCart = sharedPrefs.totalProductsInCart + 1
                }
                cartViewModel!!.insertProduct(addToCartProduct!!, it != null)
            }
        })
    }


    private fun setAdapter() {
        if (products != null && products.size > 0) {
            productsAdapter = activity?.let {
                ProductsAdapter(
                    it as AppCompatActivity,
                    products,
                    object : OnProductAddedToCart {
                        override fun OnProductAddedToCart(product: ProductDetailResponse) {
                            addToCartProduct = product
                            cartViewModel?.getProductById(product.id)
                        }
                    }
                )
            }!!
            listSearchedProducts.adapter = productsAdapter
            mainLayout.visibility = View.GONE
            listSearchedProducts.visibility = View.VISIBLE
        } else {
            mainLayout.visibility = View.VISIBLE
            listSearchedProducts.visibility = View.GONE
        }
    }

    private fun setData() {
        if (lastrelatedsearch != null && lastrelatedsearch.size > 0) {

            when (lastrelatedsearch.size) {
                1 -> {
                    linearOne.visibility = View.VISIBLE
                    imgProductOne.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastrelatedsearch[0].featureImageUrl).into(imgProductOne)

                    (linearOne.layoutParams as LinearLayout.LayoutParams).weight = 2F

                    imgProductTwo.visibility = View.GONE
                    linearTwo.visibility = View.GONE
                    linearThree.visibility = View.GONE
                    linearFour.visibility = View.INVISIBLE
                }
                2 -> {
                    linearOne.visibility = View.VISIBLE
                    imgProductOne.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastrelatedsearch[0].featureImageUrl).into(imgProductOne)
                    imgProductTwo.visibility = View.VISIBLE
                    linearTwo.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastrelatedsearch[1].featureImageUrl).into(imgProductTwo)
                    linearTwo.visibility = View.INVISIBLE
                    linearThree.visibility = View.INVISIBLE
                    linearFour.visibility = View.INVISIBLE

                }
                3 -> {
                    linearOne.visibility = View.VISIBLE
                    imgProductOne.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(lastrelatedsearch[0].featureImageUrl)
                        .into(imgProductOne)
                    imgProductTwo.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(lastrelatedsearch[1].featureImageUrl)
                        .into(imgProductTwo)
                    linearTwo.visibility = View.VISIBLE
                    imgProductThree.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(lastrelatedsearch[2].featureImageUrl)
                        .into(imgProductThree)

                    (linearTwo.layoutParams as LinearLayout.LayoutParams).weight = 2F

                    imgProductFour.visibility = View.GONE
                    linearThree.visibility = View.GONE
                    linearFour.visibility = View.INVISIBLE
                }
                4 -> {
                    linearOne.visibility = View.VISIBLE
                    imgProductOne.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(lastrelatedsearch[0].featureImageUrl)
                        .into(imgProductOne)
                    imgProductTwo.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(lastrelatedsearch[1].featureImageUrl)
                        .into(imgProductTwo)
                    linearTwo.visibility = View.VISIBLE
                    imgProductThree.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(lastrelatedsearch[2].featureImageUrl)
                        .into(imgProductThree)
                    imgProductFour.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(lastrelatedsearch[3].featureImageUrl)
                        .into(imgProductFour)
                    linearThree.visibility = View.INVISIBLE
                    linearFour.visibility = View.INVISIBLE
                }
                5 -> {

                    linearOne.visibility = View.VISIBLE
                    imgProductOne.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastrelatedsearch[0].featureImageUrl).into(imgProductOne)
                    imgProductTwo.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastrelatedsearch[1].featureImageUrl).into(imgProductTwo)
                    linearTwo.visibility = View.VISIBLE
                    imgProductThree.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastrelatedsearch[2].featureImageUrl).into(imgProductThree)
                    imgProductFour.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastrelatedsearch[3].featureImageUrl).into(imgProductFour)
                    linearThree.visibility = View.VISIBLE

                    (linearThree.layoutParams as LinearLayout.LayoutParams).weight = 2F
                    imgProductFive.visibility = View.VISIBLE
                    imgProductSix.visibility = View.GONE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastrelatedsearch[4].featureImageUrl).into(imgProductFive)
                    linearFour.visibility = View.INVISIBLE
                }
            }
            layoutLikeSearch.visibility = View.VISIBLE

            /*   searchAdapter = activity?.let {
                   SearchAdapter(
                       it as AppCompatActivity,
                       lastrelatedsearch
                   )
               }!!

               listlikeproducts.adapter = searchAdapter

               searchAdapter = activity?.let {
                   SearchAdapter(
                       it as AppCompatActivity,
                       lastsearch
                   )
               }!!

               listsearchhistory.adapter = searchAdapter*/
        } else {
            layoutLikeSearch.visibility = View.GONE
            txtLikeIt.visibility = View.GONE
        }

        if (lastsearch != null && lastsearch.size > 0) {
            when (lastsearch.size) {
                1 -> {
                    linearSearchOne.visibility = View.VISIBLE
                    imgSearchProductOne.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[0].featureImageUrl).into(imgSearchProductOne)

                    (linearSearchOne.layoutParams as LinearLayout.LayoutParams).weight = 2F

                    imgSearchProductTwo.visibility = View.GONE
                    linearSearchTwo.visibility = View.GONE
                    linearSearchThree.visibility = View.INVISIBLE
                    linearSearchFour.visibility = View.INVISIBLE
                }
                2 -> {
                    linearSearchOne.visibility = View.VISIBLE
                    imgSearchProductOne.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[0].featureImageUrl).into(imgSearchProductOne)
                    imgSearchProductTwo.visibility = View.VISIBLE
                    linearSearchTwo.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[1].featureImageUrl).into(imgSearchProductThree)

                    linearSearchTwo.visibility = View.INVISIBLE
                    linearSearchThree.visibility = View.INVISIBLE
                    linearSearchFour.visibility = View.INVISIBLE
                }
                3 -> {
                    linearSearchOne.visibility = View.VISIBLE
                    imgSearchProductOne.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(lastsearch[0].featureImageUrl)
                        .into(imgSearchProductOne)
                    imgSearchProductTwo.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(lastsearch[1].featureImageUrl)
                        .into(imgSearchProductTwo)
                    linearSearchTwo.visibility = View.VISIBLE
                    imgProductThree.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(lastsearch[2].featureImageUrl)
                        .into(imgSearchProductThree)
                    (linearSearchTwo.layoutParams as LinearLayout.LayoutParams).weight = 2F
                    imgSearchProductFour.visibility = View.GONE
                    linearSearchThree.visibility = View.GONE
                    linearSearchFour.visibility = View.INVISIBLE
                }
                4 -> {
                    linearSearchOne.visibility = View.VISIBLE
                    imgSearchProductOne.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(lastsearch[0].featureImageUrl)
                        .into(imgSearchProductOne)
                    imgSearchProductTwo.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(lastsearch[1].featureImageUrl)
                        .into(imgSearchProductTwo)
                    linearTwo.visibility = View.VISIBLE
                    imgSearchProductThree.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(lastsearch[2].featureImageUrl)
                        .into(imgSearchProductThree)
                    imgSearchProductFour.visibility = View.VISIBLE
                    Glide.with(requireActivity()).load(lastsearch[3].featureImageUrl)
                        .into(imgSearchProductFour)

                    linearSearchThree.visibility = View.INVISIBLE
                    linearSearchFour.visibility = View.INVISIBLE
                }
                5 -> {
                    linearSearchOne.visibility = View.VISIBLE
                    imgSearchProductOne.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[0].featureImageUrl).into(imgSearchProductOne)
                    imgSearchProductTwo.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[1].featureImageUrl).into(imgSearchProductTwo)
                    linearSearchTwo.visibility = View.VISIBLE
                    imgSearchProductThree.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[2].featureImageUrl).into(imgSearchProductThree)
                    imgSearchProductFour.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[3].featureImageUrl).into(imgSearchProductFour)
                    linearSearchThree.visibility = View.VISIBLE
                    imgSearchProductFive.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[4].featureImageUrl).into(imgSearchProductFive)

                    (linearSearchThree.layoutParams as LinearLayout.LayoutParams).weight = 2F
                    imgSearchProductSix.visibility = View.GONE
                    linearSearchFour.visibility = View.INVISIBLE

                }
                6 -> {
                    linearSearchOne.visibility = View.VISIBLE
                    imgSearchProductOne.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[0].featureImageUrl).into(imgSearchProductOne)
                    imgSearchProductTwo.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[1].featureImageUrl).into(imgSearchProductTwo)
                    linearSearchTwo.visibility = View.VISIBLE
                    imgSearchProductThree.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[2].featureImageUrl).into(imgSearchProductThree)
                    imgSearchProductFour.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[3].featureImageUrl).into(imgSearchProductFour)
                    linearSearchThree.visibility = View.VISIBLE
                    imgSearchProductFive.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[4].featureImageUrl).into(imgSearchProductFive)
                    imgSearchProductSix.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[5].featureImageUrl).into(imgSearchProductSix)
                    linearSearchFour.visibility = View.INVISIBLE
                }
                7 -> {
                    linearSearchOne.visibility = View.VISIBLE
                    imgSearchProductOne.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[0].featureImageUrl).into(imgSearchProductOne)
                    imgSearchProductTwo.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[1].featureImageUrl).into(imgSearchProductTwo)
                    linearSearchTwo.visibility = View.VISIBLE
                    imgSearchProductThree.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[2].featureImageUrl).into(imgSearchProductThree)
                    imgSearchProductFour.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[3].featureImageUrl).into(imgSearchProductFour)
                    linearSearchThree.visibility = View.VISIBLE
                    imgSearchProductFive.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[4].featureImageUrl).into(imgSearchProductFive)
                    imgSearchProductSix.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[5].featureImageUrl).into(imgSearchProductSix)
                    linearSearchFour.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[6].featureImageUrl).into(imgSearchProductSeven)
                    imgSearchProductEight.visibility = View.INVISIBLE
                }
                8 -> {
                    linearSearchOne.visibility = View.VISIBLE
                    imgSearchProductOne.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[0].featureImageUrl).into(imgSearchProductOne)
                    imgSearchProductTwo.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[1].featureImageUrl).into(imgSearchProductTwo)
                    linearSearchTwo.visibility = View.VISIBLE
                    imgSearchProductThree.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[2].featureImageUrl).into(imgSearchProductThree)
                    imgSearchProductFour.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[3].featureImageUrl).into(imgSearchProductFour)
                    linearSearchThree.visibility = View.VISIBLE
                    imgSearchProductFive.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[4].featureImageUrl).into(imgSearchProductFive)
                    imgSearchProductSix.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[5].featureImageUrl).into(imgSearchProductSix)
                    linearSearchFour.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[6].featureImageUrl).into(imgSearchProductSeven)
                    imgSearchProductEight.visibility = View.VISIBLE
                    Glide.with(requireActivity() as AppCompatActivity)
                        .load(lastsearch[7].featureImageUrl).into(imgSearchProductEight)
                }
            }
            layoutSearchHistory.visibility = View.VISIBLE
        } else {
            layoutSearchHistory.visibility = View.GONE
            txtSearchHistory.visibility = View.GONE
        }

        if (lastrelatedsearch.size == 0 && lastsearch.size == 0) {
            txtNoSearch.visibility = View.GONE
            view2.visibility = View.GONE
            layoutSearchHistory.visibility = View.GONE
            txtSearchHistory.visibility = View.GONE
            layoutLikeSearch.visibility = View.GONE
            txtLikeIt.visibility = View.GONE

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
                //(requireActivity() as AppCompatActivity).hideKeyboard()
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

            R.id.imgProductOne -> {

                Log.d("trace", "size=======================${lastrelatedsearch.size}")
                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductDetailFragment(lastrelatedsearch[0].id),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgProductTwo -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductDetailFragment(lastrelatedsearch[1].id),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
            R.id.imgProductThree -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductDetailFragment(lastrelatedsearch[2].id),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgProductFour -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductDetailFragment(lastrelatedsearch[3].id),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgProductFive -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductDetailFragment(lastrelatedsearch[4].id),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgSearchProductOne -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductDetailFragment(lastsearch[0].id),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgSearchProductTwo -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductDetailFragment(lastsearch[1].id),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
            R.id.imgSearchProductThree -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductDetailFragment(lastsearch[2].id),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgSearchProductFour -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductDetailFragment(lastsearch[3].id),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgSearchProductFive -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductDetailFragment(lastsearch[4].id),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgSearchProductSix -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductDetailFragment(lastsearch[5].id),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgSearchProductSeven -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductDetailFragment(lastsearch[6].id),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgSearchProductEight -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductDetailFragment(lastsearch[7].id),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.txtRequestProduct -> {
                openReorderDialog()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchViewModel?.isSearch = false
        searchViewModel!!._searchLiveData.value = null
    }


    private fun openReorderDialog() {
        // Create custom dialog object
        dialog = Dialog(requireContext())

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog_request_product)
        dialog.show()

        val window = dialog.window
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)


        val edtName = dialog.findViewById(R.id.edtName) as AppCompatEditText
        val edtEmail = dialog.findViewById(R.id.edtEmail) as AppCompatEditText
        val edtProductDescription =
            dialog.findViewById(R.id.edtProductDescription) as AppCompatEditText
        val edtProductUrl = dialog.findViewById(R.id.edtProductUrl) as AppCompatEditText
        val txtSubmitRequest = dialog.findViewById(R.id.txtSubmitRequest) as AppCompatTextView
        val txtCancel = dialog.findViewById(R.id.txtCancel) as AppCompatTextView

        val imgClose = dialog.findViewById(R.id.imgClose) as ImageView

        imgClose.setOnClickListener {
            dialog.dismiss()
        }
        txtCancel.setOnClickListener {
            dialog.dismiss()
        }

        txtSubmitRequest.setOnClickListener {
            if (Utility.isValueNull(edtName.text.toString())) {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.black_username_message))
            } else if (Utility.isValueNull(edtEmail.text.toString())) {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.blank_email_message))
            } else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.text.toString()).matches()) {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.invalid_email_message))
            } else if (Utility.isValueNull(edtProductDescription.text.toString())) {
                (requireActivity() as AppCompatActivity).shortToast(getString(R.string.blank_project_desc))
            } else {

                Utility.showProgress(context = requireContext(), message = "", cancellable = false)
                searchViewModel?.callAddRequestProduct(
                    name = edtName.text.toString(),
                    email = edtEmail.text.toString(),
                    productDescription = edtProductDescription.text.toString(),
                    productUrl = edtProductUrl.text.toString()
                )
            }
        }
    }
}
