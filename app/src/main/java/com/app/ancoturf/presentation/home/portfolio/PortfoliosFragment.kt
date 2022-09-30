package com.app.ancoturf.presentation.home.portfolio

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.portfolio.remote.entity.PortfolioResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.openLogoutAlertDialog
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.portfolio.adapters.PortfolioAdapter
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_portfolio.*
import kotlinx.android.synthetic.main.header.*
import java.util.*
import javax.inject.Inject


class PortfoliosFragment :
    BaseFragment(),
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var portfolioViewModel: PortfolioViewModel? = null

    var portfolioAdapter: PortfolioAdapter? = null
    var portfolios: ArrayList<PortfolioResponse.Data> = ArrayList()

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun getContentResource(): Int = R.layout.fragment_portfolio

    override fun viewModelSetup() {
        portfolioViewModel = ViewModelProviders.of(
            requireActivity(),
            viewModelFactory
        )[PortfolioViewModel::class.java]
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

        portfolioViewModel!!.callGetPortfolios()
//        setAdapter()
        Utility.showProgress(context = requireActivity(), message = "", cancellable = false)
        txtAddPortfolio.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgCart.setOnClickListener(this)
    }

    private fun setAdapter() {

        if (portfolios != null && portfolios.size > 0) {
            txtNoPortfolio.visibility = View.GONE
            listPortfolios.visibility = View.VISIBLE
            portfolioAdapter = activity?.let {
                PortfolioAdapter(
                    it as AppCompatActivity,
                    portfolios
                )
            }!!
            listPortfolios.adapter = portfolioAdapter


        } else {
            txtNoPortfolio.visibility = View.VISIBLE
            listPortfolios.visibility = View.GONE
        }
    }

    private fun initObservers() {
        portfolioViewModel!!.portfoliosLiveData.observe(this, Observer {
            if (portfolios == null)
                portfolios = ArrayList()
            portfolios.clear()
            portfolios.addAll(it.data)
            setAdapter()
        })

        portfolioViewModel!!.errorLiveData.observe(this, Observer {
            if (!Utility.isValueNull(portfolioViewModel!!.errorLiveData.value)) {
                if (it.equals(ErrorConstants.UNAUTHORIZED_ERROR_CODE)) {
                    if (activity is HomeActivity) {
                        (activity as HomeActivity).cartViewModel?.deleteProduct(null)
                        (activity as HomeActivity).cartViewModel?.deleteCoupon(null)
                    } else if (activity is PaymentActivity) {
                        (activity as PaymentActivity).cartViewModel?.deleteProduct(null)
                        (activity as PaymentActivity).cartViewModel?.deleteCoupon(null)
                    } else if (activity is ProfileActivity) {
                        (activity as ProfileActivity).cartViewModel?.deleteProduct(null)
                        (activity as ProfileActivity).cartViewModel?.deleteCoupon(null)
                    }
                    (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                } else {
//                it?.let { it1 -> shortToast(it1) }
                    setAdapter()
                }
                portfolioViewModel!!._errorLiveData.value = null
            }
        })
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {
            R.id.txtAddPortfolio -> {
                if (portfolios != null && portfolios.size < sharedPrefs.max_number_portfolios) {
                    (requireActivity() as AppCompatActivity).hideKeyboard()
                    val bundle = Bundle()
                    bundle.putInt("portfolioMode", Constants.ADD_PORTFOLIO)
                    bundle.putInt("portfolioId", 0)
                    (requireActivity() as AppCompatActivity).pushFragment(
                        AddEditPortfolioFragment().apply { arguments = bundle },
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                } else {
                    shortToast("You can add maximum " + sharedPrefs.max_number_portfolios + " portfolios")
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
        }
    }


}
