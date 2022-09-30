package com.app.ancoturf.presentation.manageLawn

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.app.ancoturf.R
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.offer.ClsQuickLinks
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.adapters.QuickLinksAdapter
import com.app.ancoturf.presentation.home.interfaces.IDataPositionWise
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject


class HowToLayTurfFragment : BaseFragment(),  View.OnClickListener, IDataPositionWise {

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    lateinit var quickLinksAdapter: QuickLinksAdapter
    var quickLinkList: java.util.ArrayList<ClsQuickLinks> = java.util.ArrayList()

    override fun getContentResource(): Int = R.layout.fragment_how_to_lay_turf


    override fun viewModelSetup() {
    }

    override fun viewSetup() {
        if (activity is HomeActivity) {
            (activity as HomeActivity).hideShowFooter(true)
            (activity as HomeActivity).showCartDetails(imgCart, txtCartProducts, false)
        }
        Utility.showProgress(requireContext(), "", false)
        imgMore.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
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
    override fun getDataFromPos(position: Int, name: String) {
    }
}