package com.app.ancoturf.presentation.home.offers

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.offer.remote.OfferDataResponse
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.cart.CartViewModel
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Logger
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_offer_detail.*
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject


class OfferDetailFragment(private val offer: OfferDataResponse.Data?) : BaseFragment(),
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private var cartViewModel: CartViewModel? = null

    override fun getContentResource(): Int = R.layout.fragment_offer_detail

    override fun viewModelSetup() {
        cartViewModel =
            ViewModelProviders.of(this, viewModelFactory)[CartViewModel::class.java]
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

        imgBack.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        txtGetOffer.setOnClickListener(this)

        setData()
    }

    private fun initObservers() {
        cartViewModel!!.couponInsertionLiveData.observe(this, Observer {
            if (it != null) {
                if (it)
                    shortToast(getString(R.string.offer_added_successfully_msg))
                cartViewModel!!._couponInsertionLiveData.value = null
            }
        })

        cartViewModel!!.couponLiveData.observe(this, Observer {
            if (it != null) {
                shortToast(getString(R.string.offer_already_added_msg))
                cartViewModel!!._couponLiveData.value = null
            } else {
                if (offer != null)
                    cartViewModel?.insertCoupon(offer.coupon)
            }
        })
    }

    private fun setData() {
        if (offer != null) {

            if (!Utility.isValueNull(offer.title)) {
                txtTitle.text = offer.title
            }

            if (!Utility.isValueNull(offer.imageUrl)) {
                Glide.with(requireActivity()).load(offer.imageUrl).into(imgOfferHeader)
                imgOfferHeader.visibility = View.VISIBLE
            } else {
                imgOfferHeader.visibility = View.GONE
            }

            if (!Utility.isValueNull(offer.content)) {
                txtHtmlDesc.loadData(offer.content, "text/html", "UTF-8")
                txtHtmlDesc.visibility = View.VISIBLE
            } else {
                txtHtmlDesc.visibility = View.GONE
            }

            if (!Utility.isValueNull(offer.footerImageUrl)) {
                Glide.with(requireActivity()).load(offer.footerImageUrl).into(imgOfferFooter)
                imgOfferFooter.visibility = View.VISIBLE
            } else {
                imgOfferFooter.visibility = View.GONE
            }

            Logger.log("for offer coupon : " + offer.coupon)

            if (offer.coupon == null) {
                txtGetOffer.visibility = View.GONE
            } else {
                txtGetOffer.visibility = View.VISIBLE
            }
        }
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {
            R.id.imgBell -> {
                openNotificationFragment()
            }
            R.id.imgBack -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }

            R.id.imgLogo -> {
//                (requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    HomeFragment(),
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )
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

            R.id.txtGetOffer -> {
                if (offer != null && offer.coupon != null)
                    cartViewModel?.getCouponById(offer.coupon.id)
            }
        }
    }
}
