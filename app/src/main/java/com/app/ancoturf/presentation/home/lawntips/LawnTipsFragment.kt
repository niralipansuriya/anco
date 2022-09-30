package com.app.ancoturf.presentation.home.lawntips

import android.content.Intent
import android.os.Handler
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
import com.app.ancoturf.data.lawntips.remote.LawnTipsDataResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.extension.openLogoutAlertDialog
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.lawntips.adapter.LawnTipsAdapter
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_lawn_tips.*
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject

class LawnTipsFragment() :
    BaseFragment(),
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var lawntipsViewModel: LawnTipsViewModel? = null

    var lawntipsAdapter: LawnTipsAdapter? = null
    var lawntips: ArrayList<LawnTipsDataResponse.Data> = ArrayList()

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    var pageNo = 1
    private var isLoad = false
    var isBack = false
    var itemCount = 0


    override fun getContentResource(): Int = R.layout.fragment_lawn_tips

    override fun viewModelSetup() {
        lawntipsViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[LawnTipsViewModel::class.java]
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
        Utility.showProgress(requireContext(), "", false)
        lawntipsViewModel?.getLawnTips("1")
        lawntipsAdapter = LawnTipsAdapter(activity as AppCompatActivity, lawntips,true)
        listlawntips.adapter = lawntipsAdapter
        imgMore.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        //pagination
        getLawnTipsWithPaging()
    }


    private fun initObservers() {
        lawntipsViewModel?.lawntipsLiveData?.observe(this, Observer {

            if (it != null) {
                Utility.cancelProgress()
                if (lawntips == null)
                    lawntips = ArrayList()

                if (pageNo == 1 || isBack) {
                    lawntips.clear()
                    lawntips.addAll(it.data)
                    setAdapter()
                } else
                    lawntipsAdapter?.addItems(it.data)

                lawntipsViewModel!!._lawntipsLiveData.value = null
            }
        })
        lawntipsViewModel!!.errorLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                if (it == ErrorConstants.UNAUTHORIZED_ERROR_CODE) {
                    (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                } else {
                    shortToast(it)
                    if (it == "No LawnTips found") {
                        if (lawntips == null)
                            lawntips = ArrayList()
                        lawntips.clear()
                        setAdapter()
                    }
                }
                lawntipsViewModel?._errorLiveData?.value = null
            }
        })

    }

    private fun setAdapter() {
        if (lawntips != null && lawntips.size > 0) {
            lawntipsAdapter = activity?.let {
                LawnTipsAdapter(
                    it as AppCompatActivity, lawntips,
                    true
                )
            }!!
            listlawntips.adapter = lawntipsAdapter
            txtNolawntips.visibility = View.GONE

            listlawntips.visibility = View.VISIBLE


        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgBell -> {
                openNotificationFragment()
            }
            R.id.imgMore -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    ContactFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.imgCart -> {
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

            R.id.imgLogo -> {
                //(requireActivity() as AppCompatActivity).hideKeyboard()
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
        }
    }

    private fun getLawnTipsWithPaging() {
        listlawntips.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (!isLoad) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == lawntips.size - 1) {
                        //bottom of list!

                        if (lawntipsViewModel?._isNextPageUrl?.value != null)
                            if (lawntipsViewModel?._isNextPageUrl?.value!!)
                                lawntipsViewModel?.getLawnTips("" + pageNo++)!!

                        Handler().postDelayed({
                            isLoad = !lawntipsViewModel!!._isNextPageUrl.value!!
                        }, 500)

                    }
                }
            }
        })
    }


}

