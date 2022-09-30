package com.app.ancoturf.presentation.home.quote

import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.quote.remote.entity.response.AddEditQuoteResponse
import com.app.ancoturf.data.quote.remote.entity.response.CustomersDataResponse
import com.app.ancoturf.data.quote.remote.entity.response.QuoteDetailsResponse
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
import com.app.ancoturf.presentation.home.quote.adapters.CustomersAdapter
import com.app.ancoturf.presentation.notification.NotificationFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_add_business_details.*
import kotlinx.android.synthetic.main.fragment_add_customer_info.*
import kotlinx.android.synthetic.main.fragment_add_customer_info.imgBack
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject

class AddCustomerInfoFragment(private var quote: QuoteDetailsResponse?, private var addEditquote: AddEditQuoteResponse?) :
    BaseFragment(),
    View.OnClickListener {

    private var dialog: Dialog? = null
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var quoteViewModel: QuoteViewModel? = null

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    var customersAdapter: CustomersAdapter? = null
    var customers: ArrayList<CustomersDataResponse.Data> = ArrayList()

    override fun getContentResource(): Int = R.layout.fragment_add_customer_info

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
        txtAddCustomer.setOnClickListener(this)
        txtMiddleAddCustomer.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        imgBack.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        txtConfirm.setOnClickListener(this)
        Utility.showProgress(requireContext(), "", false)
        quoteViewModel?.callGetCustomers()
        setAdapter()
    }

    private fun initObservers() {
        quoteViewModel!!.errorLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null) {
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
                    if (customers == null)
                        customers = ArrayList()
                    customers.clear()
                    setAdapter()
                }
                quoteViewModel!!._errorLiveData.value = null
            }
        })

        quoteViewModel!!.customersLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null) {
                if (customers == null)
                    customers = ArrayList()
                customers.clear()
                customers.addAll(it.data)
                setAdapter()
            }
        })

        quoteViewModel!!.addEditQuotesLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null && it.data != null) {
                shortToast(it.message)
                if (dialog != null && dialog!!.isShowing)
                    dialog!!.dismiss()
//                (requireActivity() as AppCompatActivity).hideKeyboard()
                val bundle = Bundle()
                bundle.putInt("quoteMode" , Constants.ADD_QUOTE)
                bundle.putInt("quoteId" , it.data!!.id)
                (requireActivity() as AppCompatActivity).pushFragment(
                    AddEditQuoteFragment().apply { arguments = bundle },
                    false,
                    true,
                    false,
                    R.id.flContainerHome
                )
                quoteViewModel!!._addEditQuotesLiveData.value = null
            }
        })
    }

    private fun setAdapter() {
        if (customers != null && customers.size > 0) {
            customersAdapter = activity?.let {
                CustomersAdapter(
                    it as AppCompatActivity,
                    customers
                )
            }!!
            listCustomers.adapter = customersAdapter
            listCustomers.visibility = View.VISIBLE
            txtMiddleAddCustomer.visibility = View.GONE
            txtAddCustomer.visibility = View.VISIBLE
            txtConfirm.visibility = View.VISIBLE
        } else {
            listCustomers.visibility = View.GONE
            txtMiddleAddCustomer.visibility = View.VISIBLE
            txtAddCustomer.visibility = View.GONE
            txtConfirm.visibility = View.GONE
        }
    }

    private fun openAddNewCustomerDialog() {
        dialog = Dialog(requireContext())
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(R.layout.fragment_add_new_customer)
        dialog?.show()

        val window = dialog?.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)

        val edtCustomerName = dialog?.findViewById(R.id.edtCustomerName) as EditText
        val edtAddress = dialog?.findViewById(R.id.edtAddress) as EditText
        val edtEmailAddress = dialog?.findViewById(R.id.edtEmailAddress) as EditText
        val edtMobileNum = dialog?.findViewById(R.id.edtMobileNum) as EditText
        val edtPhoneNum = dialog?.findViewById(R.id.edtPhoneNum) as EditText

        val imgClose = dialog?.findViewById(R.id.imgClose) as ImageView
        imgClose.setOnClickListener {
            dialog?.dismiss()
        }

        val txtSubmit = dialog?.findViewById(R.id.txtSubmit) as TextView
        txtSubmit.setOnClickListener {
            if (Utility.isValueNull(edtCustomerName.text.toString())) {
                shortToast(getString(R.string.blank_customer_name_message))
            } else if (Utility.isValueNull(edtAddress.text.toString())) {
                shortToast(getString(R.string.blank_customer_address_message))
            } else if (Utility.isValueNull(edtEmailAddress.text.toString())) {
                shortToast(getString(R.string.blank_customer_email_address_message))
            } else if (!Patterns.EMAIL_ADDRESS.matcher(edtEmailAddress.text.toString()).matches()) {
                shortToast(getString(R.string.invalid_customer_email_address_message))
            } else if (Utility.isValueNull(edtMobileNum.text.toString())) {
                shortToast(getString(R.string.blank_customer_mobile_num_message))
            } else if (edtMobileNum.text.toString().length < 10) {
                shortToast(getString(R.string.invalid_customer_mobile_num_message))
            } else {
                Utility.showProgress(requireContext(), "", false)
                if (quote != null) {
                    quoteViewModel?.callAddEditCustomer(
                        quote!!.id,
                        0,
                        edtCustomerName.text.toString(),
                        edtAddress.text.toString(),
                        edtEmailAddress.text.toString(),
                        edtMobileNum.text.toString(),
                        edtPhoneNum.text.toString(),
                        quote!!.users.business.contactName,
                        quote!!.users.business.businessName,
                        quote!!.users.business.abn,
                        quote!!.users.business.address,
                        quote!!.users.business.mobileNumber,
                        quote!!.users.business.phoneNumber,
                        quote!!.users.business.email,
                        quote!!.users.business.webUrl,
                        quote!!.users.business.paymentTerms,
                        quote!!.users.business.disclaimer,
                        quote!!.users.business.registeredForGst
                    )
                } else if (addEditquote != null){
                    quoteViewModel?.callAddEditCustomer(
                        addEditquote!!.id,
                        0,
                        edtCustomerName.text.toString(),
                        edtAddress.text.toString(),
                        edtEmailAddress.text.toString(),
                        edtMobileNum.text.toString(),
                        edtPhoneNum.text.toString(),
                        addEditquote!!.users.business.contactName,
                        addEditquote!!.users.business.businessName,
                        addEditquote!!.users.business.abn,
                        addEditquote!!.users.business.address,
                        addEditquote!!.users.business.mobileNumber,
                        addEditquote!!.users.business.phoneNumber,
                        addEditquote!!.users.business.email,
                        addEditquote!!.users.business.webUrl,
                        addEditquote!!.users.business.paymentTerms,
                        addEditquote!!.users.business.disclaimer,
                        addEditquote!!.users.business.registeredForGst
                    )
                }
            }
        }
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {
            R.id.imgBell ->{
                openNotificationFragment()
            }
            R.id.txtAddCustomer , R.id.txtMiddleAddCustomer -> {
                openAddNewCustomerDialog()
            }

            R.id.txtConfirm -> {
                var selectedCustomer: CustomersDataResponse.Data? = customersAdapter?.getSelectedData()
                if (selectedCustomer != null) {
                    Utility.showProgress(requireContext(), "", false)
                    if (quote != null) {
                        quoteViewModel?.callAddEditCustomer(
                            quote!!.id,
                            selectedCustomer.id,
                            selectedCustomer.customerName,
                            selectedCustomer.customerAddress,
                            selectedCustomer.customerEmail,
                            selectedCustomer.customerMobile,
                            selectedCustomer.customerPhone,
                            quote!!.users.business.contactName,
                            quote!!.users.business.businessName,
                            quote!!.users.business.abn,
                            quote!!.users.business.address,
                            quote!!.users.business.mobileNumber,
                            quote!!.users.business.phoneNumber,
                            quote!!.users.business.email,
                            quote!!.users.business.webUrl,
                            quote!!.users.business.paymentTerms,
                            quote!!.users.business.disclaimer,
                            quote!!.users.business.registeredForGst
                        )
                    } else if (addEditquote != null){
                        quoteViewModel?.callAddEditCustomer(
                            addEditquote!!.id,
                            selectedCustomer.id,
                            selectedCustomer.customerName,
                            selectedCustomer.customerAddress,
                            selectedCustomer.customerEmail,
                            selectedCustomer.customerMobile,
                            selectedCustomer.customerPhone,
                            addEditquote!!.users.business.contactName,
                            addEditquote!!.users.business.businessName,
                            addEditquote!!.users.business.abn,
                            addEditquote!!.users.business.address,
                            addEditquote!!.users.business.mobileNumber,
                            addEditquote!!.users.business.phoneNumber,
                            addEditquote!!.users.business.email,
                            addEditquote!!.users.business.webUrl,
                            addEditquote!!.users.business.paymentTerms,
                            addEditquote!!.users.business.disclaimer,
                            addEditquote!!.users.business.registeredForGst
                        )
                    }
                } else {
                    shortToast(getString(R.string.customer_not_selected_message))
                }
            }

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
}
