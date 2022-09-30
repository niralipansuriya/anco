package com.app.ancoturf.presentation.home.shop

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.product.remote.entity.response.ProductCategoryData
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.openLogoutAlertDialog
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.shop.adapters.ProductCategoryAdapter
import com.app.ancoturf.presentation.home.products.ProductsFragment
import com.app.ancoturf.presentation.notification.NotificationFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_shop.*
import kotlinx.android.synthetic.main.header.*
import java.util.*
import javax.inject.Inject

class ShopFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private var shopViewModel: ShopViewModel? = null

    var productCategoryAdapter: ProductCategoryAdapter? = null
    var productCategories: ArrayList<ProductCategoryData> = ArrayList()

    override fun getContentResource(): Int = R.layout.fragment_shop

    override fun viewModelSetup() {
        shopViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)[ShopViewModel::class.java]
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

        shopViewModel!!.callGetProductCategories()
        setAdapter()
        Utility.showProgress(context = requireActivity(), message = "", cancellable = false)
        txtShopAll.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgCart.setOnClickListener(this)
    }

    private fun initObservers() {
        shopViewModel!!.productCategoryLiveData.observe(this, Observer {
            if (productCategories == null)
                productCategories = ArrayList()
            productCategories.clear()
            productCategories.addAll(it)
            setAdapter()
        })

        shopViewModel!!.errorLiveData.observe(this, Observer {
            if(!Utility.isValueNull(shopViewModel!!.errorLiveData.value)) {
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
                    it?.let { it1 -> shortToast(it1) }
                }
                shopViewModel!!._errorLiveData.value = null
            }
        })
    }

    private fun setAdapter() {
//        if(productCategoryAdapter == null) {
        productCategoryAdapter = activity?.let {
            ProductCategoryAdapter(
                it as AppCompatActivity,
                productCategories,
                null,
                null
            )
        }!!
        listProducts.adapter = productCategoryAdapter
//        } else{
//            productCategoryAdapter!!.offers = orderStatuses
//            productCategoryAdapter!!.notifyDataSetChanged()
//        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.txtShopAll -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                val bundle = Bundle()
                bundle.putString("categoryIds" , "")
                bundle.putString("title" , "")
                bundle.putBoolean("fromRelated" , false)
                (requireActivity() as AppCompatActivity).pushFragment(
                    ProductsFragment().apply {
                        arguments = bundle
                    },
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
            R.id.imgLogo -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }

            R.id.imgBell ->{
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


}
