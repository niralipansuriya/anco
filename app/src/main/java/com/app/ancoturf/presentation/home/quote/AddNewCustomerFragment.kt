package com.app.ancoturf.presentation.home.quote

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.quote.remote.entity.response.AddEditQuoteResponse
import com.app.ancoturf.data.quote.remote.entity.response.QuoteDetailsResponse
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_add_business_details.*
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject

class AddNewCustomerFragment() :
    BaseFragment(),
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var quoteViewModel: QuoteViewModel? = null

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun getContentResource(): Int = R.layout.fragment_add_new_customer

    override fun viewModelSetup() {
        quoteViewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)[QuoteViewModel::class.java]
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
        txtSubmitBusinessDetail.setOnClickListener(this)
//        Utility.showProgress(requireContext() , "" , false)

    }


    private fun initObservers() {

        quoteViewModel!!.errorLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if(it != null) {
            }
        })
    }


    override fun onClick(view: View?) {
        if (view == null) return
        when(view.id) {
            R.id.txtSubmitBusinessDetail -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    AddCustomerInfoFragment(QuoteDetailsResponse() ,  AddEditQuoteResponse()),
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
        }
    }
}
