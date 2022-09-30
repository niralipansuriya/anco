package com.app.ancoturf.presentation.home.offers

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
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.offer.remote.OfferDataResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.extension.*
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.offers.adapters.OfferAdapter
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_offers.*
import kotlinx.android.synthetic.main.fragment_order.*
import kotlinx.android.synthetic.main.header.*
import java.util.ArrayList
import javax.inject.Inject

class OffersFragment(private var offer: OfferDataResponse.Data?) :
    BaseFragment(),
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var offersViewModel: OffersViewModel? = null

    var offerAdapter: OfferAdapter? = null
    var offers: ArrayList<OfferDataResponse.Data> = ArrayList()

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    var pageNo = 1
    private var isLoad = false
    var itemCount = 0


    override fun getContentResource(): Int = R.layout.fragment_offers

    override fun viewModelSetup() {
        offersViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)[OffersViewModel::class.java]
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

        if(offer != null) {
            (requireActivity() as AppCompatActivity).pushFragment(
                OfferDetailFragment(offer),
                true,
                true,
                false,
                R.id.flContainerHome
            )
            offer = null
        } else {
            Utility.showProgress(context = requireActivity(), message = "", cancellable = false)
            offersViewModel?.callGetOffers("1")
        }

        imgMore.setOnClickListener(this@OffersFragment)
        imgSearch.setOnClickListener(this@OffersFragment)
        imgCart.setOnClickListener(this@OffersFragment)
        imgLogo.setOnClickListener(this@OffersFragment)

        //pagination
        getOfferWithPaging()
    }


    private fun getOfferWithPaging()
    {
        listOffers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (!isLoad) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == offers.size - 1) {
                        //bottom of list!
                        pageNo++
                        if (offersViewModel?._isNextPageUrl?.value != null)
                            if (offersViewModel!!._isNextPageUrl.value!!)
                                offersViewModel?.callGetOffers("" + pageNo)!!

                        Handler().postDelayed({
                            isLoad = !offersViewModel!!._isNextPageUrl.value!!
                        }, 500)

                    }
                }
            }
        })
    }

    private fun initObservers() {
        offersViewModel!!.offersLiveData.observe(this, Observer {
            if (it != null) {
                Log.e("resp", it.toString())
                if (offers == null)
                    offers = ArrayList()
                offers.clear()
                offers.addAll(it.data)
                setAdapter()
            } else {
                offers.clear()
                setAdapter()
            }
        })

        offersViewModel!!.errorLiveData.observe(this, Observer {
            if (!Utility.isValueNull(offersViewModel!!.errorLiveData.value)) {
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
                        if (offers != null)
                            offers.clear()
                        setAdapter()
                    }
                    offersViewModel!!._errorLiveData.value = null
                }
            }

        })
    }

    private fun setAdapter() {
        if (offers != null && offers.size > 0) {
            offerAdapter = activity?.let {
                OfferAdapter(
                    it as AppCompatActivity,
                    offers
                )
            }!!
            listOffers.adapter = offerAdapter
            txtNoOffer.visibility = View.GONE
            listOffers.visibility = View.VISIBLE
        } else {
            txtNoOffer.visibility = View.VISIBLE
            listOffers.visibility = View.GONE
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