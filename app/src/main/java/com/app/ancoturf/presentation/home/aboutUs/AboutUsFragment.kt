package com.app.ancoturf.presentation.home.aboutUs

import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
import com.app.ancoturf.presentation.home.lawntips.adapter.LawnTipsAdapter
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_about_us.*

import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject


/**
 * A simple [Fragment] subclass.
 * Use the [AboutUsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AboutUsFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var aboutUsViewModel: AboutUsViewModel? = null

//    var lawntipsAdapter: LawnTipsAdapter? = null
    var aboutUsAdapter: AboutUsAdapter? = null
    var lawntips: ArrayList<LawnTipsDataResponse.Data> = ArrayList()
    var pageNo = 1
    private var isLoad = false
    var isBack = false
    var itemCount = 0

    override fun getContentResource(): Int = R.layout.fragment_about_us

    override fun viewModelSetup() {
        aboutUsViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[AboutUsViewModel::class.java]
        initObservers()
    }

    private fun setAdapter() {
        if (lawntips != null && lawntips.size > 0) {
            aboutUsAdapter = activity?.let {
                AboutUsAdapter(
                    it as AppCompatActivity, lawntips,false
                )
            }!!
            listAboutUs.adapter = aboutUsAdapter
            txtAboutUs.visibility = View.GONE
            listAboutUs.visibility = View.VISIBLE
        }
    }

    private fun initObservers() {
        aboutUsViewModel?.lawntipsLiveData?.observe(this, Observer {

            if (it != null) {
                Utility.cancelProgress()
                if (lawntips == null)
                    lawntips = ArrayList()

                if (pageNo == 1 || isBack) {
                    lawntips.clear()
                    lawntips.addAll(it.data)
                    setAdapter()
                } else
                    aboutUsAdapter?.addItems(it.data)

                aboutUsViewModel!!._lawntipsLiveData.value = null
            }
        })
        aboutUsViewModel!!.errorLiveData.observe(this, Observer {
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
                aboutUsViewModel?._errorLiveData?.value = null
            }
        })

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
            Utility.showProgress(requireContext(), "", false)
            aboutUsViewModel?.getAboutUs("1")
            aboutUsAdapter = AboutUsAdapter(activity as AppCompatActivity, lawntips,false)
            listAboutUs.adapter = aboutUsAdapter
            imgMore.setOnClickListener(this)
            imgCart.setOnClickListener(this)
            imgBell.setOnClickListener(this)
            imgBack.setOnClickListener(this)
            imgSearch.setOnClickListener(this)
            imgLogo.setOnClickListener(this)
        //pagination
        getAboutUsWithPaging()
    }

    private fun getAboutUsWithPaging() {
        listAboutUs.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (!isLoad) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == lawntips.size - 1) {
                        //bottom of list!

                        if (aboutUsViewModel?._isNextPageUrl?.value != null)
                            if (aboutUsViewModel?._isNextPageUrl?.value!!)
                                aboutUsViewModel?.getAboutUs("" + pageNo++)!!

                        Handler().postDelayed({
                            isLoad = !aboutUsViewModel!!._isNextPageUrl.value!!
                        }, 500)

                    }
                }
            }
        })
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgBack -> {
                requireActivity().supportFragmentManager.popBackStack()
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

            R.id.imgSearch -> {
                (requireActivity() as AppCompatActivity).pushFragment(
                    SearchFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }
            R.id.imgBell -> {
                openNotificationFragment()
            }

        }
    }

}