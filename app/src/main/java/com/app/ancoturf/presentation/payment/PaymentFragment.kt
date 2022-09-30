package com.app.ancoturf.presentation.payment

import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.app.ancoturf.R
import com.app.ancoturf.data.common.Constants
import com.app.ancoturf.data.common.local.SharedPrefs
import com.app.ancoturf.data.payment.remote.entity.BillHistoryResponse
import com.app.ancoturf.data.payment.remote.entity.PendingPaymentResponse
import com.app.ancoturf.extension.pushFragment
import com.app.ancoturf.extension.shortToast
import com.app.ancoturf.presentation.cart.CartFragment
import com.app.ancoturf.presentation.cart.CartViewModel
import com.app.ancoturf.presentation.common.base.BaseFragment
import com.app.ancoturf.presentation.contactus.ContactFragment
import com.app.ancoturf.presentation.home.HomeActivity
import com.app.ancoturf.presentation.invoice.InvoiceFragment
import com.app.ancoturf.presentation.payment.adapter.BillHistoryAdapter
import com.app.ancoturf.presentation.payment.adapter.PendingPaymentAdapter
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_payment.*
import kotlinx.android.synthetic.main.header.*
import javax.inject.Inject

class PaymentFragment : BaseFragment(),
    View.OnClickListener, BillHistoryAdapter.TxtBillOnClick {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var paymentViewModel: PaymentViewModel? = null

    private var cartViewModel: CartViewModel? = null

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    private var pendingPayments: ArrayList<PendingPaymentResponse.Invoices> = ArrayList()

    private var pendingPaymentAdapter: PendingPaymentAdapter? = null

    private var totalPendingAmount = 0.0F

    private var billHistory: ArrayList<BillHistoryResponse.Data> = ArrayList()

    private var billHistoryAdapter: BillHistoryAdapter? = null

    private var isLoad = false
    var pageNo = 1


    override fun getContentResource(): Int = R.layout.fragment_payment

    override fun viewModelSetup() {
        paymentViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[PaymentViewModel::class.java]

        cartViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[CartViewModel::class.java]

        initObservers()
        Utility.showProgress(context = requireContext(), message = "", cancellable = false)
//        paymentViewModel!!.callGetPendingPayment()
        paymentViewModel!!.callGetBillHistory("")
        getProductByPaging()
    }

    private fun initObservers() {

        /*paymentViewModel!!.pendingPaymentLiveData.observe(this, Observer {
            if (it != null) {
                Log.e("resp", it.toString())
                if (pendingPayments == null) {
                    pendingPayments = ArrayList()
                }
                pendingPayments.clear()
                pendingPayments.addAll(it.data)
                totalPendingAmount = it.total_pending_amount
                txtPendingPaymentAmount.text =  activity?.getString(R.string.pending_amount_price, Utility.formatNumber(Utility.roundTwoDecimals(it.total_pending_amount).toFloat()))
                setAdapter()
            } else {
                txtPendingPaymentAmount.text =  activity?.getString(R.string.pending_amount_price, " 0.0")
                pendingPayments.clear()
                setAdapter()
            }

        })*/

        paymentViewModel!!.billHistoryLiveData.observe(this, Observer {
            if (it != null) {
                Log.e("resp", it.toString())

                totalPendingAmount = it.total_pending_amount

                txtPendingPaymentAmount.text = activity?.getString(
                    R.string.pending_amount_price,
                    Utility.formatNumber(Utility.roundTwoDecimals(it.total_pending_amount).toFloat())
                )
                if (pageNo == 1) {
                    billHistory.clear()
                    billHistory.addAll(it.data)
                    setAdapter()
                } else {
                    billHistoryAdapter?.addItems(it.data)
                }

            } else {
                txtPendingPaymentAmount.text =
                    activity?.getString(R.string.pending_amount_price, " 0.0")
                billHistory.clear()
                setAdapter()
            }

        })
    }

    private fun setAdapter() {
        Utility.cancelProgress()
        /*if (pendingPayments != null && pendingPayments.size > 0) {
            pendingPaymentAdapter = activity?.let {
                PendingPaymentAdapter(
                    it as AppCompatActivity,
                    pendingPayments
                )
            }!!
            listPendingBill.adapter = pendingPaymentAdapter
            listPendingBill.visibility = View.VISIBLE
        } else {
            listPendingBill.visibility = View.GONE
        }*/

        if (billHistory != null && billHistory.size > 0) {
            billHistoryAdapter = activity?.let {
                BillHistoryAdapter(
                    it as AppCompatActivity,
                    billHistory,
                    this
                )
            }!!
            listPendingBill.adapter = billHistoryAdapter
            listPendingBill.visibility = View.VISIBLE
        } else {
            listPendingBill.visibility = View.GONE
        }
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

        imgLogo.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgSearch.setOnClickListener(this)

//        txtViewBillHistory.setOnClickListener(this)
        txtMakePayment.setOnClickListener(this)

        if (sharedPrefs?.userType == Constants.WHOLESALER) {
            txtPaymentAmountLabel.visibility == VISIBLE
            txtPendingPaymentAmount.visibility = VISIBLE
            txtMakePayment.visibility = VISIBLE
        } else {
            txtPaymentAmountLabel.visibility == GONE
            txtPendingPaymentAmount.visibility = GONE
            txtMakePayment.visibility = GONE
        }
    }

    override fun onClick(view: View?) {
        if (view == null) return
        when (view.id) {
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

            R.id.imgLogo -> {
                requireActivity().startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finishAffinity()
            }


            /* R.id.txtViewBillHistory -> {
                     (requireActivity() as AppCompatActivity).pushFragment(
                         BillHistoryFragment(),
                         true,
                         true,
                         false,
                         R.id.flContainerHome
                     )
             }*/
            R.id.txtMakePayment -> {
                if (totalPendingAmount > 0) {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        LastPaymentFragment(),
                        true,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                } else {
                    shortToast("There is no pending payment")
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


    private fun getProductByPaging() {
        /* listPendingBill.addOnScrollListener(object : RecyclerView.OnScrollListener() {
             override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                 super.onScrollStateChanged(recyclerView, newState)
             }

             override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                 super.onScrolled(recyclerView, dx, dy)

                 val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                 if (!isLoad) {
                     if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == billHistory.size - 1) {
                         //bottom of list!
                         pageNo++
                         if (paymentViewModel?._isNextPageUrl?.value != null){
                             if (paymentViewModel?._isNextPageUrl?.value!!)
                                 paymentViewModel?.callGetBillHistory("$pageNo")!!
                             Handler().postDelayed({
                                 isLoad = !paymentViewModel!!._isNextPageUrl.value!!
                             }, 500)

                         }


                     }
                 }
             }
         })*/


        nestedScroll.getViewTreeObserver()
            .addOnScrollChangedListener(ViewTreeObserver.OnScrollChangedListener {
                if (nestedScroll != null) {

                    val view = nestedScroll.getChildAt(nestedScroll.getChildCount() - 1) as View

                    val diff = view.bottom - (nestedScroll.getHeight() + nestedScroll
                        .getScrollY())

                    if (diff == 0 && !isLoad) {
                        // your pagination code
                        if (paymentViewModel?._isNextPageUrl?.value != null) {
                            pageNo++
                            if (paymentViewModel?._isNextPageUrl?.value!!)
                                paymentViewModel?.callGetBillHistory("$pageNo")!!

                            Handler().postDelayed({
                                isLoad = !paymentViewModel!!._isNextPageUrl.value!!
                            }, 500)
                        }
                    }
                }
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        /*pageNo = 1
        paymentViewModel!!._billHistoryLiveData.value = null*/
    }

    override fun onClick(pdfUrl: String) {
        (requireActivity() as AppCompatActivity).pushFragment(
            InvoiceFragment(pdfUrl),
            true,
            true,
            false,
            R.id.flContainerHome
        )
    }
}
