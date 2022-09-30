package com.app.ancoturf.presentation.profile

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.account.remote.entity.response.UserInfo
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.cart.CartViewModel
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.order.OrderFragment
import com.app.ancoturf.presentation.login.LoginActivity
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.android.synthetic.main.header.*
import okhttp3.internal.Util
import javax.inject.Inject

class ChangePasswordFragment : BaseFragment(), View.OnClickListener {

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var cartViewModel: CartViewModel? = null
    var profileViewModel: ProfileViewModel? = null
    var userInfo: UserInfo? = null

    override fun getContentResource(): Int = R.layout.fragment_change_password

    override fun viewModelSetup() {
        cartViewModel = ViewModelProviders.of(this, viewModelFactory)[CartViewModel::class.java]
        profileViewModel = ViewModelProviders.of(this, viewModelFactory)[ProfileViewModel::class.java]
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

        imgLogo.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        txtChangePassword.setOnClickListener(this)
        imgBack.setOnClickListener(this)

        Utility.showProgress(requireContext(), "", false)
        profileViewModel?.callGetUserDetails()

    }

    private fun initObservers() {

        profileViewModel!!.changePasswordLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null) {
                if(it.success)  {
                    shortToast(it.message)
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    shortToast(it.message)
                }
                profileViewModel!!._changePasswordLiveData.value = null
            }
        })
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.imgBell -> {
                openNotificationFragment()
            }
            R.id.imgBack -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
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

            R.id.imgLogo -> {
                requireActivity().startActivity(Intent(requireActivity() , HomeActivity::class.java))
                requireActivity().finishAffinity()
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

            R.id.txtChangePassword -> {
                if(Utility.isValueNull(edtCurrentPassword.text.toString())) {
                    shortToast("Please enter your current password")
                } else if(Utility.isValueNull(edtNewPassword.text.toString())) {
                    shortToast("Please create a new password")
                }  else if(Utility.isValueNull(edtConfirmPassword.text.toString())) {
                    shortToast("Please confirm your new password")
                } else if (!edtNewPassword.text.toString().equals(edtConfirmPassword.text.toString())) {
                    shortToast("Snap, the passwords you entered don't match")
                } else if (edtNewPassword.text.toString().equals(edtCurrentPassword.text.toString())) {
                    shortToast("The old password and new password should not be same")
                } else {
                    profileViewModel?.callChangePassword(edtCurrentPassword.text.toString() , edtNewPassword.text.toString())
                }
            }

        }
    }
}
