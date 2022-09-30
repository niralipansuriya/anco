package com.app.ancoturf.presentation.home.quote

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.Constants.KEY_QUOTE_ID
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.quote.remote.entity.response.QuoteDetailsResponse
import com.app.ancoturf.domain.common.ErrorConstants
import com.app.ancoturf.domain.common.UseCaseConstants
import com.app.ancoturf.extension.hideKeyboard
import com.app.ancoturf.extension.openLogoutAlertDialog
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.home.HomeFragment
import com.app.ancoturf.presentation.home.order.interfaces.OnBack
import com.app.ancoturf.presentation.home.quote.adapters.QuotesAdapter
import com.app.ancoturf.presentation.home.quote.filter.QuoteFilterFragment
import com.app.ancoturf.presentation.home.quote.interfaces.OnQuoteButtonsClickedListener
import com.app.ancoturf.presentation.notification.NotificationFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_quotes.*
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject

class QuotesFragment() :
    BaseFragment(),
    View.OnClickListener {

    private var dialogSendQuote: Dialog? = null
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var quoteViewModel: QuoteViewModel? = null

    var quotesAdapter: QuotesAdapter? = null
    var quotes: ArrayList<QuoteDetailsResponse> = ArrayList()

    var showAll: Boolean = false

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private var isBack = false
    var pageNo = 1
    private var isLoad = false
    var itemCount = 0

    override fun getContentResource(): Int = R.layout.fragment_quotes

    override fun viewModelSetup() {
        quoteViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[QuoteViewModel::class.java]
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

        Utility.showProgress(requireContext(), "", false)
        listQuotes.visibility = View.GONE

        quoteViewModel?.callGetQuotes()
        txtGetStarted.setOnClickListener(this)
        txtAddQuote.setOnClickListener(this)
        imgFilter.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        txtShowAllQuotes.setOnClickListener(this)

        arguments?.let {
            val bundle = Bundle()
            bundle.putInt("quoteMode", Constants.VIEW_QUOTE)
            bundle.putInt("quoteId", arguments?.getString(KEY_QUOTE_ID)?.toInt()!!)
            (requireActivity() as AppCompatActivity).pushFragment(
                AddEditQuoteFragment().apply { arguments = bundle },
                true,
                true,
                false,
                R.id.flContainerHome
            )
            arguments = null
        }
    }

    private fun initObservers() {
        quoteViewModel!!.quotesLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                if (quotes == null)
                    quotes = ArrayList()

                if (pageNo == 1 || isBack || quoteViewModel?.filterAvailable!!) {
                    quotes.clear()
                    quotes.addAll(it)
                    setAdapter()
                } else
                    quotesAdapter?.addItems(it)

                quoteViewModel!!._quotesLiveData.value = null
            }
        })

        quoteViewModel!!.addEditQuotesLiveData.observe(this, Observer {
            if (it != null) {
                quoteViewModel!!._addEditQuotesLiveData.value = null
                if (dialogSendQuote != null && dialogSendQuote!!.isShowing)
                    dialogSendQuote?.dismiss()
                quoteViewModel!!.callGetQuotes()
            }
        })

        quoteViewModel!!.duplicateQuotesLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                val bundle = Bundle()
                bundle.putInt("quoteMode", Constants.EDIT_QUOTE)
                bundle.putInt("quoteId", it.id)
                (requireActivity() as AppCompatActivity).pushFragment(
                    AddEditQuoteFragment().apply { arguments = bundle },
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
                quoteViewModel!!._duplicateQuotesLiveData.value = null
            }
        })

        quoteViewModel!!.errorLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
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
                    it?.let { it1 -> shortToast(it1) }
                    if (it.equals("No quotes found")) {
                        if (quotes == null)
                            quotes = ArrayList()
                        quotes.clear()
                        setAdapter()
                    }
                }
                quoteViewModel?._errorLiveData?.value = null
            }
        })
    }

    private fun setAdapter() {
        if (quotes != null && quotes.size > 0) {
            quotesAdapter = activity?.let {
                QuotesAdapter(
                    it as AppCompatActivity, quotes, object : OnQuoteButtonsClickedListener {
                        override fun onClickOfResend(quote: QuoteDetailsResponse) {
                            openSendQuoteDialog(quote.id)
                        }

                        override fun onClickOfDuplicate(quote: QuoteDetailsResponse) {
                            Utility.showProgress(requireContext(), "", false)
                            quoteViewModel?.callDuplicateQuote(quoteId = quote.id)
                        }

                    },
                    showAll,
                    object : OnBack {
                        override fun onClickOfBack(isBack: Boolean) {
                            this@QuotesFragment.isBack = isBack
                        }
                    }
                )
            }!!
            listQuotes.layoutManager = LinearLayoutManager(requireActivity())
            listQuotes.adapter = quotesAdapter
            txtNoQuotes.visibility = View.GONE
            layoutNoQuotes.visibility = View.GONE
            listQuotes.visibility = View.VISIBLE
            txtAddQuote.visibility = View.VISIBLE
            imgFilter.visibility = View.VISIBLE
            if (!showAll) {
                txtSubTitle.visibility = View.VISIBLE
                txtShowAllQuotes.visibility = View.VISIBLE
            } else {
                txtSubTitle.visibility = View.GONE
                txtShowAllQuotes.visibility = View.GONE
            }
        } else {
            if (quoteViewModel!!.isFilterApplied()) {
                txtNoQuotes.visibility = View.VISIBLE
                layoutNoQuotes.visibility = View.GONE
                listQuotes.visibility = View.GONE
                txtAddQuote.visibility = View.GONE
                imgFilter.visibility = View.VISIBLE
                txtSubTitle.visibility = View.GONE
                txtShowAllQuotes.visibility = View.GONE
            } else {
                txtNoQuotes.visibility = View.GONE
                layoutNoQuotes.visibility = View.VISIBLE
                listQuotes.visibility = View.GONE
                txtAddQuote.visibility = View.GONE
                imgFilter.visibility = View.GONE
                txtSubTitle.visibility = View.GONE
                txtShowAllQuotes.visibility = View.GONE
            }
        }
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {
            R.id.imgBack -> {
                (requireActivity() as AppCompatActivity).hideKeyboard()
                requireActivity().supportFragmentManager.popBackStack()
            }
            R.id.imgFilter -> {
                isBack = true
                pageNo = 1
                (requireActivity() as AppCompatActivity).pushFragment(
                    QuoteFilterFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.txtShowAllQuotes -> {
                showAll = true
                setAdapter()
                //pagination
                getQuotesWithPaging()
            }

            R.id.txtGetStarted -> {
//                (requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    AddBusinessDetailsFragment(null),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.txtAddQuote -> {
//                (requireActivity() as AppCompatActivity).hideKeyboard()
                (requireActivity() as AppCompatActivity).pushFragment(
                    AddBusinessDetailsFragment(null),
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

    private fun getQuotesWithPaging() {
        listQuotes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (!isLoad) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == quotes.size - 1) {
                        //bottom of list!
                        pageNo++

                        if (quoteViewModel?._isNextPageUrl?.value != null)
                            if (quoteViewModel?._isNextPageUrl?.value!!)
                                quoteViewModel?.callGetAllQuotesWithPaging("" + pageNo)!!

                        Handler().postDelayed({
                            isLoad = !quoteViewModel?._isNextPageUrl?.value!!
                        }, 500)

                    }
                }
            }
        })
    }

    private fun openSendQuoteDialog(quoteId: Int) {
        dialogSendQuote = Dialog(requireContext())
        dialogSendQuote?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogSendQuote?.setContentView(R.layout.dialog_send_quote)
        dialogSendQuote?.show()

        val window = dialogSendQuote?.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)

        val txtTitle = dialogSendQuote?.findViewById(R.id.txtTitle) as TextView
        txtTitle.text = getString(R.string.resend_quote_title)
        val txtUserEmail = dialogSendQuote?.findViewById(R.id.txtUserEmail) as TextView
        txtUserEmail.text = sharedPrefs.userEmail
        val txtConfirm = dialogSendQuote?.findViewById(R.id.txtConfirm) as TextView
        txtConfirm.setOnClickListener {
            if (Utility.isValueNull(txtUserEmail.text.toString()))
                shortToast(getString(R.string.blank_email_message))
            else {
                var sendToQuote = ArrayList<String>()
//            dialogSendQuote?.dismiss()
                sendToQuote.add(txtUserEmail.text.toString())
                Utility.showProgress(requireContext(), "", false)
                quoteViewModel?.callResendQuote(quoteId, sendToQuote)
            }
        }

        val imgClose = dialogSendQuote?.findViewById(R.id.imgClose) as ImageView
        imgClose.setOnClickListener {
            dialogSendQuote?.dismiss()
        }

        val txtCancel = dialogSendQuote?.findViewById(R.id.txtCancel) as TextView
        txtCancel.setOnClickListener {
            dialogSendQuote?.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        quoteViewModel?.data?.put(UseCaseConstants.STATUS, "")
        quoteViewModel?.data?.put(UseCaseConstants.COST_MIN, "")
        quoteViewModel?.data?.put(UseCaseConstants.COST_MAX, "")
        quoteViewModel?.data?.put(UseCaseConstants.ADDRESS, "")
        quoteViewModel?.data?.put(UseCaseConstants.SORT_BY, "")
    }
}
