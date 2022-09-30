package com.app.ancoturf.presentation.home.tracking

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
import com.app.ancoturf.data.order.remote.entity.response.OrderDetailResponse
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
import com.app.ancoturf.presentation.home.order.OrderViewModel
import com.app.ancoturf.presentation.home.order.adapters.OrderAdapter
import com.app.ancoturf.presentation.home.order.filter.OrderFilterFragment
import com.app.ancoturf.presentation.home.order.interfaces.OnBack
import com.app.ancoturf.presentation.home.tracking.adapters.TrackingAdapter
import com.app.ancoturf.presentation.notification.NotificationFragment
import com.app.ancoturf.presentation.payment.PaymentActivity
import com.app.ancoturf.presentation.profile.ProfileActivity
import com.app.ancoturf.presentation.search.SearchFragment
import com.app.ancoturf.utils.Utility
import kotlinx.android.synthetic.main.fragment_order.*
import kotlinx.android.synthetic.main.header.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class TrackingFragment() : BaseFragment(),
    View.OnClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var orderViewModel: OrderViewModel? = null

    var orderAdapter: TrackingAdapter? = null
    var orders: ArrayList<OrderDetailResponse> = ArrayList()

    var showAll: Boolean = false

    private var isBack = false
    var pageNo = 1
    private var isLoad = false
    var itemCount = 0

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun getContentResource(): Int = R.layout.fragment_tracking
    override fun viewModelSetup() {
        orderViewModel =
            ViewModelProviders.of(requireActivity(), viewModelFactory)[OrderViewModel::class.java]
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

        listOrder.visibility = View.GONE

        if (!isBack || (orderViewModel?.filterAvailable!! && orderViewModel!!.isfilterApplied)) {
            Utility.showProgress(requireContext(), "", false)
            orderViewModel?.data?.put(UseCaseConstants.PRICE_MAX, "127800")
//            orderViewModel?.data?.put(UseCaseConstants.PER_PAGE, "1")
            orderViewModel?.data?.put(UseCaseConstants.DELIVERY_STATUS, "4")
            var date: String = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
//            orderViewModel?.data?.put(UseCaseConstants.DELIVERY_DATE, date)
            orderViewModel?.data?.put(UseCaseConstants.DELIVERY_DATE_FROM, date)
            orderViewModel?.callGetOrders()
        }
        imgFilter.setOnClickListener(this)
        imgMore.setOnClickListener(this)
        imgSearch.setOnClickListener(this)
        imgCart.setOnClickListener(this)
        imgBell.setOnClickListener(this)
        imgLogo.setOnClickListener(this)
        txtShowAllOrder.setOnClickListener(this)
        /*if (orderViewModel?.numberOfFilters != 0) {
            txtNumberOfFilters.visibility = View.VISIBLE
            txtNumberOfFilters.text = "" + orderViewModel?.numberOfFilters
            txtClearFilters.visibility = View.VISIBLE
            txtClearFilters.setOnClickListener(this)

        } else {
            txtNumberOfFilters.visibility = View.GONE
            txtClearFilters.visibility = View.GONE
        }
*/
        if (isBack)
            setAdapter()
        if (showAll)
            getOrderWithPaging()
        if (orderViewModel?.filterAvailable!! && orderViewModel!!.isfilterApplied) {
            pageNo = 1
            orders.clear()
        }
    }
    fun getDateFromTimeStamp(requiredFormat: String, value: Long): String {
        var string = ""
        try {
            val date = Date(value)
            val reqSimpleDateFormat = SimpleDateFormat(requiredFormat)
            string = reqSimpleDateFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return string
    }

    private fun initObservers() {
        orderViewModel!!.orderLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                if (orders == null)
                    orders = ArrayList()

                if (pageNo == 1) {
                    orders.clear()
                    orders.addAll(it)
                    setAdapter()
                } else
                    orderAdapter?.addItems(it)

                orderViewModel!!._orderLiveData.value = null
            }
        })



        orderViewModel!!.cancelOrderLiveData.observe(this, Observer {
            Utility.cancelProgress()
            if (it != null) {
                if (it.success) {

                    orders[orderViewModel!!.position].orderStatus.displayName =
                        it.data!!.orderStatus.displayName
                    orderAdapter?.notifyDataSetChanged()

                } else
                    shortToast(it.message)
                orderViewModel!!._rescheduleOrderLiveData.value = null
            }
        })

        orderViewModel!!.errorLiveData.observe(this, Observer {
            if (it != null) {
                Utility.cancelProgress()
                if (it.equals(ErrorConstants.UNAUTHORIZED_ERROR_CODE)) {
                    (requireActivity() as AppCompatActivity).openLogoutAlertDialog(sharedPrefs = sharedPrefs)
                } else {
                    it?.let { it1 -> shortToast(it1) }
                    if (it.equals("No order found")) {
                        if (orders == null)
                            orders = ArrayList()
                        orders.clear()
                        setAdapter()
                    }
                }
                orderViewModel?._errorLiveData?.value = null
            }
        })
    }


    private fun setAdapter() {
        if (orders != null && orders.size > 0) {
            orderAdapter = activity?.let {
                TrackingAdapter(
                    it as AppCompatActivity, orders, showAll,
                    object : OnBack {
                        override fun onClickOfBack(isBack: Boolean) {

                            this@TrackingFragment.isBack = isBack
                        }
                    }
                )
            }!!
            listOrder.adapter = orderAdapter
            txtNoQuotes.visibility = View.GONE
            listOrder.visibility = View.VISIBLE
            imgFilter.visibility = View.VISIBLE

            if (showAll) {
                txtSubTitle.text =
                    (activity as AppCompatActivity).resources.getString(R.string.order_all)
                txtShowAllOrder.visibility = View.GONE
            } else {
                txtSubTitle.visibility = View.VISIBLE
                txtShowAllOrder.visibility = View.VISIBLE
            }

        } else {
            if (orderViewModel!!.isFilterApplied()) {
                txtNoQuotes.visibility = View.VISIBLE
                listOrder.visibility = View.GONE
                imgFilter.visibility = View.VISIBLE
                txtSubTitle.visibility = View.GONE
                txtShowAllOrder.visibility = View.GONE

            } else {
                txtNoQuotes.visibility = View.VISIBLE
                listOrder.visibility = View.GONE
                imgFilter.visibility = View.GONE
                txtSubTitle.visibility = View.GONE
                txtShowAllOrder.visibility = View.GONE

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
                //pageNo = 1
                isBack = true
                orderViewModel!!.isfilterApplied = false
                (requireActivity() as AppCompatActivity).pushFragment(
                    OrderFilterFragment(),
                    true,
                    true,
                    false,
                    R.id.flContainerHome
                )
            }

            R.id.txtShowAllOrder -> {
                showAll = true
                txtShowAllOrder.visibility = View.GONE
                txtSubTitle.text =
                    (activity as AppCompatActivity).resources.getString(R.string.order_all)
                setAdapter()
                //pagination
                getOrderWithPaging()
            }

            R.id.imgLogo -> {
//                (requireActivity() as AppCompatActivity).hideKeyboard()
                if (activity is HomeActivity) {
                    (requireActivity() as AppCompatActivity).pushFragment(
                        HomeFragment(),
                        false,
                        true,
                        false,
                        R.id.flContainerHome
                    )
                } else {
                    requireActivity().startActivity(
                        Intent(
                            requireActivity(),
                            HomeActivity::class.java
                        )
                    )
                    requireActivity().finishAffinity()
                }
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

            /* R.id.txtClearFilters -> {
                 (requireActivity() as AppCompatActivity).openAlertDialogWithTwoClick(
                     title = getString(R.string.app_name),
                     message = getString(R.string.clear_filter_confirmation_message),
                     positiveButton = getString(android.R.string.ok),
                     nagetiveButton = getString(android.R.string.cancel),
                     onDialogButtonClick = object : OnDialogButtonClick {
                         override fun onPositiveButtonClick() {

                             orderViewModel?.data?.put(UseCaseConstants.ORDER_STATUS, "")
                             orderViewModel?.data?.put(UseCaseConstants.DELIVERY_STATUS, "")
                             orderViewModel?.data?.put(UseCaseConstants.PRICE_MIN, "")
                             orderViewModel?.data?.put(UseCaseConstants.PRICE_MAX, "")
                             orderViewModel?.data?.put(UseCaseConstants.ADDRESS, "")
                             orderViewModel?.data?.put(UseCaseConstants.SORT_BY, "")

                             if (orderViewModel?.orderStatusLiveData?.value != null && orderViewModel?.orderStatusLiveData?.value!!.size > 0) {
                                 for (i in 0 until orderViewModel?.orderStatusLiveData?.value!!.size) {
                                     orderViewModel?.orderStatusLiveData?.value!![i].checked =
                                         false
                                 }
                             }


                             pageNo = 1
                             showAll = false
                             orderViewModel?.title = getString(R.string.shop_all)
                             orderViewModel?.numberOfFilters = 0
                             orderViewModel?.filterAvailable = false
                             orderViewModel!!._orderLiveData.value = null
                             orders.clear()

                             Utility.showProgress(requireContext(), "", false)
                             orderViewModel?.callGetOrders()

                             txtNumberOfFilters.visibility = View.GONE
                             txtClearFilters.visibility = View.GONE
                             txtShowAllOrder.visibility = View.VISIBLE
                             txtSubTitle.visibility = View.VISIBLE
                         }

                         override fun onNegativeButtonClick() {
                         }
                     })
             }*/
        }
    }

    private fun getOrderWithPaging() {
        listOrder.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?

                if (!isLoad) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == orders.size - 1) {
                        //bottom of list!
                        pageNo++

                        if (orderViewModel?._isNextPageUrl?.value != null)
                            if (orderViewModel?._isNextPageUrl?.value!!)
                                orderViewModel?.callGetAllOrdersWithPaging("" + pageNo)!!

                        Handler().postDelayed({
                            isLoad = !orderViewModel!!._isNextPageUrl.value!!
                        }, 500)

                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        orderViewModel?.data?.put(UseCaseConstants.DELIVERY_STATUS, "")
        orderViewModel?.data?.put(UseCaseConstants.ORDER_STATUS, "")
        orderViewModel?.data?.put(UseCaseConstants.PRICE_MIN, "")
        orderViewModel?.data?.put(UseCaseConstants.PRICE_MAX, "")
        orderViewModel?.data?.put(UseCaseConstants.ADDRESS, "")
        orderViewModel?.data?.put(UseCaseConstants.SORT_BY, "")
        orderViewModel?.data?.put(UseCaseConstants.PER_PAGE, "")
        orderViewModel?.data?.put(UseCaseConstants.DELIVERY_DATE_FROM, "")
        orderViewModel?.filterAvailable = false
    }
}
